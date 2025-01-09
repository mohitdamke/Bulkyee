package com.example.bulkyee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.bulkyee.navigation.NavigationControl
import com.example.bulkyee.ui.theme.BulkyeeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BulkyeeTheme {
                NavigationControl()
            }
        }
    }
}
