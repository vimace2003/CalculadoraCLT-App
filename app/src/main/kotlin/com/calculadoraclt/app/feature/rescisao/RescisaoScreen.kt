package com.calculadoraclt.app.feature.rescisao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calculadoraclt.app.ads.BannerAdView
import com.calculadoraclt.common.format.formatMoeda
import com.calculadoraclt.designsystem.component.CltDateField
import com.calculadoraclt.designsystem.component.CurrencyTextField
import com.calculadoraclt.designsystem.component.LegalDisclaimerBanner
import com.calculadoraclt.designsystem.component.ResultLine
import com.calculadoraclt.designsystem.component.ResultSummaryCard
import com.calculadoraclt.domain.rescisao.TipoAvisoPrevio
import com.calculadoraclt.domain.rescisao.TipoRescisao
import java.math.BigDecimal

private fun TipoRescisao.rotulo(): String = when (this) {
    TipoRescisao.SEM_JUSTA_CAUSA -> "Demissão sem justa causa"
    TipoRescisao.PEDIDO_DEMISSAO -> "Pedido de demissão"
    TipoRescisao.JUSTA_CAUSA -> "Demissão por justa causa"
    TipoRescisao.ACORDO_MUTUO -> "Acordo mútuo"
    TipoRescisao.TERMINO_CONTRATO_DETERMINADO -> "Término de contrato determinado"
}

private fun TipoAvisoPrevio.rotulo(): String = when (this) {
    TipoAvisoPrevio.INDENIZADO -> "Indenizado (não trabalhado)"
    TipoAvisoPrevio.TRABALHADO -> "Trabalhado"
    TipoAvisoPrevio.DISPENSADO -> "Dispensado (acordo)"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> DropdownSelecionavel(
    label: String,
    opcoes: List<T>,
    selecionado: T,
    rotulo: (T) -> String,
    onSelecionar: (T) -> Unit,
) {
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = rotulo(selecionado),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(rotulo(opcao)) },
                    onClick = {
                        onSelecionar(opcao)
                        expandido = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RescisaoScreen(
    onNavigateBack: () -> Unit,
    viewModel: RescisaoViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rescisão") },
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
            DropdownSelecionavel(
                label = "Tipo de rescisão",
                opcoes = TipoRescisao.entries,
                selecionado = uiState.tipoRescisao,
                rotulo = TipoRescisao::rotulo,
                onSelecionar = viewModel::onTipoRescisaoChange,
            )
            CurrencyTextField(
                value = uiState.salarioBase,
                onValueChange = viewModel::onSalarioChange,
                label = "Salário base",
            )
            CltDateField(
                value = uiState.dataAdmissao,
                onValueChange = viewModel::onDataAdmissaoChange,
                label = "Data de admissão",
            )
            CltDateField(
                value = uiState.dataDemissao,
                onValueChange = viewModel::onDataDemissaoChange,
                label = "Data de demissão",
            )
            DropdownSelecionavel(
                label = "Aviso prévio",
                opcoes = TipoAvisoPrevio.entries,
                selecionado = uiState.tipoAvisoPrevio,
                rotulo = TipoAvisoPrevio::rotulo,
                onSelecionar = viewModel::onTipoAvisoPrevioChange,
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
                Text("Possui férias vencidas", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = uiState.possuiFeriasVencidas, onCheckedChange = viewModel::onFeriasVencidasChange)
            }

            uiState.resultado?.let { resultado ->
                val linhas = buildList {
                    add(ResultLine("Saldo de salário", resultado.saldoSalario.formatMoeda()))
                    resultado.avisoPrevioIndenizado?.let {
                        add(ResultLine("Aviso prévio indenizado (${resultado.diasAvisoPrevio} dias)", it.formatMoeda()))
                    }
                    resultado.feriasVencidas?.let { add(ResultLine("Férias vencidas + 1/3", it.formatMoeda())) }
                    add(ResultLine("Férias proporcionais + 1/3", resultado.feriasProporcionais.formatMoeda()))
                    add(ResultLine("13º proporcional", resultado.decimoTerceiroProporcional.formatMoeda()))
                    add(ResultLine("Multa FGTS (${resultado.percentualMultaFgts.multiply(BigDecimal(100)).toInt()}%)", resultado.fgtsMultaRescisoria.formatMoeda()))
                    add(ResultLine("Total bruto", resultado.totalBruto.formatMoeda()))
                    add(ResultLine("Total de descontos (INSS/IRRF)", resultado.totalDescontos.formatMoeda()))
                    add(ResultLine("Total líquido", resultado.totalLiquido.formatMoeda(), destaque = true))
                }
                ResultSummaryCard(titulo = "Resultado", linhas = linhas)
                LegalDisclaimerBanner()
            }
        }
    }
}
