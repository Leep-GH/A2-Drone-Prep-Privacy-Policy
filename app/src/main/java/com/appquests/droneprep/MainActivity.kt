package com.appquests.droneprep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.appquests.droneprep.navigation.AppNavHost
import com.appquests.droneprep.ui.theme.DronePrepTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DronePrepTheme {
                AppNavHost()
            }
        }
    }
}