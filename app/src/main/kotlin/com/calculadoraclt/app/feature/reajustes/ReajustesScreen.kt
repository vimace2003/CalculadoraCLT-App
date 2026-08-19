package com.calculadoraclt.app.feature.reajustes

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
import com.calculadoraclt.common.format.formatMoeda
import com.calculadoraclt.designsystem.component.CltDateField
import com.calculadoraclt.designsystem.component.CurrencyTextField
import com.calculadoraclt.designsystem.component.LegalDisclaimerBanner
import com.calculadoraclt.designsystem.component.PercentTextField
import com.calculadoraclt.designsystem.component.ResultLine
import com.calculadoraclt.designsystem.component.ResultSummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReajustesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReajustesViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reajustes e Dissídios") },
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
            CurrencyTextField(
                value = uiState.salarioAtual,
                onValueChange = viewModel::onSalarioAtualChange,
                label = "Salário atual",
            )
            PercentTextField(
                value = uiState.percentualReajuste,
                onValueChange = viewModel::onPercentualChange,
                label = "Percentual de reajuste",
            )
            CltDateField(
                value = uiState.dataBase,
                onValueChange = viewModel::onDataBaseChange,
                label = "Data-base do reajuste",
            )
            CltDateField(
                value = uiState.dataReferencia,
                onValueChange = viewModel::onDataReferenciaChange,
                label = "Data de referência",
            )

            uiState.resultado?.let { resultado ->
                val linhas = buildList {
                    add(ResultLine("Novo salário", resultado.salarioNovo.formatMoeda(), destaque = true))
                    add(ResultLine("Diferença mensal", resultado.diferenca.formatMoeda()))
                    add(ResultLine("Meses retroativos", "${resultado.mesesRetroativos}"))
                    resultado.valorRetroativoTotal?.let {
                        add(ResultLine("Valor retroativo total", it.formatMoeda()))
                    }
                }
                ResultSummaryCard(titulo = "Resultado", linhas = linhas)
                LegalDisclaimerBanner()
            }
        }
    }
}
