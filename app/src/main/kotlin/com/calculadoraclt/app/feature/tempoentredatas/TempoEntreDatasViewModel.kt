package com.calculadoraclt.app.feature.tempoentredatas

import androidx.lifecycle.ViewModel
import com.calculadoraclt.domain.tempoentredatas.TempoEntreDatasCalculator
import com.calculadoraclt.domain.tempoentredatas.TempoEntreDatasInput
import com.calculadoraclt.domain.tempoentredatas.TempoEntreDatasResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class TempoEntreDatasUiState(
    val dataInicial: LocalDate? = null,
    val dataFinal: LocalDate? = null,
    val resultado: TempoEntreDatasResult? = null,
)

class TempoEntreDatasViewModel(
    private val calculator: TempoEntreDatasCalculator = TempoEntreDatasCalculator(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(TempoEntreDatasUiState())
    val uiState: StateFlow<TempoEntreDatasUiState> = _uiState.asStateFlow()

    fun onDataInicialChange(data: LocalDate) {
        _uiState.update { it.copy(dataInicial = data) }
        recalcular()
    }

    fun onDataFinalChange(data: LocalDate) {
        _uiState.update { it.copy(dataFinal = data) }
        recalcular()
    }

    private fun recalcular() {
        val estadoAtual = _uiState.value
        val inicial = estadoAtual.dataInicial
        val final = estadoAtual.dataFinal
        if (inicial == null || final == null) return

        val resultado = calculator.calculate(TempoEntreDatasInput(inicial, final))
        _uiState.update { it.copy(resultado = resultado) }
    }
}
