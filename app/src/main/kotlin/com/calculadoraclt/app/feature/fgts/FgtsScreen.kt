package com.calculadoraclt.app.feature.fgts

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
import com.calculadoraclt.designsystem.component.CurrencyTextField
import com.calculadoraclt.designsystem.component.LegalDisclaimerBanner
import com.calculadoraclt.designsystem.component.ResultLine
import com.calculadoraclt.designsystem.component.ResultSummaryCard
import com.calculadoraclt.domain.rescisao.TipoRescisao

private fun TipoRescisao.rotulo(): String = when (this) {
    TipoRescisao.SEM_JUSTA_CAUSA -> "Demissão sem justa causa"
    TipoRescisao.PEDIDO_DEMISSAO -> "Pedido de demissão"
    TipoRescisao.JUSTA_CAUSA -> "Demissão por justa causa"
    TipoRescisao.ACORDO_MUTUO -> "Acordo mútuo"
    TipoRescisao.TERMINO_CONTRATO_DETERMINADO -> "Término de contrato determinado"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FgtsScreen(
    onNavigateBack: () -> Unit,
    viewModel: FgtsViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FGTS") },
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
                value = uiState.mesesTrabalhados,
                onValueChange = viewModel::onMesesChange,
                label = { Text("Meses trabalhados") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Simular multa rescisória", style = MaterialTheme.typography.bodyLarge)
                Switch(
                    checked = uiState.simularMulta,
                    onCheckedChange = viewModel::onSimularMultaChange,
                )
            }

            if (uiState.simularMulta) {
                var expandido by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandido,
                    onExpandedChange = { expandido = it },
                ) {
                    OutlinedTextField(
                        value = uiState.tipoRescisao.rotulo(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de rescisão") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    )
                    ExposedDropdownMenu(
                        expanded = expandido,
                        onDismissRequest = { expandido = false },
                    ) {
                        TipoRescisao.entries.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo.rotulo()) },
                                onClick = {
                                    viewModel.onTipoRescisaoChange(tipo)
                                    expandido = false
                                },
                            )
                        }
                    }
                }
            }

            uiState.resultado?.let { resultado ->
                val linhas = buildList {
                    add(ResultLine("Depósito mensal (8%)", resultado.depositoMensal.formatMoeda()))
                    add(ResultLine("Saldo estimado", resultado.saldoEstimadoTotal.formatMoeda(), destaque = true))
                    resultado.multaRescisoria?.let {
                        add(ResultLine("Multa rescisória", it.formatMoeda()))
                    }
                }
                ResultSummaryCard(titulo = "Resultado", linhas = linhas)
                LegalDisclaimerBanner()
            }
        }
    }
}
