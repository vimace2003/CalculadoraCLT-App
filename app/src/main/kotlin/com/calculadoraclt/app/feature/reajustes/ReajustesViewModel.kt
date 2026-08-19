package com.calculadoraclt.app.feature.reajustes

import androidx.lifecycle.ViewModel
import com.calculadoraclt.common.format.paraBigDecimalOuNull
import com.calculadoraclt.domain.reajustes.ReajusteCalculator
import com.calculadoraclt.domain.reajustes.ReajusteInput
import com.calculadoraclt.domain.reajustes.ReajusteResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class ReajustesUiState(
    val salarioAtual: String = "",
    val percentualReajuste: String = "",
    val dataBase: LocalDate? = null,
    val dataReferencia: LocalDate = LocalDate.now(),
    val resultado: ReajusteResult? = null,
)

class ReajustesViewModel(
    private val calculator: ReajusteCalculator = ReajusteCalculator(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReajustesUiState())
    val uiState: StateFlow<ReajustesUiState> = _uiState.asStateFlow()

    fun onSalarioAtualChange(valor: String) {
        _uiState.update { it.copy(salarioAtual = valor) }
        recalcular()
    }

    fun onPercentualChange(valor: String) {
        _uiState.update { it.copy(percentualReajuste = valor) }
        recalcular()
    }

    fun onDataBaseChange(data: LocalDate) {
        _uiState.update { it.copy(dataBase = data) }
        recalcular()
    }

    fun onDataReferenciaChange(data: LocalDate) {
        _uiState.update { it.copy(dataReferencia = data) }
        recalcular()
    }

    private fun recalcular() {
        val estadoAtual = _uiState.value
        val salario = estadoAtual.salarioAtual.paraBigDecimalOuNull()
        val percentual = estadoAtual.percentualReajuste.paraBigDecimalOuNull()
        val dataBase = estadoAtual.dataBase
        if (salario == null || percentual == null || dataBase == null) {
            _uiState.update { it.copy(resultado = null) }
            return
        }

        val resultado = calculator.calculate(
            ReajusteInput(
                salarioAtual = salario,
                percentualReajuste = percentual,
                dataBase = dataBase,
                dataReferencia = estadoAtual.dataReferencia,
            ),
        )
        _uiState.update { it.copy(resultado = resultado) }
    }
}
