package com.example.attendanceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.attendanceapp.main.AttendanceForm
import com.example.attendanceapp.main.AttendanceListScreen
import com.example.attendanceapp.viewmodels.AttendanceViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)
        setContent {
          AttendanceApp()
        }
    }
}

@Composable
fun AttendanceApp() {
    val viewModel = remember { AttendanceViewModel() }
    var currentScreen by remember { mutableStateOf("list") }

    when (currentScreen) {
        "list" -> AttendanceListScreen(viewModel) { currentScreen = "form" }
        "form" -> AttendanceForm(viewModel) { currentScreen = "list" }
    }
}


