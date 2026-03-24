package com.mbdrmod.delivery.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mbdrmod.delivery.data.model.Anomaly
import com.mbdrmod.delivery.data.model.AppConfig
import com.mbdrmod.delivery.data.model.DeliveryData
import com.mbdrmod.delivery.data.model.SendStatus
import com.mbdrmod.delivery.data.repository.DeliveryRepository
import com.mbdrmod.delivery.util.ConfigManager
import com.mbdrmod.delivery.worker.WebhookRetryWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val repository: DeliveryRepository,
    private val configManager: ConfigManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _currentDelivery = MutableStateFlow(createNewDelivery())
    val currentDelivery: StateFlow<DeliveryData> = _currentDelivery.asStateFlow()

    private val _currentFormIndex = MutableStateFlow(0)
    val currentFormIndex: StateFlow<Int> = _currentFormIndex.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _sendResult = MutableSharedFlow<Result<Unit>>()
    val sendResult: SharedFlow<Result<Unit>> = _sendResult.asSharedFlow()

    val deliveries: StateFlow<List<DeliveryData>> = repository.getAllDeliveries()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val config: AppConfig
        get() = configManager.getConfig()

    init {
        // Schedule periodic webhook retry work
        val retryInterval = config.webhook.retryIntervalMinutes
        WebhookRetryWorker.schedulePeriodicWork(context, retryInterval)
    }

    private fun createNewDelivery(): DeliveryData {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        return DeliveryData(
            deliveryDate = dateFormat.format(Date()),
            sendStatus = SendStatus.DRAFT
        )
    }

    fun startNewDelivery() {
        _currentDelivery.value = createNewDelivery()
        _currentFormIndex.value = -1 // Start at site selection screen
        _isEditing.value = true
    }

    fun editDelivery(delivery: DeliveryData) {
        _currentDelivery.value = delivery
        _currentFormIndex.value = 0
        _isEditing.value = true
    }

    fun cancelEditing() {
        // Save as draft before leaving
        saveDraft()
        _isEditing.value = false
        _currentFormIndex.value = 0
    }

    fun goHome() {
        // Save as draft and return to home screen
        saveDraft()
        _isEditing.value = false
        _currentFormIndex.value = 0
    }

    fun updateDelivery(update: (DeliveryData) -> DeliveryData) {
        _currentDelivery.value = update(_currentDelivery.value)
        // Save draft on every value change
        saveDraft()
    }

    fun nextForm() {
        // Save draft when navigating between forms
        saveDraft()
        if (_currentFormIndex.value < 7) { // 8 forms (0-7) + site selection (-1)
            _currentFormIndex.value++
        }
    }

    fun previousForm() {
        // Save draft when navigating between forms
        saveDraft()
        if (_currentFormIndex.value > 0) {
            _currentFormIndex.value--
        }
    }

    fun goToForm(index: Int) {
        if (index in 0..7) {
            _currentFormIndex.value = index
        }
    }

    // Save current delivery as draft
    private fun saveDraft() {
        viewModelScope.launch {
            val delivery = _currentDelivery.value
            // Only save if there's some data entered
            if (hasAnyData(delivery)) {
                if (delivery.id == 0L) {
                    // New delivery - save and update current with ID
                    val id = repository.saveDelivery(delivery.copy(sendStatus = SendStatus.DRAFT))
                    _currentDelivery.value = delivery.copy(id = id, sendStatus = SendStatus.DRAFT)
                } else {
                    // Existing delivery - update (keep current status if not SENT)
                    val statusToKeep = if (delivery.sendStatus == SendStatus.SENT) {
                        SendStatus.SENT
                    } else {
                        SendStatus.DRAFT
                    }
                    repository.updateDelivery(delivery.copy(sendStatus = statusToKeep))
                }
            }
        }
    }

    // Check if delivery has any meaningful data
    private fun hasAnyData(delivery: DeliveryData): Boolean {
        return delivery.deliverySite.isNotBlank() ||
                delivery.clientNumber.isNotBlank() ||
                delivery.tourNumber != null ||
                delivery.expectedArrivalTime.isNotBlank() ||
                delivery.actualArrivalTime.isNotBlank() ||
                delivery.deliveryStartTime.isNotBlank() ||
                delivery.supports != null ||
                delivery.weightKg != null ||
                delivery.packages != null ||
                delivery.volumeM3 != null ||
                delivery.vehicleFrozenTemp != null ||
                delivery.vehicleFreshTemp != null ||
                delivery.productFrozenTemp != null ||
                delivery.productFreshTemp != null ||
                delivery.ssccNumbers.isNotBlank() ||
                delivery.anomalies.isNotEmpty() ||
                delivery.remarks.isNotBlank() ||
                delivery.driverName.isNotBlank() ||
                delivery.managerName.isNotBlank()
    }

    // Anomaly management
    fun addAnomaly() {
        val newAnomaly = Anomaly(id = System.currentTimeMillis())
        updateDelivery { delivery ->
            delivery.copy(anomalies = delivery.anomalies + newAnomaly)
        }
    }

    fun updateAnomaly(anomalyId: Long, update: (Anomaly) -> Anomaly) {
        updateDelivery { delivery ->
            delivery.copy(
                anomalies = delivery.anomalies.map { anomaly ->
                    if (anomaly.id == anomalyId) update(anomaly) else anomaly
                }
            )
        }
    }

    fun removeAnomaly(anomalyId: Long) {
        updateDelivery { delivery ->
            delivery.copy(anomalies = delivery.anomalies.filter { it.id != anomalyId })
        }
    }

    fun finishAndSend() {
        viewModelScope.launch {
            val delivery = _currentDelivery.value.copy(
                sendStatus = SendStatus.PENDING
            )

            // Save to database
            val id = if (delivery.id == 0L) {
                repository.saveDelivery(delivery)
            } else {
                repository.updateDelivery(delivery)
                delivery.id
            }

            // Get saved delivery with ID
            val savedDelivery = repository.getDeliveryById(id)
            if (savedDelivery != null) {
                // Try to send
                val result = repository.sendDelivery(savedDelivery)
                _sendResult.emit(result)

                if (result.isFailure) {
                    // Schedule retry
                    WebhookRetryWorker.scheduleOneTimeWork(context)
                }
            }

            // Reset state
            _isEditing.value = false
            _currentFormIndex.value = 0
            _currentDelivery.value = createNewDelivery()
        }
    }

    fun resendDelivery(delivery: DeliveryData) {
        viewModelScope.launch {
            val result = repository.sendDelivery(delivery)
            _sendResult.emit(result)
        }
    }

    fun deleteDelivery(deliveryId: Long) {
        viewModelScope.launch {
            repository.deleteDelivery(deliveryId)
        }
    }
}
