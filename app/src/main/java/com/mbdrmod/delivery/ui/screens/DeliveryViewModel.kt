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
            deliveryDate = dateFormat.format(Date())
        )
    }

    fun startNewDelivery() {
        _currentDelivery.value = createNewDelivery()
        _currentFormIndex.value = 0
        _isEditing.value = true
    }

    fun editDelivery(delivery: DeliveryData) {
        _currentDelivery.value = delivery
        _currentFormIndex.value = 0
        _isEditing.value = true
    }

    fun cancelEditing() {
        _isEditing.value = false
        _currentFormIndex.value = 0
    }

    fun updateDelivery(update: (DeliveryData) -> DeliveryData) {
        _currentDelivery.value = update(_currentDelivery.value)
    }

    fun nextForm() {
        if (_currentFormIndex.value < 6) { // 7 forms (0-6)
            _currentFormIndex.value++
        }
    }

    fun previousForm() {
        if (_currentFormIndex.value > 0) {
            _currentFormIndex.value--
        }
    }

    fun goToForm(index: Int) {
        if (index in 0..6) {
            _currentFormIndex.value = index
        }
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
