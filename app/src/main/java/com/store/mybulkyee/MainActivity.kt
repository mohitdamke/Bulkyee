package com.store.mybulkyee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.store.mybulkyee.navigation.NavigationControl
import com.store.mybulkyee.ui.theme.BulkyeeTheme
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MobileAds.initialize(this) { }
        setContent {
            BulkyeeTheme {
                NavigationControl()
            }
        }
    }
}

