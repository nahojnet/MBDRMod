package com.mbdrmod.delivery.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mbdrmod.delivery.ui.theme.Error
import com.mbdrmod.delivery.ui.theme.TextSecondary

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "Ce champ est obligatoire",
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = if (isRequired) "$label *" else label
                )
            },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = singleLine,
            maxLines = maxLines
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun FormNumberField(
    label: String,
    value: Int?,
    onValueChange: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isError: Boolean = false
) {
    FormTextField(
        label = label,
        value = value?.toString() ?: "",
        onValueChange = { newValue ->
            if (newValue.isEmpty()) {
                onValueChange(null)
            } else {
                newValue.toIntOrNull()?.let { onValueChange(it) }
            }
        },
        modifier = modifier,
        isRequired = isRequired,
        isError = isError,
        keyboardType = KeyboardType.Number
    )
}

@Composable
fun FormDecimalField(
    label: String,
    value: Double?,
    onValueChange: (Double?) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isError: Boolean = false
) {
    // Track the text input separately to allow free typing
    var textValue by remember { mutableStateOf("") }

    // Initialize text value from Double only once or when value becomes non-null from null
    LaunchedEffect(value) {
        if (value != null && textValue.isEmpty()) {
            // Only set initial value, don't override user input
            val formatted = if (value == value.toLong().toDouble()) {
                value.toLong().toString()
            } else {
                value.toString().replace(".", ",")
            }
            textValue = formatted
        } else if (value == null && textValue.isNotEmpty()) {
            // Value was cleared externally
            val parsed = textValue.replace(",", ".").toDoubleOrNull()
            if (parsed == null) {
                textValue = ""
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                // Allow: digits, comma (French decimal), and dot
                val filtered = newValue.filter { it.isDigit() || it == ',' || it == '.' }

                // Only one decimal separator allowed
                val normalized = filtered.replace(".", ",")
                val parts = normalized.split(",")
                val finalValue = if (parts.size > 2) {
                    parts[0] + "," + parts.drop(1).joinToString("")
                } else {
                    normalized
                }

                textValue = finalValue

                // Convert to Double
                if (finalValue.isEmpty()) {
                    onValueChange(null)
                } else {
                    val doubleValue = finalValue.replace(",", ".").toDoubleOrNull()
                    if (doubleValue != null) {
                        onValueChange(doubleValue)
                    }
                }
            },
            label = {
                Text(text = if (isRequired) "$label *" else label)
            },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        if (isError) {
            Text(
                text = "Ce champ est obligatoire",
                color = Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTimeField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isError: Boolean = false
) {
    // Parse current value
    val parts = value.split(":")
    val currentHour = parts.getOrNull(0)?.toIntOrNull()
    val currentMinute = parts.getOrNull(1)?.toIntOrNull()

    var hoursExpanded by remember { mutableStateOf(false) }
    var minutesExpanded by remember { mutableStateOf(false) }

    val hours = (0..23).toList()
    val minutes = (0..59).toList()

    // Scroll states for inertia effect
    val hoursScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = currentHour ?: 0
    )
    val minutesScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = currentMinute ?: 0
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (isRequired) "$label *" else label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hours dropdown
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = currentHour?.toString()?.padStart(2, '0') ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Heures") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hoursExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { hoursExpanded = true },
                    isError = isError,
                    singleLine = true
                )
                DropdownMenu(
                    expanded = hoursExpanded,
                    onDismissRequest = { hoursExpanded = false },
                    modifier = Modifier.heightIn(max = 250.dp)
                ) {
                    LazyColumn(
                        state = hoursScrollState,
                        modifier = Modifier
                            .width(120.dp)
                            .heightIn(max = 250.dp),
                        flingBehavior = androidx.compose.foundation.gestures.ScrollableDefaults.flingBehavior()
                    ) {
                        items(hours) { hour ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = hour.toString().padStart(2, '0'),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                onClick = {
                                    val newMinute = currentMinute ?: 0
                                    onValueChange("${hour.toString().padStart(2, '0')}:${newMinute.toString().padStart(2, '0')}")
                                    hoursExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Text(
                text = ":",
                style = MaterialTheme.typography.headlineMedium
            )

            // Minutes dropdown
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = currentMinute?.toString()?.padStart(2, '0') ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Minutes") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = minutesExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { minutesExpanded = true },
                    isError = isError,
                    singleLine = true
                )
                DropdownMenu(
                    expanded = minutesExpanded,
                    onDismissRequest = { minutesExpanded = false },
                    modifier = Modifier.heightIn(max = 250.dp)
                ) {
                    LazyColumn(
                        state = minutesScrollState,
                        modifier = Modifier
                            .width(120.dp)
                            .heightIn(max = 250.dp),
                        flingBehavior = androidx.compose.foundation.gestures.ScrollableDefaults.flingBehavior()
                    ) {
                        items(minutes) { minute ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = minute.toString().padStart(2, '0'),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                onClick = {
                                    val newHour = currentHour ?: 0
                                    onValueChange("${newHour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}")
                                    minutesExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        if (isError) {
            Text(
                text = "Ce champ est obligatoire",
                color = Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun FormDateField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isError: Boolean = false
) {
    FormTextField(
        label = "$label (AAAA/MM/JJ)",
        value = value,
        onValueChange = { newValue ->
            // Format date as YYYY/MM/DD
            val cleaned = newValue.filter { it.isDigit() }
            val formatted = when {
                cleaned.length <= 4 -> cleaned
                cleaned.length <= 6 -> "${cleaned.take(4)}/${cleaned.drop(4)}"
                cleaned.length <= 8 -> "${cleaned.take(4)}/${cleaned.substring(4, 6)}/${cleaned.drop(6)}"
                else -> "${cleaned.take(4)}/${cleaned.substring(4, 6)}/${cleaned.substring(6, 8)}"
            }
            onValueChange(formatted)
        },
        modifier = modifier,
        isRequired = isRequired,
        isError = isError,
        keyboardType = KeyboardType.Number
    )
}

@Composable
fun FormTemperatureField(
    label: String,
    value: Double?,
    onValueChange: (Double?) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isError: Boolean = false
) {
    // Track the text input separately to allow "-" at the start and free typing
    var textValue by remember { mutableStateOf("") }

    // Initialize text value from Double only once
    LaunchedEffect(value) {
        if (value != null && textValue.isEmpty()) {
            val formatted = if (value == value.toLong().toDouble()) {
                value.toLong().toString()
            } else {
                value.toString().replace(".", ",")
            }
            textValue = formatted
        } else if (value == null && textValue.isNotEmpty()) {
            val parsed = textValue.replace(",", ".").toDoubleOrNull()
            if (parsed == null) {
                textValue = ""
            }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                // Allow: digits, comma (French decimal), dot, and minus sign
                val filtered = newValue.filter { it.isDigit() || it == ',' || it == '.' || it == '-' }

                // Ensure minus is only at the beginning
                val withMinus = if (filtered.startsWith("-")) {
                    "-" + filtered.drop(1).filter { it != '-' }
                } else {
                    filtered.filter { it != '-' }
                }

                // Only one decimal separator allowed
                val normalized = withMinus.replace(".", ",")
                val parts = normalized.split(",")
                val finalValue = if (parts.size > 2) {
                    parts[0] + "," + parts.drop(1).joinToString("")
                } else {
                    normalized
                }

                textValue = finalValue

                // Convert to Double
                if (finalValue.isEmpty() || finalValue == "-") {
                    onValueChange(null)
                } else {
                    val doubleValue = finalValue.replace(",", ".").toDoubleOrNull()
                    if (doubleValue != null) {
                        onValueChange(doubleValue)
                    }
                }
            },
            label = {
                Text(text = if (isRequired) "$label (°C) *" else "$label (°C)")
            },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        if (isError) {
            Text(
                text = "Ce champ est obligatoire",
                color = Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun FormRadioGroup(
    label: String,
    options: List<Pair<String, String>>, // Pair of value and display text
    selectedValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = if (isRequired) "$label *" else label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        options.forEach { (value, displayText) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                RadioButton(
                    selected = selectedValue == value,
                    onClick = { onValueChange(value) }
                )
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun FormNavigationButtons(
    onPrevious: (() -> Unit)? = null,
    onNext: (() -> Unit)? = null,
    onFinish: (() -> Unit)? = null,
    isLastForm: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (onPrevious != null) {
            OutlinedButton(
                onClick = onPrevious,
                modifier = Modifier.weight(1f)
            ) {
                Text("Précédent")
            }
        }

        if (isLastForm && onFinish != null) {
            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f)
            ) {
                Text("Terminer")
            }
        } else if (onNext != null) {
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f)
            ) {
                Text("Suivant")
            }
        }
    }
}

@Composable
fun FormCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}
