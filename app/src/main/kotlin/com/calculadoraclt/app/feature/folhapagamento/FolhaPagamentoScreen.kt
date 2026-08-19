package com.calculadoraclt.app.feature.folhapagamento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun FolhaPagamentoScreen(
    onNavigateBack: () -> Unit,
    viewModel: FolhaPagamentoViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Folha de Pagamento") },
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
                value = uiState.numeroDependentes,
                onValueChange = viewModel::onDependentesChange,
                label = { Text("Número de dependentes") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = uiState.descricaoProventoAdicional,
                onValueChange = viewModel::onDescricaoProventoChange,
                label = { Text("Descrição de provento adicional (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            CurrencyTextField(
                value = uiState.valorProventoAdicional,
                onValueChange = viewModel::onValorProventoChange,
                label = "Valor do provento adicional",
            )

            OutlinedTextField(
                value = uiState.descricaoDescontoAdicional,
                onValueChange = viewModel::onDescricaoDescontoChange,
                label = { Text("Descrição de desconto adicional (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            CurrencyTextField(
                value = uiState.valorDescontoAdicional,
                onValueChange = viewModel::onValorDescontoChange,
                label = "Valor do desconto adicional",
            )

            uiState.resultado?.let { resultado ->
                ResultSummaryCard(
                    titulo = "Folha do colaborador",
                    linhas = resultado.proventos.map { ResultLine(it.descricao, it.valor.formatMoeda()) } +
                        ResultLine("Total de proventos", resultado.totalProventos.formatMoeda(), destaque = true) +
                        resultado.descontos.map { ResultLine(it.descricao, "- ${it.valor.formatMoeda()}") } +
                        ResultLine("Total de descontos", resultado.totalDescontos.formatMoeda()) +
                        ResultLine("Salário líquido", resultado.salarioLiquido.formatMoeda(), destaque = true),
                )
                HorizontalDivider()
                ResultSummaryCard(
                    titulo = "Custo para a empresa",
                    linhas = listOf(
                        ResultLine("FGTS (8%)", resultado.encargosPatronais.fgts.formatMoeda()),
                        ResultLine("INSS patronal (20%)", resultado.encargosPatronais.inssPatronal.formatMoeda()),
                        ResultLine("Total de encargos", resultado.encargosPatronais.total.formatMoeda()),
                        ResultLine("Custo total para a empresa", resultado.custoTotalEmpresa.formatMoeda(), destaque = true),
                    ),
                )
            }
        }
    }
}
