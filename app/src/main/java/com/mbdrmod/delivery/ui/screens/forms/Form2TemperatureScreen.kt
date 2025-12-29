package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mbdrmod.delivery.data.model.DeliveryData
import com.mbdrmod.delivery.data.model.Form2RequiredFields
import com.mbdrmod.delivery.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form2TemperatureScreen(
    delivery: DeliveryData,
    requiredFields: Form2RequiredFields,
    onUpdate: (DeliveryData) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val methodOptions = listOf(
        "inertia_probe" to "Sondes à inertie",
        "manual_control" to "Contrôle manuel"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Température") },
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
            // Section Véhicule
            FormCard {
                SectionHeader("Véhicule")

                FormTemperatureField(
                    label = "Surgelé",
                    value = delivery.vehicleFrozenTemp,
                    onValueChange = { onUpdate(delivery.copy(vehicleFrozenTemp = it)) },
                    isRequired = requiredFields.vehicleFrozen
                )

                Spacer(modifier = Modifier.height(12.dp))

                FormTemperatureField(
                    label = "Frais",
                    value = delivery.vehicleFreshTemp,
                    onValueChange = { onUpdate(delivery.copy(vehicleFreshTemp = it)) },
                    isRequired = requiredFields.vehicleFresh
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section Produits
            FormCard {
                SectionHeader("Produits")

                // Surgelé
                FormTemperatureField(
                    label = "Surgelé",
                    value = delivery.productFrozenTemp,
                    onValueChange = { onUpdate(delivery.copy(productFrozenTemp = it)) },
                    isRequired = requiredFields.productFrozen
                )

                Spacer(modifier = Modifier.height(8.dp))

                FormRadioGroup(
                    label = "Méthode de contrôle (Surgelé)",
                    options = methodOptions,
                    selectedValue = delivery.productFrozenMethod,
                    onValueChange = { onUpdate(delivery.copy(productFrozenMethod = it)) },
                    isRequired = requiredFields.productFrozenMethod
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Frais
                FormTemperatureField(
                    label = "Frais",
                    value = delivery.productFreshTemp,
                    onValueChange = { onUpdate(delivery.copy(productFreshTemp = it)) },
                    isRequired = requiredFields.productFresh
                )

                Spacer(modifier = Modifier.height(8.dp))

                FormRadioGroup(
                    label = "Méthode de contrôle (Frais)",
                    options = methodOptions,
                    selectedValue = delivery.productFreshMethod,
                    onValueChange = { onUpdate(delivery.copy(productFreshMethod = it)) },
                    isRequired = requiredFields.productFreshMethod
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation
            FormNavigationButtons(
                onPrevious = onPrevious,
                onNext = onNext
            )
        }
    }
}
