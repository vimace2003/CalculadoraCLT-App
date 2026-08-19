package com.calculadoraclt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.calculadoraclt.app.ads.AdIntroOverlay
import com.calculadoraclt.app.ads.LaunchAdUiState
import com.calculadoraclt.app.navigation.AppNavHost
import com.calculadoraclt.designsystem.theme.CalculadoraCltTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val launchAdManager = (application as CalculadoraCltApp).launchAdManager
        setContent {
            CalculadoraCltRoot(
                adUiState = launchAdManager.uiState.collectAsState().value,
                onAssistirAnuncio = launchAdManager::onUsuarioAceitouAssistir,
                onPularAnuncio = launchAdManager::onUsuarioPulou,
            )
        }
    }
}

@Composable
private fun CalculadoraCltRoot(
    adUiState: LaunchAdUiState,
    onAssistirAnuncio: () -> Unit,
    onPularAnuncio: () -> Unit,
) {
    CalculadoraCltTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            AppNavHost(navController = navController)

            if (adUiState == LaunchAdUiState.MOSTRANDO_INTRODUCAO) {
                AdIntroOverlay(
                    onAssistir = onAssistirAnuncio,
                    onPular = onPularAnuncio,
                )
            }
        }
    }
}
