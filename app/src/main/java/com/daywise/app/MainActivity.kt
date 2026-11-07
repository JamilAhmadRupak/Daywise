package com.daywise.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.daywise.app.ui.navigation.DaywiseNavHost
import com.daywise.app.ui.theme.DaywiseTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as DaywiseApplication
        val repository = app.repository

        setContent {
            DaywiseTheme {
                DaywiseNavHost(repository = repository)
            }
        }
    }
}
