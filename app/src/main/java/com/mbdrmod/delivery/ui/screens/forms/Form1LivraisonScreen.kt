package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Section Client
            FormCard {
                SectionHeader("Client")

                FormDateField(
                    label = "Date de Livraison",
                    value = delivery.deliveryDate,
                    onValueChange = { onUpdate(delivery.copy(deliveryDate = it)) },
                    isRequired = requiredFields.deliveryDate
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormTextField(
                    label = "Numéro de client",
                    value = delivery.clientNumber,
                    onValueChange = { onUpdate(delivery.copy(clientNumber = it)) },
                    isRequired = requiredFields.clientNumber
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormNumberField(
                    label = "Numéro de Tour",
                    value = delivery.tourNumber,
                    onValueChange = { onUpdate(delivery.copy(tourNumber = it)) },
                    isRequired = requiredFields.tourNumber
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section Horaire
            FormCard {
                SectionHeader("Horaire")

                FormTimeField(
                    label = "Heure prévue d'arrivée",
                    value = delivery.expectedArrivalTime,
                    onValueChange = { onUpdate(delivery.copy(expectedArrivalTime = it)) },
                    isRequired = requiredFields.expectedArrivalTime
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormTimeField(
                    label = "Heure réelle d'arrivée",
                    value = delivery.actualArrivalTime,
                    onValueChange = { onUpdate(delivery.copy(actualArrivalTime = it)) },
                    isRequired = requiredFields.actualArrivalTime
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormTimeField(
                    label = "Heure de début de Livraison",
                    value = delivery.deliveryStartTime,
                    onValueChange = { onUpdate(delivery.copy(deliveryStartTime = it)) },
                    isRequired = requiredFields.deliveryStartTime
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section Quantité
            FormCard {
                SectionHeader("Quantité")

                FormNumberField(
                    label = "Supports",
                    value = delivery.supports,
                    onValueChange = { onUpdate(delivery.copy(supports = it)) },
                    isRequired = requiredFields.supports
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormDecimalField(
                    label = "Poids en Kg",
                    value = delivery.weightKg,
                    onValueChange = { onUpdate(delivery.copy(weightKg = it)) },
                    isRequired = requiredFields.weightKg
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormNumberField(
                    label = "Colis",
                    value = delivery.packages,
                    onValueChange = { onUpdate(delivery.copy(packages = it)) },
                    isRequired = requiredFields.packages
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormDecimalField(
                    label = "Volume en M3",
                    value = delivery.volumeM3,
                    onValueChange = { onUpdate(delivery.copy(volumeM3 = it)) },
                    isRequired = requiredFields.volumeM3
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation
            FormNavigationButtons(
                onNext = onNext
            )
        }
    }
}
