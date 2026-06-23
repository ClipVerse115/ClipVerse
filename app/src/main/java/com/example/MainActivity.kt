package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainDashboard
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ClipViewModel
import com.example.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: ClipViewModel by viewModels {
        ViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Immersive edge-to-edge full screen coordinates
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                MainDashboard(viewModel = viewModel)
            }
        }
    }
}
