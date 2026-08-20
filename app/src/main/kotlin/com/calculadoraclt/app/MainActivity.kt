package com.calculadoraclt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.calculadoraclt.app.navigation.AppNavHost
import com.calculadoraclt.designsystem.theme.CalculadoraCltTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraCltRoot()
        }
    }
}

@Composable
private fun CalculadoraCltRoot() {
    CalculadoraCltTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            AppNavHost(navController = navController)
        }
    }
}
