package com.calculadoraclt.app.feature.horasextras

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import com.calculadoraclt.designsystem.component.PercentTextField
import com.calculadoraclt.designsystem.component.ResultLine
import com.calculadoraclt.designsystem.component.ResultSummaryCard
import com.calculadoraclt.domain.horasextras.PercentualHoraExtra

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorasExtrasScreen(
    onNavigateBack: () -> Unit,
    viewModel: HorasExtrasViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Horas Extras") },
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
                label = "Salário mensal",
            )
            OutlinedTextField(
                value = uiState.cargaHorariaMensal,
                onValueChange = viewModel::onCargaHorariaChange,
                label = { Text("Carga horária mensal") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = uiState.quantidadeHoras,
                onValueChange = viewModel::onQuantidadeHorasChange,
                label = { Text("Quantidade de horas extras") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )

            val opcoes = listOf("50%", "100%", "Outro")
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                opcoes.forEachIndexed { index, label ->
                    SegmentedButton(
                        selected = when (index) {
                            0 -> uiState.percentual == PercentualHoraExtra.CINQUENTA
                            1 -> uiState.percentual == PercentualHoraExtra.CEM
                            else -> uiState.percentual == PercentualHoraExtra.PERSONALIZADO
                        },
                        onClick = {
                            viewModel.onPercentualChange(
                                when (index) {
                                    0 -> PercentualHoraExtra.CINQUENTA
                                    1 -> PercentualHoraExtra.CEM
                                    else -> PercentualHoraExtra.PERSONALIZADO
                                },
                            )
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = opcoes.size),
                    ) {
                        Text(label)
                    }
                }
            }

            if (uiState.percentual == PercentualHoraExtra.PERSONALIZADO) {
                PercentTextField(
                    value = uiState.percentualPersonalizado,
                    onValueChange = viewModel::onPercentualPersonalizadoChange,
                    label = "Percentual personalizado",
                )
            }

            uiState.resultado?.let { resultado ->
                val linhas = buildList {
                    add(ResultLine("Valor da hora normal", resultado.valorHoraNormal.formatMoeda()))
                    add(ResultLine("Valor da hora extra", resultado.valorHoraExtra.formatMoeda()))
                    add(ResultLine("Total de horas extras", resultado.totalHorasExtras.formatMoeda()))
                    resultado.reflexoDsr?.let { add(ResultLine("Reflexo no DSR", it.formatMoeda())) }
                    add(ResultLine("Total geral", resultado.totalGeral.formatMoeda(), destaque = true))
                }
                ResultSummaryCard(titulo = "Resultado", linhas = linhas)
                LegalDisclaimerBanner()
            }
        }
    }
}
