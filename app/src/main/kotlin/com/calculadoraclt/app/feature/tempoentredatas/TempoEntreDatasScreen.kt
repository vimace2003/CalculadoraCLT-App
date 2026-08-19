package com.calculadoraclt.app.feature.tempoentredatas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calculadoraclt.app.ads.BannerAdView
import com.calculadoraclt.designsystem.component.CltDateField
import com.calculadoraclt.designsystem.component.ResultLine
import com.calculadoraclt.designsystem.component.ResultSummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TempoEntreDatasScreen(
    onNavigateBack: () -> Unit,
    viewModel: TempoEntreDatasViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tempo entre Datas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
            )
        },
        bottomBar = { BannerAdView() },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CltDateField(
                value = uiState.dataInicial,
                onValueChange = viewModel::onDataInicialChange,
                label = "Data inicial",
            )
            CltDateField(
                value = uiState.dataFinal,
                onValueChange = viewModel::onDataFinalChange,
                label = "Data final",
            )

            uiState.resultado?.let { resultado ->
                ResultSummaryCard(
                    titulo = "Resultado",
                    linhas = listOf(
                        ResultLine("Anos, meses e dias", "${resultado.anos}a ${resultado.meses}m ${resultado.dias}d", destaque = true),
                        ResultLine("Total em dias", "${resultado.totalDias}"),
                        ResultLine("Total em semanas", "${resultado.totalSemanas}"),
                        ResultLine("Total em meses", "${resultado.totalMeses}"),
                    ),
                )
            }
        }
    }
}
