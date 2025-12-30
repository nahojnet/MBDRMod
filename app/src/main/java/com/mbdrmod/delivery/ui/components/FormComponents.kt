package com.mbdrmod.delivery.ui.components

import androidx.compose.foundation.layout.*
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
            ExposedDropdownMenuBox(
                expanded = hoursExpanded,
                onExpandedChange = { hoursExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = currentHour?.toString()?.padStart(2, '0') ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Heure") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = hoursExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = isError,
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = hoursExpanded,
                    onDismissRequest = { hoursExpanded = false }
                ) {
                    hours.forEach { hour ->
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

            Text(
                text = ":",
                style = MaterialTheme.typography.headlineMedium
            )

            // Minutes dropdown
            ExposedDropdownMenuBox(
                expanded = minutesExpanded,
                onExpandedChange = { minutesExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = currentMinute?.toString()?.padStart(2, '0') ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Minute") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = minutesExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = isError,
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = minutesExpanded,
                    onDismissRequest = { minutesExpanded = false }
                ) {
                    minutes.forEach { minute ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTemperatureField(
    label: String,
    value: Double?,
    onValueChange: (Double?) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isError: Boolean = false,
    isFrozen: Boolean = false // Default to "-" sign for frozen temperatures
) {
    // Parse current value - default to 0 if null
    val effectiveValue = value ?: 0.0
    val currentSign = if (effectiveValue < 0) "-" else if (isFrozen && value == null) "-" else "+"
    val absValue = kotlin.math.abs(effectiveValue)
    val currentUnits = absValue.toInt()
    val currentDecimal = ((absValue - absValue.toInt()) * 10).toInt()

    var signExpanded by remember { mutableStateOf(false) }
    var unitsExpanded by remember { mutableStateOf(false) }
    var decimalExpanded by remember { mutableStateOf(false) }

    // Track selected values - default to 0 if null
    var selectedSign by remember(value) { mutableStateOf(currentSign) }
    var selectedUnits by remember(value) { mutableStateOf(currentUnits) }
    var selectedDecimal by remember(value) { mutableStateOf(currentDecimal) }

    val signs = listOf("-", "+")
    val units = (0..25).toList()
    val decimals = (0..9).toList()

    // Combine values into Double
    fun updateValue() {
        val decimal = selectedDecimal
        val absTemp = selectedUnits + (decimal / 10.0)
        val finalTemp = if (selectedSign == "-") -absTemp else absTemp
        onValueChange(finalTemp)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (isRequired) "$label (°C) *" else "$label (°C)",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sign dropdown
            ExposedDropdownMenuBox(
                expanded = signExpanded,
                onExpandedChange = { signExpanded = it },
                modifier = Modifier.width(72.dp)
            ) {
                OutlinedTextField(
                    value = selectedSign,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = signExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = isError,
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = signExpanded,
                    onDismissRequest = { signExpanded = false }
                ) {
                    signs.forEach { sign ->
                        DropdownMenuItem(
                            text = { Text(sign) },
                            onClick = {
                                selectedSign = sign
                                signExpanded = false
                                updateValue()
                            }
                        )
                    }
                }
            }

            // Units dropdown (0-25)
            ExposedDropdownMenuBox(
                expanded = unitsExpanded,
                onExpandedChange = { unitsExpanded = it },
                modifier = Modifier.width(95.dp)
            ) {
                OutlinedTextField(
                    value = selectedUnits.toString(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitsExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = isError,
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = unitsExpanded,
                    onDismissRequest = { unitsExpanded = false }
                ) {
                    units.forEach { unit ->
                        DropdownMenuItem(
                            text = { Text(unit.toString()) },
                            onClick = {
                                selectedUnits = unit
                                unitsExpanded = false
                                updateValue()
                            }
                        )
                    }
                }
            }

            Text(
                text = ",",
                style = MaterialTheme.typography.headlineMedium
            )

            // Decimal dropdown (0-9)
            ExposedDropdownMenuBox(
                expanded = decimalExpanded,
                onExpandedChange = { decimalExpanded = it },
                modifier = Modifier.width(72.dp)
            ) {
                OutlinedTextField(
                    value = selectedDecimal.toString(),
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = decimalExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = isError,
                    singleLine = true
                )
                ExposedDropdownMenu(
                    expanded = decimalExpanded,
                    onDismissRequest = { decimalExpanded = false }
                ) {
                    decimals.forEach { decimal ->
                        DropdownMenuItem(
                            text = { Text(decimal.toString()) },
                            onClick = {
                                selectedDecimal = decimal
                                decimalExpanded = false
                                updateValue()
                            }
                        )
                    }
                }
            }

            Text(
                text = "°C",
                style = MaterialTheme.typography.bodyLarge
            )
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
fun FormCheckboxField(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
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
