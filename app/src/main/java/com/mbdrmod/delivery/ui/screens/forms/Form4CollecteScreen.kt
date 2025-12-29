package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mbdrmod.delivery.data.model.DeliveryData
import com.mbdrmod.delivery.data.model.Form4RequiredFields
import com.mbdrmod.delivery.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form4CollecteScreen(
    delivery: DeliveryData,
    requiredFields: Form4RequiredFields,
    onUpdate: (DeliveryData) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Collecte") },
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
                SectionHeader("Collecte")

                FormNumberField(
                    label = "Films Plastiques",
                    value = delivery.plasticFilms,
                    onValueChange = { onUpdate(delivery.copy(plasticFilms = it)) },
                    isRequired = requiredFields.plasticFilms
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormNumberField(
                    label = "Cartons Collecte",
                    value = delivery.cardboard,
                    onValueChange = { onUpdate(delivery.copy(cardboard = it)) },
                    isRequired = requiredFields.cardboard
                )

                Spacer(modifier = Modifier.height(16.dp))

                FormNumberField(
                    label = "Aluminium",
                    value = delivery.aluminum,
                    onValueChange = { onUpdate(delivery.copy(aluminum = it)) },
                    isRequired = requiredFields.aluminum
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
