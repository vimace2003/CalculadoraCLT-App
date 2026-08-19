package com.calculadoraclt.app.feature.ferias

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
import com.calculadoraclt.designsystem.component.LegalDisclaimerBanner
import com.calculadoraclt.designsystem.component.ResultLine
import com.calculadoraclt.designsystem.component.ResultSummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeriasScreen(
    onNavigateBack: () -> Unit,
    viewModel: FeriasViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Férias") },
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
                value = uiState.diasFerias,
                onValueChange = viewModel::onDiasFeriasChange,
                label = { Text("Dias de férias (1 a 30)") },
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

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Vender 1/3 (abono pecuniário)", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = uiState.venderAbonoPecuniario, onCheckedChange = viewModel::onVenderAbonoChange)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Adiantar 13º junto com férias", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = uiState.adiantar13, onCheckedChange = viewModel::onAdiantar13Change)
            }

            uiState.resultado?.let { resultado ->
                val linhas = buildList {
                    add(ResultLine("Valor das férias", resultado.valorFerias.formatMoeda()))
                    add(ResultLine("1/3 constitucional", resultado.tercoConstitucional.formatMoeda()))
                    resultado.valorAbono?.let { add(ResultLine("Abono pecuniário", it.formatMoeda())) }
                    resultado.tercoAbono?.let { add(ResultLine("1/3 do abono", it.formatMoeda())) }
                    resultado.adiantamento13?.let { add(ResultLine("Adiantamento 13º", it.formatMoeda())) }
                    add(ResultLine("Total bruto", resultado.totalBruto.formatMoeda()))
                    add(ResultLine("Desconto INSS", resultado.descontoInss.formatMoeda()))
                    add(ResultLine("Desconto IRRF", resultado.descontoIrrf.formatMoeda()))
                    add(ResultLine("Total líquido", resultado.totalLiquido.formatMoeda(), destaque = true))
                }
                ResultSummaryCard(titulo = "Resultado", linhas = linhas)
                LegalDisclaimerBanner()
            }
        }
    }
}
