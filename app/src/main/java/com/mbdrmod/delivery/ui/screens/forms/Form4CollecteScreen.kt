package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
    var showErrors by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Validation functions
    fun isPlasticFilmsValid() = !requiredFields.plasticFilms || delivery.plasticFilms != null
    fun isCardboardValid() = !requiredFields.cardboard || delivery.cardboard != null
    fun isAluminumValid() = !requiredFields.aluminum || delivery.aluminum != null

    fun validateAndProceed() {
        showErrors = true
        if (isPlasticFilmsValid() && isCardboardValid() && isAluminumValid()) {
            onNext()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Collecte") },
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
            item {
                FormCard {
                    SectionHeader("Collecte")

                    FormNumberField(
                        label = "Films Plastiques",
                        value = delivery.plasticFilms,
                        onValueChange = { onUpdate(delivery.copy(plasticFilms = it)) },
                        isRequired = requiredFields.plasticFilms,
                        isError = showErrors && !isPlasticFilmsValid()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FormNumberField(
                        label = "Cartons Collecte",
                        value = delivery.cardboard,
                        onValueChange = { onUpdate(delivery.copy(cardboard = it)) },
                        isRequired = requiredFields.cardboard,
                        isError = showErrors && !isCardboardValid()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FormNumberField(
                        label = "Aluminium",
                        value = delivery.aluminum,
                        onValueChange = { onUpdate(delivery.copy(aluminum = it)) },
                        isRequired = requiredFields.aluminum,
                        isError = showErrors && !isAluminumValid()
                    )
                }
            }
        }
    }
}
