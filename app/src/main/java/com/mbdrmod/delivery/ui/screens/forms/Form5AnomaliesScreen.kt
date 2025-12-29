package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mbdrmod.delivery.data.model.Anomaly
import com.mbdrmod.delivery.data.model.DeliveryData
import com.mbdrmod.delivery.data.model.Form5RequiredFields
import com.mbdrmod.delivery.ui.components.*
import com.mbdrmod.delivery.ui.theme.Error

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form5AnomaliesScreen(
    delivery: DeliveryData,
    requiredFields: Form5RequiredFields,
    onUpdate: (DeliveryData) -> Unit,
    onAddAnomaly: () -> Unit,
    onUpdateAnomaly: (Long, (Anomaly) -> Anomaly) -> Unit,
    onRemoveAnomaly: (Long) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Anomalies") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddAnomaly,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter une anomalie")
            }
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
            if (delivery.anomalies.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Aucune anomalie",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Appuyez sur + pour ajouter une anomalie",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            } else {
                itemsIndexed(delivery.anomalies, key = { _, anomaly -> anomaly.id }) { index, anomaly ->
                    AnomalyCard(
                        anomaly = anomaly,
                        index = index + 1,
                        requiredFields = requiredFields,
                        onUpdate = { update -> onUpdateAnomaly(anomaly.id, update) },
                        onRemove = { onRemoveAnomaly(anomaly.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }

            // Navigation
            item {
                FormNavigationButtons(
                    onPrevious = onPrevious,
                    onNext = onNext
                )
            }
        }
    }
}

@Composable
private fun AnomalyCard(
    anomaly: Anomaly,
    index: Int,
    requiredFields: Form5RequiredFields,
    onUpdate: ((Anomaly) -> Anomaly) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Anomalie #$index",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer",
                        tint = Error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            FormTextField(
                label = "WRIN",
                value = anomaly.wrin,
                onValueChange = { newValue ->
                    onUpdate { it.copy(wrin = newValue) }
                },
                isRequired = requiredFields.wrin
            )

            Spacer(modifier = Modifier.height(12.dp))

            FormTextField(
                label = "Description",
                value = anomaly.description,
                onValueChange = { newValue ->
                    onUpdate { it.copy(description = newValue) }
                },
                isRequired = requiredFields.description
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormNumberField(
                    label = "Quantité",
                    value = anomaly.quantity,
                    onValueChange = { newValue ->
                        onUpdate { it.copy(quantity = newValue) }
                    },
                    modifier = Modifier.weight(1f),
                    isRequired = requiredFields.quantity
                )

                FormNumberField(
                    label = "Manquant",
                    value = anomaly.missing,
                    onValueChange = { newValue ->
                        onUpdate { it.copy(missing = newValue) }
                    },
                    modifier = Modifier.weight(1f),
                    isRequired = requiredFields.missing
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FormNumberField(
                    label = "Refus",
                    value = anomaly.refused,
                    onValueChange = { newValue ->
                        onUpdate { it.copy(refused = newValue) }
                    },
                    modifier = Modifier.weight(1f),
                    isRequired = requiredFields.refused
                )

                FormNumberField(
                    label = "Excédent",
                    value = anomaly.excess,
                    onValueChange = { newValue ->
                        onUpdate { it.copy(excess = newValue) }
                    },
                    modifier = Modifier.weight(1f),
                    isRequired = requiredFields.excess
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FormTextField(
                label = "Motif",
                value = anomaly.reason,
                onValueChange = { newValue ->
                    onUpdate { it.copy(reason = newValue) }
                },
                isRequired = requiredFields.reason
            )
        }
    }
}
