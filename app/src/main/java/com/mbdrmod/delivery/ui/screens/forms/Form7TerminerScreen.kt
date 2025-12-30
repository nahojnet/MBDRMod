package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.gestures.ScrollableDefaults
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
import com.mbdrmod.delivery.data.model.Form7RequiredFields
import com.mbdrmod.delivery.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form7TerminerScreen(
    delivery: DeliveryData,
    requiredFields: Form7RequiredFields,
    onUpdate: (DeliveryData) -> Unit,
    onPrevious: () -> Unit,
    onFinish: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showErrors by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Validation functions
    fun isDeliveryEndTimeValid() = !requiredFields.deliveryEndTime || delivery.deliveryEndTime.isNotBlank()
    fun isDepartureTimeValid() = !requiredFields.departureTime || delivery.departureTime.isNotBlank()
    fun isDriverNameValid() = !requiredFields.driverName || delivery.driverName.isNotBlank()
    fun isManagerNameValid() = !requiredFields.managerName || delivery.managerName.isNotBlank()

    fun validateAndFinish() {
        showErrors = true
        if (isDeliveryEndTimeValid() && isDepartureTimeValid() &&
            isDriverNameValid() && isManagerNameValid()) {
            onFinish()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terminer") },
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
                    onFinish = { validateAndFinish() },
                    isLastForm = true,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            flingBehavior = ScrollableDefaults.flingBehavior(),
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                FormCard {
                    SectionHeader("Fin de livraison")

                    FormTimeField(
                        label = "Heure de fin de livraison",
                        value = delivery.deliveryEndTime,
                        onValueChange = { onUpdate(delivery.copy(deliveryEndTime = it)) },
                        isRequired = requiredFields.deliveryEndTime,
                        isError = showErrors && !isDeliveryEndTimeValid()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FormTimeField(
                        label = "Heure de départ",
                        value = delivery.departureTime,
                        onValueChange = { onUpdate(delivery.copy(departureTime = it)) },
                        isRequired = requiredFields.departureTime,
                        isError = showErrors && !isDepartureTimeValid()
                    )
                }
            }

            item {
                FormCard {
                    SectionHeader("Signatures")

                    FormTextField(
                        label = "Nom du conducteur",
                        value = delivery.driverName,
                        onValueChange = { onUpdate(delivery.copy(driverName = it)) },
                        isRequired = requiredFields.driverName,
                        isError = showErrors && !isDriverNameValid()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FormTextField(
                        label = "Nom du manager",
                        value = delivery.managerName,
                        onValueChange = { onUpdate(delivery.copy(managerName = it)) },
                        isRequired = requiredFields.managerName,
                        isError = showErrors && !isManagerNameValid()
                    )
                }
            }
        }
    }
}
