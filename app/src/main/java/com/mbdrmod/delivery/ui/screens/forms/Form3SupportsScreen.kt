package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supports de livraison") },
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
            // Dolies
            SupportRow(
                title = "Dolies",
                deliveredValue = delivery.doliesDelivered,
                returnedValue = delivery.doliesReturned,
                onDeliveredChange = { onUpdate(delivery.copy(doliesDelivered = it)) },
                onReturnedChange = { onUpdate(delivery.copy(doliesReturned = it)) },
                deliveredRequired = requiredFields.doliesDelivered,
                returnedRequired = requiredFields.doliesReturned
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1/2 PAL
            SupportRow(
                title = "1/2 PAL",
                deliveredValue = delivery.halfPalDelivered,
                returnedValue = delivery.halfPalReturned,
                onDeliveredChange = { onUpdate(delivery.copy(halfPalDelivered = it)) },
                onReturnedChange = { onUpdate(delivery.copy(halfPalReturned = it)) },
                deliveredRequired = requiredFields.halfPalDelivered,
                returnedRequired = requiredFields.halfPalReturned
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Europe
            SupportRow(
                title = "Europe",
                deliveredValue = delivery.europeDelivered,
                returnedValue = delivery.europeReturned,
                onDeliveredChange = { onUpdate(delivery.copy(europeDelivered = it)) },
                onReturnedChange = { onUpdate(delivery.copy(europeReturned = it)) },
                deliveredRequired = requiredFields.europeDelivered,
                returnedRequired = requiredFields.europeReturned
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Autres
            SupportRow(
                title = "Autres",
                deliveredValue = delivery.othersDelivered,
                returnedValue = delivery.othersReturned,
                onDeliveredChange = { onUpdate(delivery.copy(othersDelivered = it)) },
                onReturnedChange = { onUpdate(delivery.copy(othersReturned = it)) },
                deliveredRequired = requiredFields.othersDelivered,
                returnedRequired = requiredFields.othersReturned
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Panières
            SupportRow(
                title = "Panières",
                deliveredValue = delivery.panieresDelivered,
                returnedValue = delivery.panieresReturned,
                onDeliveredChange = { onUpdate(delivery.copy(panieresDelivered = it)) },
                onReturnedChange = { onUpdate(delivery.copy(panieresReturned = it)) },
                deliveredRequired = requiredFields.panieresDelivered,
                returnedRequired = requiredFields.panieresReturned
            )

            Spacer(modifier = Modifier.height(16.dp))

            // MegaBib
            SupportRow(
                title = "MegaBib",
                deliveredValue = delivery.megabibDelivered,
                returnedValue = delivery.megabibReturned,
                onDeliveredChange = { onUpdate(delivery.copy(megabibDelivered = it)) },
                onReturnedChange = { onUpdate(delivery.copy(megabibReturned = it)) },
                deliveredRequired = requiredFields.megabibDelivered,
                returnedRequired = requiredFields.megabibReturned
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation
            FormNavigationButtons(
                onPrevious = onPrevious,
                onNext = onNext
            )
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
    returnedRequired: Boolean = false
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
                isRequired = deliveredRequired
            )

            FormNumberField(
                label = "Rendues",
                value = returnedValue,
                onValueChange = onReturnedChange,
                modifier = Modifier.weight(1f),
                isRequired = returnedRequired
            )
        }
    }
}
