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
    // Convert to French decimal format (comma as separator)
    val displayValue = value?.toString()?.replace(".", ",") ?: ""

    FormTextField(
        label = label,
        value = displayValue,
        onValueChange = { newValue ->
            if (newValue.isEmpty()) {
                onValueChange(null)
            } else {
                // Convert French format (comma) to standard format (dot)
                val normalizedValue = newValue.replace(",", ".")
                normalizedValue.toDoubleOrNull()?.let { onValueChange(it) }
            }
        },
        modifier = modifier,
        isRequired = isRequired,
        isError = isError,
        keyboardType = KeyboardType.Decimal
    )
}

@Composable
fun FormTimeField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isRequired: Boolean = false,
    isError: Boolean = false
) {
    FormTextField(
        label = "$label (HH:MM)",
        value = value,
        onValueChange = { newValue ->
            // Format time as HH:MM
            val cleaned = newValue.filter { it.isDigit() }
            val formatted = when {
                cleaned.length <= 2 -> cleaned
                cleaned.length <= 4 -> "${cleaned.take(2)}:${cleaned.drop(2)}"
                else -> "${cleaned.take(2)}:${cleaned.substring(2, 4)}"
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
    FormDecimalField(
        label = "$label (°C)",
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        isRequired = isRequired,
        isError = isError
    )
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
