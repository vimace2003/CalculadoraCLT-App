package com.calculadoraclt.app.feature.decimoterceiro

import androidx.lifecycle.ViewModel
import com.calculadoraclt.common.format.paraBigDecimalOuNull
import com.calculadoraclt.domain.decimoterceiro.DecimoTerceiroCalculator
import com.calculadoraclt.domain.decimoterceiro.DecimoTerceiroInput
import com.calculadoraclt.domain.decimoterceiro.DecimoTerceiroResult
import com.calculadoraclt.domain.taxtables.TaxTables2026
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DecimoTerceiroUiState(
    val salarioBase: String = "",
    val mesesTrabalhadosNoAno: String = "12",
    val houveAdiantamento: Boolean = false,
    val numeroDependentes: String = "0",
    val resultado: DecimoTerceiroResult? = null,
)

class DecimoTerceiroViewModel(
    private val calculator: DecimoTerceiroCalculator = DecimoTerceiroCalculator(TaxTables2026),
) : ViewModel() {

    private val _uiState = MutableStateFlow(DecimoTerceiroUiState())
    val uiState: StateFlow<DecimoTerceiroUiState> = _uiState.asStateFlow()

    fun onSalarioChange(valor: String) {
        _uiState.update { it.copy(salarioBase = valor) }
        recalcular()
    }

    fun onMesesChange(valor: String) {
        _uiState.update { it.copy(mesesTrabalhadosNoAno = valor) }
        recalcular()
    }

    fun onAdiantamentoChange(valor: Boolean) {
        _uiState.update { it.copy(houveAdiantamento = valor) }
        recalcular()
    }

    fun onDependentesChange(valor: String) {
        _uiState.update { it.copy(numeroDependentes = valor) }
        recalcular()
    }

    private fun recalcular() {
        val estadoAtual = _uiState.value
        val salario = estadoAtual.salarioBase.paraBigDecimalOuNull()
        val meses = estadoAtual.mesesTrabalhadosNoAno.toIntOrNull()
        if (salario == null || meses == null) {
            _uiState.update { it.copy(resultado = null) }
            return
        }

        val resultado = calculator.calculate(
            DecimoTerceiroInput(
                salarioBase = salario,
                mesesTrabalhadosNoAno = meses,
                houveAdiantamento = estadoAtual.houveAdiantamento,
                numeroDependentes = estadoAtual.numeroDependentes.toIntOrNull() ?: 0,
            ),
        )
        _uiState.update { it.copy(resultado = resultado) }
    }
}
