package com.mbdrmod.delivery.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mbdrmod.delivery.data.model.DeliveryData
import com.mbdrmod.delivery.data.model.SendStatus
import com.mbdrmod.delivery.ui.theme.Error
import com.mbdrmod.delivery.ui.theme.Success
import com.mbdrmod.delivery.ui.theme.Warning
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    deliveries: List<DeliveryData>,
    onNewDelivery: () -> Unit,
    onResendDelivery: (DeliveryData) -> Unit,
    onDeleteDelivery: (DeliveryData) -> Unit,
    onEditDelivery: (DeliveryData) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Livraisons") },
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
            // New delivery button
            Button(
                onClick = onNewDelivery,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nouvelle saisie", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Deliveries list
            if (deliveries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune livraison enregistrée",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(deliveries) { delivery ->
                        DeliveryListItem(
                            delivery = delivery,
                            onResend = { onResendDelivery(delivery) },
                            onDelete = { onDeleteDelivery(delivery) },
                            onClick = { onEditDelivery(delivery) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryListItem(
    delivery: DeliveryData,
    onResend: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val createdDate = dateFormat.format(Date(delivery.createdAt))

    var showResendConfirmation by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Resend confirmation dialog (only for already sent items)
    if (showResendConfirmation) {
        AlertDialog(
            onDismissRequest = { showResendConfirmation = false },
            title = { Text("Confirmer le renvoi") },
            text = { Text("Cette livraison a déjà été envoyée. Voulez-vous vraiment la renvoyer ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResendConfirmation = false
                        onResend()
                    }
                ) {
                    Text("Renvoyer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResendConfirmation = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Delete confirmation dialog (only for not sent items - error or pending)
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirmer la suppression") },
            text = { Text("Cette livraison n'a pas été envoyée. Voulez-vous vraiment la supprimer ? Les données seront perdues.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    }
                ) {
                    Text("Supprimer", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status icon
            val (icon, iconColor) = when (delivery.sendStatus) {
                SendStatus.DRAFT -> Icons.Default.Edit to MaterialTheme.colorScheme.outline
                SendStatus.SENT -> Icons.Default.CheckCircle to Success
                SendStatus.FAILED -> Icons.Default.Error to Error
                SendStatus.PENDING -> Icons.Default.Schedule to Warning
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Delivery info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Client: ${delivery.clientNumber.ifEmpty { "-" }}",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Tour: ${delivery.tourNumber ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Livraison: ${delivery.deliveryDate.ifEmpty { "-" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Resend button (hidden for drafts)
            if (delivery.sendStatus != SendStatus.DRAFT) {
                IconButton(
                    onClick = {
                        if (delivery.sendStatus == SendStatus.SENT) {
                            showResendConfirmation = true
                        } else {
                            onResend()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Renvoyer",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Delete button
            IconButton(
                onClick = {
                    if (delivery.sendStatus != SendStatus.SENT) {
                        // Not sent yet (draft, pending, failed) - ask for confirmation
                        showDeleteConfirmation = true
                    } else {
                        // Already sent - delete directly
                        onDelete()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = Error
                )
            }
        }
    }
}
