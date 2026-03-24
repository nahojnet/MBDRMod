package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.mbdrmod.delivery.data.model.DeliveryData
import com.mbdrmod.delivery.ui.components.FormNavigationButtons
import com.mbdrmod.delivery.ui.theme.TextSecondary

private const val SSCC_LINE_LENGTH = 18

private fun processSSCCText(newValue: TextFieldValue, oldText: String): TextFieldValue {
    val raw = newValue.text
    val lines = raw.split("\n")
    val processedLines = mutableListOf<String>()
    for (line in lines) {
        if (line.length > SSCC_LINE_LENGTH) {
            processedLines.addAll(line.chunked(SSCC_LINE_LENGTH))
        } else {
            processedLines.add(line)
        }
    }
    val processed = processedLines.joinToString("\n")

    // Adjust cursor: number of chars added (newlines inserted)
    val added = processed.length - raw.length
    val newCursor = (newValue.selection.end + added).coerceIn(0, processed.length)
    return TextFieldValue(text = processed, selection = TextRange(newCursor))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormSSCCScreen(
    delivery: DeliveryData,
    onUpdate: (DeliveryData) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(delivery.ssccNumbers))
    }

    // Sync if delivery changes externally (e.g. draft reload)
    LaunchedEffect(delivery.ssccNumbers) {
        if (delivery.ssccNumbers != textFieldValue.text) {
            textFieldValue = TextFieldValue(delivery.ssccNumbers)
        }
    }

    val lineCount = if (textFieldValue.text.isEmpty()) 0
    else textFieldValue.text.split("\n").size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Numéros SSCC") },
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
                    onNext = onNext,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Saisie des numéros de traçabilité de palettes (SSCC)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Chaque ligne correspond à un numéro SSCC (18 caractères max). " +
                        "Un saut de ligne est inséré automatiquement après 18 caractères.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Text(
                text = "$lineCount palette(s) saisie(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    val processed = processSSCCText(newValue, textFieldValue.text)
                    textFieldValue = processed
                    onUpdate(delivery.copy(ssccNumbers = processed.text))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = FontFamily.Monospace
                ),
                placeholder = {
                    Text(
                        "Saisir les numéros SSCC...\n(18 caractères par ligne)",
                        color = TextSecondary
                    )
                },
                label = { Text("Numéros SSCC") },
                singleLine = false,
                maxLines = Int.MAX_VALUE
            )
        }
    }
}
