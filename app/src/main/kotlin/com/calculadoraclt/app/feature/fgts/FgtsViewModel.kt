package com.calculadoraclt.app.feature.fgts

import androidx.lifecycle.ViewModel
import com.calculadoraclt.common.format.paraBigDecimalOuNull
import com.calculadoraclt.domain.fgts.FgtsCalculator
import com.calculadoraclt.domain.fgts.FgtsInput
import com.calculadoraclt.domain.fgts.FgtsResult
import com.calculadoraclt.domain.rescisao.TipoRescisao
import com.calculadoraclt.domain.taxtables.TaxTables2026
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FgtsUiState(
    val salarioBase: String = "",
    val mesesTrabalhados: String = "",
    val simularMulta: Boolean = false,
    val tipoRescisao: TipoRescisao = TipoRescisao.SEM_JUSTA_CAUSA,
    val resultado: FgtsResult? = null,
)

class FgtsViewModel(
    private val calculator: FgtsCalculator = FgtsCalculator(TaxTables2026.fgts),
) : ViewModel() {

    private val _uiState = MutableStateFlow(FgtsUiState())
    val uiState: StateFlow<FgtsUiState> = _uiState.asStateFlow()

    fun onSalarioChange(valor: String) {
        _uiState.update { it.copy(salarioBase = valor) }
        recalcular()
    }

    fun onMesesChange(valor: String) {
        _uiState.update { it.copy(mesesTrabalhados = valor) }
        recalcular()
    }

    fun onSimularMultaChange(ativo: Boolean) {
        _uiState.update { it.copy(simularMulta = ativo) }
        recalcular()
    }

    fun onTipoRescisaoChange(tipo: TipoRescisao) {
        _uiState.update { it.copy(tipoRescisao = tipo) }
        recalcular()
    }

    private fun recalcular() {
        val estadoAtual = _uiState.value
        val salario = estadoAtual.salarioBase.paraBigDecimalOuNull()
        val meses = estadoAtual.mesesTrabalhados.toIntOrNull()
        if (salario == null || meses == null) {
            _uiState.update { it.copy(resultado = null) }
            return
        }

        val resultado = calculator.calculate(
            FgtsInput(
                salarioBase = salario,
                mesesTrabalhados = meses,
                simularMulta = estadoAtual.simularMulta,
                tipoRescisaoParaMulta = estadoAtual.tipoRescisao,
            ),
        )
        _uiState.update { it.copy(resultado = resultado) }
    }
}
