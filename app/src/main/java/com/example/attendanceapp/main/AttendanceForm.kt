package com.example.attendanceapp.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.attendanceapp.data.AttendanceRecord
import com.example.attendanceapp.viewmodels.AttendanceViewModel
import kotlinx.coroutines.launch

@Composable
fun AttendanceForm(viewModel: AttendanceViewModel, onBack: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val record = viewModel.selectedRecord ?: AttendanceRecord()
    var name by remember { mutableStateOf(record.name) }
    var role by remember { mutableStateOf(record.role) }
    var date by remember { mutableStateOf(record.date) }
    var status by remember { mutableStateOf(record.status) }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            TextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            Spacer(Modifier.height(8.dp))
            DropdownMenuBox("Role", listOf("Student", "Employee"), role) { role = it }
            Spacer(Modifier.height(8.dp))
            DateInputField("Date", date) { date = it }
            Spacer(Modifier.height(8.dp))
            DropdownMenuBox("Status", listOf("Present", "Absent"), status) { status = it }

            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                if (name.isBlank() || role.isBlank() || date.isBlank() || status.isBlank()) {
                    scope.launch { snackbarHostState.showSnackbar("Please fill all fields.") }
                    return@Button
                }
                val newRecord = record.copy(name = name, role = role, date = date, status = status)
                if (record.id.isEmpty()) viewModel.addRecord(newRecord, onBack)
                else viewModel.updateRecord(newRecord, onBack)
            }) {
                Text(if (record.id.isEmpty()) "Add Attendance" else "Update Attendance")
            }
        }
    }
}