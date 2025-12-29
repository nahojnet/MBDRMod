package com.mbdrmod.delivery.ui.screens

import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.mbdrmod.delivery.ui.screens.forms.FormContainerScreen
import com.mbdrmod.delivery.ui.screens.home.HomeScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainScreen(
    viewModel: DeliveryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val deliveries by viewModel.deliveries.collectAsState()
    val currentDelivery by viewModel.currentDelivery.collectAsState()
    val currentFormIndex by viewModel.currentFormIndex.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    // Handle send results
    LaunchedEffect(Unit) {
        viewModel.sendResult.collectLatest { result ->
            result.fold(
                onSuccess = {
                    Toast.makeText(context, "Données envoyées avec succès", Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Toast.makeText(
                        context,
                        "Échec de l'envoi: ${error.message}. Nouvelle tentative programmée.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
        }
    }

    if (isEditing) {
        FormContainerScreen(
            currentFormIndex = currentFormIndex,
            delivery = currentDelivery,
            config = viewModel.config,
            onUpdate = { updatedDelivery ->
                viewModel.updateDelivery { updatedDelivery }
            },
            onNextForm = { viewModel.nextForm() },
            onPreviousForm = { viewModel.previousForm() },
            onAddAnomaly = { viewModel.addAnomaly() },
            onUpdateAnomaly = { id, update -> viewModel.updateAnomaly(id, update) },
            onRemoveAnomaly = { id -> viewModel.removeAnomaly(id) },
            onFinish = { viewModel.finishAndSend() }
        )
    } else {
        HomeScreen(
            deliveries = deliveries,
            onNewDelivery = { viewModel.startNewDelivery() },
            onResendDelivery = { delivery -> viewModel.resendDelivery(delivery) },
            onDeleteDelivery = { delivery -> viewModel.deleteDelivery(delivery.id) },
            onEditDelivery = { delivery -> viewModel.editDelivery(delivery) }
        )
    }
}
