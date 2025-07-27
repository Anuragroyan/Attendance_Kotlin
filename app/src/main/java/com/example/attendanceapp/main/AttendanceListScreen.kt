package com.example.attendanceapp.main

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.attendanceapp.viewmodels.AttendanceViewModel
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.attendanceapp.data.AttendanceRecord
import kotlin.math.roundToInt


@Composable
fun AttendanceListScreen(viewModel: AttendanceViewModel, onEdit: () -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri -> uri?.let { viewModel.exportToCSVViaSAF(context, it) } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 20.dp)
    ) {
        Text(
            text = "Attendance Records",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 🔹 Stats View inserted here
        AttendanceStatsView(viewModel.filteredRecords)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.searchQuery = it },
                label = { Text("Search by name") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = {
                launcher.launch("attendance_export_${System.currentTimeMillis()}.csv")
            }) {
                Icon(Icons.Default.Save, contentDescription = "Export CSV")
            }
            IconButton(onClick = {
                viewModel.selectRecord(null)
                onEdit()
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Record")
            }
        }

        if (viewModel.filteredRecords.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No records found", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(viewModel.filteredRecords) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${record.name} (${record.role})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text("📅 ${record.date}", style = MaterialTheme.typography.bodyMedium)
                                Text("✅ ${record.status}", style = MaterialTheme.typography.bodyMedium)
                            }
                            IconButton(onClick = {
                                viewModel.selectRecord(record)
                                onEdit()
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = {
                                viewModel.deleteRecord(record.id)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun AttendanceStatsView(records: List<AttendanceRecord>) {
    val total = records.size
    val present = records.count { it.status == "Present" }
    val absent = records.count { it.status == "Absent" }

    val totalStudents = records.count { it.role == "Student" }
    val totalEmployees = records.count { it.role == "Employee" }

    val presentPercentage = if (total > 0) (present * 100f / total).roundToInt() else 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📊 Attendance Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Text("✅ Present: $present")
            Text("❌ Absent: $absent")
            Text("👨‍🎓 Students: $totalStudents")
            Text("👔 Employees: $totalEmployees")
            Text("📈 Attendance Rate: $presentPercentage%")
        }
    }
}
