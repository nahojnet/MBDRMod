package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showErrors by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val methodOptions = listOf(
        "inertia_probe" to "Sondes à inertie",
        "manual_control" to "Contrôle manuel"
    )

    // Validation functions
    fun isVehicleFrozenValid() = !requiredFields.vehicleFrozen || delivery.vehicleFrozenTemp != null
    fun isVehicleFreshValid() = !requiredFields.vehicleFresh || delivery.vehicleFreshTemp != null
    fun isProductFrozenValid() = !requiredFields.productFrozen || delivery.productFrozenTemp != null
    fun isProductFrozenMethodValid() = !requiredFields.productFrozenMethod || delivery.productFrozenMethod.isNotBlank()
    fun isProductFreshValid() = !requiredFields.productFresh || delivery.productFreshTemp != null
    fun isProductFreshMethodValid() = !requiredFields.productFreshMethod || delivery.productFreshMethod.isNotBlank()

    fun validateAndProceed() {
        showErrors = true
        if (isVehicleFrozenValid() && isVehicleFreshValid() &&
            isProductFrozenValid() && isProductFrozenMethodValid() &&
            isProductFreshValid() && isProductFreshMethodValid()) {
            onNext()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Température") },
                navigationIcon = {
                    IconButton(onClick = onGoHome) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Accueil",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp
            ) {
                FormNavigationButtons(
                    onPrevious = onPrevious,
                    onNext = { validateAndProceed() },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Section Véhicule
            item {
                FormCard {
                    SectionHeader("Véhicule")

                    FormTemperatureField(
                        label = "Surgelé",
                        value = delivery.vehicleFrozenTemp,
                        onValueChange = { onUpdate(delivery.copy(vehicleFrozenTemp = it)) },
                        isRequired = requiredFields.vehicleFrozen,
                        isError = showErrors && !isVehicleFrozenValid(),
                        isFrozen = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FormTemperatureField(
                        label = "Frais",
                        value = delivery.vehicleFreshTemp,
                        onValueChange = { onUpdate(delivery.copy(vehicleFreshTemp = it)) },
                        isRequired = requiredFields.vehicleFresh,
                        isError = showErrors && !isVehicleFreshValid(),
                        isFrozen = false
                    )
                }
            }

            // Section Produits
            item {
                FormCard {
                    SectionHeader("Produits")

                    // Surgelé
                    FormTemperatureField(
                        label = "Surgelé",
                        value = delivery.productFrozenTemp,
                        onValueChange = { onUpdate(delivery.copy(productFrozenTemp = it)) },
                        isRequired = requiredFields.productFrozen,
                        isError = showErrors && !isProductFrozenValid(),
                        isFrozen = true
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
                        isRequired = requiredFields.productFresh,
                        isError = showErrors && !isProductFreshValid(),
                        isFrozen = false
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
            }
        }
    }
}
