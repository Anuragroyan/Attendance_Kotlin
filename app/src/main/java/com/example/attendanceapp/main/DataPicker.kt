package com.example.attendanceapp.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import android.app.DatePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInputField(label: String, selectedDate: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val calendar = Calendar.getInstance()

                // If selectedDate is not blank, parse it to pre-fill the picker
                if (selectedDate.isNotBlank()) {
                    val parts = selectedDate.split("-")
                    if (parts.size == 3) {
                        calendar.set(Calendar.YEAR, parts[0].toInt())
                        calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
                        calendar.set(Calendar.DAY_OF_MONTH, parts[2].toInt())
                    }
                }

                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                        onDateSelected(formatted)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
    )
}

@Composable
fun DropdownMenuBox(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        // Wrap TextField in a Box with click handler
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
        ) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Dropdown"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = false // Optional: disables keyboard focus
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
