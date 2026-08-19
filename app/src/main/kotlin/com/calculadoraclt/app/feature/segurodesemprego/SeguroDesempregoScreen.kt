package com.calculadoraclt.app.feature.segurodesemprego

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calculadoraclt.app.ads.BannerAdView
import com.calculadoraclt.common.format.formatMoeda
import com.calculadoraclt.designsystem.component.CurrencyTextField
import com.calculadoraclt.designsystem.component.ResultLine
import com.calculadoraclt.designsystem.component.ResultSummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeguroDesempregoScreen(
    onNavigateBack: () -> Unit,
    viewModel: SeguroDesempregoViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seguro-Desemprego") },
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
                value = uiState.salario1,
                onValueChange = viewModel::onSalario1Change,
                label = "Salário do último mês",
            )
            CurrencyTextField(
                value = uiState.salario2,
                onValueChange = viewModel::onSalario2Change,
                label = "Salário do penúltimo mês",
            )
            CurrencyTextField(
                value = uiState.salario3,
                onValueChange = viewModel::onSalario3Change,
                label = "Salário de 3 meses atrás",
            )
            OutlinedTextField(
                value = uiState.mesesTrabalhadosUltimoVinculo,
                onValueChange = viewModel::onMesesTrabalhadosChange,
                label = { Text("Meses trabalhados no último vínculo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = uiState.numeroSolicitacoesAnteriores,
                onValueChange = viewModel::onSolicitacoesChange,
                label = { Text("Número de solicitações anteriores") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            uiState.resultado?.let { resultado ->
                if (!resultado.elegivel) {
                    ResultSummaryCard(
                        titulo = "Resultado",
                        linhas = listOf(
                            ResultLine("Média salarial", resultado.mediaSalarial.formatMoeda()),
                            ResultLine("Elegível", "Não", destaque = true),
                        ),
                    )
                    resultado.motivoInelegibilidade?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    ResultSummaryCard(
                        titulo = "Resultado",
                        linhas = listOf(
                            ResultLine("Média salarial", resultado.mediaSalarial.formatMoeda()),
                            ResultLine("Número de parcelas", "${resultado.numeroParcelas}"),
                            ResultLine("Valor de cada parcela", resultado.valorParcela.formatMoeda(), destaque = true),
                        ),
                    )
                }
            }
        }
    }
}
