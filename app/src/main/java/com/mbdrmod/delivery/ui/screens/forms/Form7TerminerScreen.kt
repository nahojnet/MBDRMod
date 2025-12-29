package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terminer") },
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
            FormCard {
                SectionHeader("Fin de livraison")

                FormTimeField(
                    label = "Heure de fin de livraison",
                    value = delivery.deliveryEndTime,
                    onValueChange = { onUpdate(delivery.copy(deliveryEndTime = it)) },
                    isRequired = requiredFields.deliveryEndTime
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormTimeField(
                    label = "Heure de départ",
                    value = delivery.departureTime,
                    onValueChange = { onUpdate(delivery.copy(departureTime = it)) },
                    isRequired = requiredFields.departureTime
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            FormCard {
                SectionHeader("Signatures")

                FormTextField(
                    label = "Nom du conducteur",
                    value = delivery.driverName,
                    onValueChange = { onUpdate(delivery.copy(driverName = it)) },
                    isRequired = requiredFields.driverName
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormTextField(
                    label = "Nom du manager",
                    value = delivery.managerName,
                    onValueChange = { onUpdate(delivery.copy(managerName = it)) },
                    isRequired = requiredFields.managerName
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation with finish button
            FormNavigationButtons(
                onPrevious = onPrevious,
                onFinish = onFinish,
                isLastForm = true
            )
        }
    }
}
