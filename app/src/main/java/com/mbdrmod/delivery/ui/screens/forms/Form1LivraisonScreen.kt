package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mbdrmod.delivery.data.model.DeliveryData
import com.mbdrmod.delivery.data.model.Form1RequiredFields
import com.mbdrmod.delivery.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form1LivraisonScreen(
    delivery: DeliveryData,
    requiredFields: Form1RequiredFields,
    onUpdate: (DeliveryData) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showErrors by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Validation functions
    fun isDateValid() = !requiredFields.deliveryDate || delivery.deliveryDate.isNotBlank()
    fun isClientNumberValid() = !requiredFields.clientNumber || delivery.clientNumber.isNotBlank()
    fun isTourNumberValid() = !requiredFields.tourNumber || delivery.tourNumber != null
    fun isExpectedArrivalValid() = !requiredFields.expectedArrivalTime || delivery.expectedArrivalTime.isNotBlank()
    fun isActualArrivalValid() = !requiredFields.actualArrivalTime || delivery.actualArrivalTime.isNotBlank()
    fun isDeliveryStartValid() = !requiredFields.deliveryStartTime || delivery.deliveryStartTime.isNotBlank()
    fun isSupportsValid() = !requiredFields.supports || delivery.supports != null
    fun isWeightValid() = !requiredFields.weightKg || delivery.weightKg != null
    fun isPackagesValid() = !requiredFields.packages || delivery.packages != null
    fun isVolumeValid() = !requiredFields.volumeM3 || delivery.volumeM3 != null

    fun validateAndProceed() {
        showErrors = true
        if (isDateValid() && isClientNumberValid() && isTourNumberValid() &&
            isExpectedArrivalValid() && isActualArrivalValid() && isDeliveryStartValid() &&
            isSupportsValid() && isWeightValid() && isPackagesValid() && isVolumeValid()) {
            onNext()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Livraison") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            flingBehavior = ScrollableDefaults.flingBehavior(),
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Client
            item {
                FormCard {
                    SectionHeader("Client")

                    FormDateField(
                        label = "Date de Livraison",
                        value = delivery.deliveryDate,
                        onValueChange = { onUpdate(delivery.copy(deliveryDate = it)) },
                        isRequired = requiredFields.deliveryDate,
                        isError = showErrors && !isDateValid()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormTextField(
                        label = "Numéro de client",
                        value = delivery.clientNumber,
                        onValueChange = { onUpdate(delivery.copy(clientNumber = it)) },
                        isRequired = requiredFields.clientNumber,
                        isError = showErrors && !isClientNumberValid()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormNumberField(
                        label = "Numéro de Tour",
                        value = delivery.tourNumber,
                        onValueChange = { onUpdate(delivery.copy(tourNumber = it)) },
                        isRequired = requiredFields.tourNumber,
                        isError = showErrors && !isTourNumberValid()
                    )
                }
            }

            // Section Horaire
            item {
                FormCard {
                    SectionHeader("Horaire")

                    FormTimeField(
                        label = "Heure prévue d'arrivée",
                        value = delivery.expectedArrivalTime,
                        onValueChange = { onUpdate(delivery.copy(expectedArrivalTime = it)) },
                        isRequired = requiredFields.expectedArrivalTime,
                        isError = showErrors && !isExpectedArrivalValid()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormTimeField(
                        label = "Heure réelle d'arrivée",
                        value = delivery.actualArrivalTime,
                        onValueChange = { onUpdate(delivery.copy(actualArrivalTime = it)) },
                        isRequired = requiredFields.actualArrivalTime,
                        isError = showErrors && !isActualArrivalValid()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormTimeField(
                        label = "Heure de début de Livraison",
                        value = delivery.deliveryStartTime,
                        onValueChange = { onUpdate(delivery.copy(deliveryStartTime = it)) },
                        isRequired = requiredFields.deliveryStartTime,
                        isError = showErrors && !isDeliveryStartValid()
                    )
                }
            }

            // Section Quantité
            item {
                FormCard {
                    SectionHeader("Quantité")

                    FormNumberField(
                        label = "Supports",
                        value = delivery.supports,
                        onValueChange = { onUpdate(delivery.copy(supports = it)) },
                        isRequired = requiredFields.supports,
                        isError = showErrors && !isSupportsValid()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormDecimalField(
                        label = "Poids en Kg",
                        value = delivery.weightKg,
                        onValueChange = { onUpdate(delivery.copy(weightKg = it)) },
                        isRequired = requiredFields.weightKg,
                        isError = showErrors && !isWeightValid()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormNumberField(
                        label = "Colis",
                        value = delivery.packages,
                        onValueChange = { onUpdate(delivery.copy(packages = it)) },
                        isRequired = requiredFields.packages,
                        isError = showErrors && !isPackagesValid()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormDecimalField(
                        label = "Volume en M3",
                        value = delivery.volumeM3,
                        onValueChange = { onUpdate(delivery.copy(volumeM3 = it)) },
                        isRequired = requiredFields.volumeM3,
                        isError = showErrors && !isVolumeValid()
                    )
                }
            }

            // Navigation
            item {
                FormNavigationButtons(
                    onNext = { validateAndProceed() }
                )
            }
        }
    }
}
