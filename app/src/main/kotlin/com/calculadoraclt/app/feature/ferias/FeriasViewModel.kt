package com.calculadoraclt.app.feature.ferias

import androidx.lifecycle.ViewModel
import com.calculadoraclt.common.format.paraBigDecimalOuNull
import com.calculadoraclt.domain.ferias.FeriasCalculator
import com.calculadoraclt.domain.ferias.FeriasInput
import com.calculadoraclt.domain.ferias.FeriasResult
import com.calculadoraclt.domain.taxtables.TaxTables2026
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FeriasUiState(
    val salarioBase: String = "",
    val diasFerias: String = "30",
    val venderAbonoPecuniario: Boolean = false,
    val adiantar13: Boolean = false,
    val numeroDependentes: String = "0",
    val resultado: FeriasResult? = null,
)

class FeriasViewModel(
    private val calculator: FeriasCalculator = FeriasCalculator(TaxTables2026),
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeriasUiState())
    val uiState: StateFlow<FeriasUiState> = _uiState.asStateFlow()

    fun onSalarioChange(valor: String) {
        _uiState.update { it.copy(salarioBase = valor) }
        recalcular()
    }

    fun onDiasFeriasChange(valor: String) {
        _uiState.update { it.copy(diasFerias = valor) }
        recalcular()
    }

    fun onVenderAbonoChange(valor: Boolean) {
        _uiState.update { it.copy(venderAbonoPecuniario = valor) }
        recalcular()
    }

    fun onAdiantar13Change(valor: Boolean) {
        _uiState.update { it.copy(adiantar13 = valor) }
        recalcular()
    }

    fun onDependentesChange(valor: String) {
        _uiState.update { it.copy(numeroDependentes = valor) }
        recalcular()
    }

    private fun recalcular() {
        val estadoAtual = _uiState.value
        val salario = estadoAtual.salarioBase.paraBigDecimalOuNull()
        val dias = estadoAtual.diasFerias.toIntOrNull()
        if (salario == null || dias == null) {
            _uiState.update { it.copy(resultado = null) }
            return
        }

        val resultado = calculator.calculate(
            FeriasInput(
                salarioBase = salario,
                diasFerias = dias,
                venderAbonoPecuniario = estadoAtual.venderAbonoPecuniario,
                adiantar13 = estadoAtual.adiantar13,
                numeroDependentes = estadoAtual.numeroDependentes.toIntOrNull() ?: 0,
            ),
        )
        _uiState.update { it.copy(resultado = resultado) }
    }
}
