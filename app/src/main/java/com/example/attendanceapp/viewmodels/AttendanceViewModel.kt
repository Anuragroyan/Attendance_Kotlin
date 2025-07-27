package com.example.attendanceapp.viewmodels

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.attendanceapp.data.AttendanceRecord
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AttendanceViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _records = mutableStateListOf<AttendanceRecord>()
    val records: List<AttendanceRecord> = _records

    var selectedRecord by mutableStateOf<AttendanceRecord?>(null)
    var searchQuery by mutableStateOf("")

    val filteredRecords: List<AttendanceRecord>
        get() = if (searchQuery.isBlank()) records
        else records.filter {
            it.name.contains(searchQuery, true) ||
                    it.role.contains(searchQuery, true)
        }

    init { fetchRecords() }

    fun fetchRecords() {
        db.collection("attendance").get().addOnSuccessListener { result ->
            _records.clear()
            result.forEach { doc ->
                _records.add(doc.toObject(AttendanceRecord::class.java).copy(id = doc.id))
            }
        }
    }

    fun addRecord(record: AttendanceRecord, onComplete: () -> Unit) {
        val newId = UUID.randomUUID().toString()
        val newRecord = record.copy(id = newId)

        db.collection("attendance")
            .document(newId) // Use the generated ID as document ID
            .set(newRecord)
            .addOnSuccessListener {
                fetchRecords()
                onComplete()
            }
    }


    fun updateRecord(record: AttendanceRecord, onComplete: () -> Unit) {
        db.collection("attendance").document(record.id).set(record).addOnSuccessListener {
            fetchRecords(); onComplete()
        }
    }

    fun deleteRecord(id: String) {
        db.collection("attendance").document(id).delete().addOnSuccessListener {
            fetchRecords()
        }
    }

    fun selectRecord(record: AttendanceRecord?) { selectedRecord = record }

    fun exportToCSVViaSAF(context: Context, uri: Uri) {
        val csvHeader = "Name,Role,Date,Status"
        val csvBody = records.joinToString("\n") {
            "\"${it.name}\",\"${it.role}\",\"${it.date}\",\"${it.status}\""
        }
        try {
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write("$csvHeader\n$csvBody".toByteArray())
            }
            Toast.makeText(context, "CSV saved successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
