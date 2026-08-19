package com.calculadoraclt.app.feature.decimoterceiro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
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
fun DecimoTerceiroScreen(
    onNavigateBack: () -> Unit,
    viewModel: DecimoTerceiroViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("13º Salário") },
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
                value = uiState.salarioBase,
                onValueChange = viewModel::onSalarioChange,
                label = "Salário base",
            )
            OutlinedTextField(
                value = uiState.mesesTrabalhadosNoAno,
                onValueChange = viewModel::onMesesChange,
                label = { Text("Meses trabalhados no ano") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = uiState.numeroDependentes,
                onValueChange = viewModel::onDependentesChange,
                label = { Text("Número de dependentes") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Houve adiantamento (1ª parcela)", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = uiState.houveAdiantamento,
                    onCheckedChange = viewModel::onAdiantamentoChange,
                )
            }

            uiState.resultado?.let { resultado ->
                ResultSummaryCard(
                    titulo = "Resultado",
                    linhas = listOf(
                        ResultLine("13º bruto proporcional", resultado.valorBrutoProporcional.formatMoeda()),
                        ResultLine("1ª parcela", resultado.primeiraParcela.formatMoeda()),
                        ResultLine("2ª parcela bruta", resultado.segundaParcelaBruta.formatMoeda()),
                        ResultLine("Desconto INSS", resultado.descontoInss.formatMoeda()),
                        ResultLine("Desconto IRRF", resultado.descontoIrrf.formatMoeda()),
                        ResultLine("2ª parcela líquida", resultado.segundaParcelaLiquida.formatMoeda()),
                        ResultLine("Total líquido", resultado.totalLiquido.formatMoeda(), destaque = true),
                    ),
                )
            }
        }
    }
}
