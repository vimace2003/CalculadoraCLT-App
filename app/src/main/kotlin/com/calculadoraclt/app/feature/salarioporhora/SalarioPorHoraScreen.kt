package com.calculadoraclt.app.feature.salarioporhora

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
fun SalarioPorHoraScreen(
    onNavigateBack: () -> Unit,
    viewModel: SalarioPorHoraViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Salário por Hora") },
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
                value = uiState.salarioMensal,
                onValueChange = viewModel::onSalarioMensalChange,
                label = "Salário mensal",
            )
            OutlinedTextField(
                value = uiState.cargaHorariaSemanal,
                onValueChange = viewModel::onCargaHorariaChange,
                label = { Text("Carga horária semanal (horas)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )

            uiState.resultado?.let { resultado ->
                ResultSummaryCard(
                    titulo = "Resultado",
                    linhas = listOf(
                        ResultLine("Valor da hora", resultado.valorHora.formatMoeda(), destaque = true),
                        ResultLine("Valor do minuto", resultado.valorMinuto.formatMoeda()),
                    ),
                )
            }
        }
    }
}
