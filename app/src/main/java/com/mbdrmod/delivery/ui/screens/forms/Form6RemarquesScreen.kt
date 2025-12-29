package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mbdrmod.delivery.data.model.DeliveryData
import com.mbdrmod.delivery.data.model.Form6RequiredFields
import com.mbdrmod.delivery.ui.components.*
import com.mbdrmod.delivery.ui.theme.Error

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form6RemarquesScreen(
    delivery: DeliveryData,
    requiredFields: Form6RequiredFields,
    onUpdate: (DeliveryData) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showErrors by remember { mutableStateOf(false) }

    fun isRemarksValid() = !requiredFields.remarks || delivery.remarks.isNotBlank()

    fun validateAndProceed() {
        showErrors = true
        if (isRemarksValid()) {
            onNext()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Remarques") },
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
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (requiredFields.remarks) "Remarques *" else "Remarques",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = delivery.remarks,
                        onValueChange = { onUpdate(delivery.copy(remarks = it)) },
                        modifier = Modifier.fillMaxSize(),
                        placeholder = { Text("Saisissez vos remarques ici...") },
                        maxLines = Int.MAX_VALUE,
                        isError = showErrors && !isRemarksValid()
                    )

                    if (showErrors && !isRemarksValid()) {
                        Text(
                            text = "Ce champ est obligatoire",
                            color = Error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }
            }

            // Navigation
            FormNavigationButtons(
                onPrevious = onPrevious,
                onNext = { validateAndProceed() }
            )
        }
    }
}
