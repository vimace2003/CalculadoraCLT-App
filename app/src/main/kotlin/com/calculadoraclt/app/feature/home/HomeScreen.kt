package com.calculadoraclt.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.calculadoraclt.app.ads.BannerAdView
import com.calculadoraclt.designsystem.component.CltCalculatorCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateTo: (Any) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculadora CLT", style = MaterialTheme.typography.titleLarge) },
            )
        },
        bottomBar = { BannerAdView() },
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(calculatorCatalog) { item ->
                Box {
                    CltCalculatorCard(
                        titulo = item.titulo,
                        icone = item.icone,
                        onClick = { if (item.disponivel) onNavigateTo(item.route) },
                        modifier = Modifier.alpha(if (item.disponivel) 1f else 0.5f),
                    )
                    if (!item.disponivel) {
                        Badge(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                        ) {
                            Text("Em breve")
                        }
                    }
                }
            }
        }
    }
}
