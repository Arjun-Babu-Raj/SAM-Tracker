package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicFormFields(
    fields: List<FormField>,
    answers: Map<String, String>,
    onAnswerChange: (String, String) -> Unit
) {
    fields.forEach { field ->
        when (field) {
            is FormField.Text -> {
                val keyboardOptions = if (field.isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default
                OutlinedTextField(
                    value = answers[field.id] ?: "",
                    onValueChange = { onAnswerChange(field.id, it) },
                    label = { Text(field.label) },
                    keyboardOptions = keyboardOptions,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )
            }
            is FormField.Dropdown -> {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    OutlinedTextField(
                        value = answers[field.id] ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(field.label) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        field.options.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onAnswerChange(field.id, option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
            is FormField.Checkbox -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = answers[field.id] == "true",
                        onCheckedChange = { onAnswerChange(field.id, it.toString()) }
                    )
                    Text(field.label, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}
