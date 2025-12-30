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
import com.mbdrmod.delivery.data.model.Form3RequiredFields
import com.mbdrmod.delivery.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form3SupportsScreen(
    delivery: DeliveryData,
    requiredFields: Form3RequiredFields,
    onUpdate: (DeliveryData) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showErrors by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Validation functions
    fun isDoliesDeliveredValid() = !requiredFields.doliesDelivered || delivery.doliesDelivered != null
    fun isDoliesReturnedValid() = !requiredFields.doliesReturned || delivery.doliesReturned != null
    fun isHalfPalDeliveredValid() = !requiredFields.halfPalDelivered || delivery.halfPalDelivered != null
    fun isHalfPalReturnedValid() = !requiredFields.halfPalReturned || delivery.halfPalReturned != null
    fun isEuropeDeliveredValid() = !requiredFields.europeDelivered || delivery.europeDelivered != null
    fun isEuropeReturnedValid() = !requiredFields.europeReturned || delivery.europeReturned != null
    fun isOthersDeliveredValid() = !requiredFields.othersDelivered || delivery.othersDelivered != null
    fun isOthersReturnedValid() = !requiredFields.othersReturned || delivery.othersReturned != null
    fun isPanieresDeliveredValid() = !requiredFields.panieresDelivered || delivery.panieresDelivered != null
    fun isPanieresReturnedValid() = !requiredFields.panieresReturned || delivery.panieresReturned != null
    fun isMegabibDeliveredValid() = !requiredFields.megabibDelivered || delivery.megabibDelivered != null
    fun isMegabibReturnedValid() = !requiredFields.megabibReturned || delivery.megabibReturned != null

    fun validateAndProceed() {
        showErrors = true
        if (isDoliesDeliveredValid() && isDoliesReturnedValid() &&
            isHalfPalDeliveredValid() && isHalfPalReturnedValid() &&
            isEuropeDeliveredValid() && isEuropeReturnedValid() &&
            isOthersDeliveredValid() && isOthersReturnedValid() &&
            isPanieresDeliveredValid() && isPanieresReturnedValid() &&
            isMegabibDeliveredValid() && isMegabibReturnedValid()) {
            onNext()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supports de livraison") },
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
            // Dolies
            item {
                SupportRow(
                    title = "Dolies",
                    deliveredValue = delivery.doliesDelivered,
                    returnedValue = delivery.doliesReturned,
                    onDeliveredChange = { onUpdate(delivery.copy(doliesDelivered = it)) },
                    onReturnedChange = { onUpdate(delivery.copy(doliesReturned = it)) },
                    deliveredRequired = requiredFields.doliesDelivered,
                    returnedRequired = requiredFields.doliesReturned,
                    deliveredError = showErrors && !isDoliesDeliveredValid(),
                    returnedError = showErrors && !isDoliesReturnedValid()
                )
            }

            // 1/2 PAL
            item {
                SupportRow(
                    title = "1/2 PAL",
                    deliveredValue = delivery.halfPalDelivered,
                    returnedValue = delivery.halfPalReturned,
                    onDeliveredChange = { onUpdate(delivery.copy(halfPalDelivered = it)) },
                    onReturnedChange = { onUpdate(delivery.copy(halfPalReturned = it)) },
                    deliveredRequired = requiredFields.halfPalDelivered,
                    returnedRequired = requiredFields.halfPalReturned,
                    deliveredError = showErrors && !isHalfPalDeliveredValid(),
                    returnedError = showErrors && !isHalfPalReturnedValid()
                )
            }

            // Europe
            item {
                SupportRow(
                    title = "Europe",
                    deliveredValue = delivery.europeDelivered,
                    returnedValue = delivery.europeReturned,
                    onDeliveredChange = { onUpdate(delivery.copy(europeDelivered = it)) },
                    onReturnedChange = { onUpdate(delivery.copy(europeReturned = it)) },
                    deliveredRequired = requiredFields.europeDelivered,
                    returnedRequired = requiredFields.europeReturned,
                    deliveredError = showErrors && !isEuropeDeliveredValid(),
                    returnedError = showErrors && !isEuropeReturnedValid()
                )
            }

            // Autres
            item {
                SupportRow(
                    title = "Autres",
                    deliveredValue = delivery.othersDelivered,
                    returnedValue = delivery.othersReturned,
                    onDeliveredChange = { onUpdate(delivery.copy(othersDelivered = it)) },
                    onReturnedChange = { onUpdate(delivery.copy(othersReturned = it)) },
                    deliveredRequired = requiredFields.othersDelivered,
                    returnedRequired = requiredFields.othersReturned,
                    deliveredError = showErrors && !isOthersDeliveredValid(),
                    returnedError = showErrors && !isOthersReturnedValid()
                )
            }

            // Panières
            item {
                SupportRow(
                    title = "Panières",
                    deliveredValue = delivery.panieresDelivered,
                    returnedValue = delivery.panieresReturned,
                    onDeliveredChange = { onUpdate(delivery.copy(panieresDelivered = it)) },
                    onReturnedChange = { onUpdate(delivery.copy(panieresReturned = it)) },
                    deliveredRequired = requiredFields.panieresDelivered,
                    returnedRequired = requiredFields.panieresReturned,
                    deliveredError = showErrors && !isPanieresDeliveredValid(),
                    returnedError = showErrors && !isPanieresReturnedValid()
                )
            }

            // MegaBib
            item {
                SupportRow(
                    title = "MegaBib",
                    deliveredValue = delivery.megabibDelivered,
                    returnedValue = delivery.megabibReturned,
                    onDeliveredChange = { onUpdate(delivery.copy(megabibDelivered = it)) },
                    onReturnedChange = { onUpdate(delivery.copy(megabibReturned = it)) },
                    deliveredRequired = requiredFields.megabibDelivered,
                    returnedRequired = requiredFields.megabibReturned,
                    deliveredError = showErrors && !isMegabibDeliveredValid(),
                    returnedError = showErrors && !isMegabibReturnedValid()
                )
            }
        }
    }
}

@Composable
private fun SupportRow(
    title: String,
    deliveredValue: Int?,
    returnedValue: Int?,
    onDeliveredChange: (Int?) -> Unit,
    onReturnedChange: (Int?) -> Unit,
    deliveredRequired: Boolean = false,
    returnedRequired: Boolean = false,
    deliveredError: Boolean = false,
    returnedError: Boolean = false
) {
    FormCard {
        SectionHeader(title)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FormNumberField(
                label = "Livrées",
                value = deliveredValue,
                onValueChange = onDeliveredChange,
                modifier = Modifier.weight(1f),
                isRequired = deliveredRequired,
                isError = deliveredError
            )

            FormNumberField(
                label = "Rendues",
                value = returnedValue,
                onValueChange = onReturnedChange,
                modifier = Modifier.weight(1f),
                isRequired = returnedRequired,
                isError = returnedError
            )
        }
    }
}
