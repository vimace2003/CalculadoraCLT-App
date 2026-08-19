package com.calculadoraclt.app.feature.segurodesemprego

import androidx.lifecycle.ViewModel
import com.calculadoraclt.common.format.paraBigDecimalOuNull
import com.calculadoraclt.domain.segurodesemprego.SeguroDesempregoCalculator
import com.calculadoraclt.domain.segurodesemprego.SeguroDesempregoInput
import com.calculadoraclt.domain.segurodesemprego.SeguroDesempregoResult
import com.calculadoraclt.domain.taxtables.TaxTables2026
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SeguroDesempregoUiState(
    val salario1: String = "",
    val salario2: String = "",
    val salario3: String = "",
    val numeroSolicitacoesAnteriores: String = "0",
    val mesesTrabalhadosUltimoVinculo: String = "",
    val resultado: SeguroDesempregoResult? = null,
)

class SeguroDesempregoViewModel(
    private val calculator: SeguroDesempregoCalculator = SeguroDesempregoCalculator(TaxTables2026),
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeguroDesempregoUiState())
    val uiState: StateFlow<SeguroDesempregoUiState> = _uiState.asStateFlow()

    fun onSalario1Change(valor: String) {
        _uiState.update { it.copy(salario1 = valor) }
        recalcular()
    }

    fun onSalario2Change(valor: String) {
        _uiState.update { it.copy(salario2 = valor) }
        recalcular()
    }

    fun onSalario3Change(valor: String) {
        _uiState.update { it.copy(salario3 = valor) }
        recalcular()
    }

    fun onSolicitacoesChange(valor: String) {
        _uiState.update { it.copy(numeroSolicitacoesAnteriores = valor) }
        recalcular()
    }

    fun onMesesTrabalhadosChange(valor: String) {
        _uiState.update { it.copy(mesesTrabalhadosUltimoVinculo = valor) }
        recalcular()
    }

    private fun recalcular() {
        val estadoAtual = _uiState.value
        val salarios = listOfNotNull(
            estadoAtual.salario1.paraBigDecimalOuNull(),
            estadoAtual.salario2.paraBigDecimalOuNull(),
            estadoAtual.salario3.paraBigDecimalOuNull(),
        )
        val meses = estadoAtual.mesesTrabalhadosUltimoVinculo.toIntOrNull()
        if (salarios.isEmpty() || meses == null) {
            _uiState.update { it.copy(resultado = null) }
            return
        }

        val resultado = calculator.calculate(
            SeguroDesempregoInput(
                salarios = salarios,
                numeroSolicitacoesAnteriores = estadoAtual.numeroSolicitacoesAnteriores.toIntOrNull() ?: 0,
                mesesTrabalhadosUltimoVinculo = meses,
            ),
        )
        _uiState.update { it.copy(resultado = resultado) }
    }
}
