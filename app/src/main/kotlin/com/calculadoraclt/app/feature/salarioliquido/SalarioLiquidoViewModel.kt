package com.calculadoraclt.app.feature.salarioliquido

import androidx.lifecycle.ViewModel
import com.calculadoraclt.common.format.paraBigDecimalOuNull
import com.calculadoraclt.domain.salarioliquido.SalarioLiquidoCalculator
import com.calculadoraclt.domain.salarioliquido.SalarioLiquidoInput
import com.calculadoraclt.domain.salarioliquido.SalarioLiquidoResult
import com.calculadoraclt.domain.taxtables.TaxTables2026
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SalarioLiquidoUiState(
    val salarioBruto: String = "",
    val numeroDependentes: String = "0",
    val descontoValeTransporte: String = "",
    val descontoValeAlimentacao: String = "",
    val descontoPlanoSaude: String = "",
    val adiantamentos: String = "",
    val resultado: SalarioLiquidoResult? = null,
)

class SalarioLiquidoViewModel(
    private val calculator: SalarioLiquidoCalculator = SalarioLiquidoCalculator(TaxTables2026),
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalarioLiquidoUiState())
    val uiState: StateFlow<SalarioLiquidoUiState> = _uiState.asStateFlow()

    fun onSalarioChange(valor: String) {
        _uiState.update { it.copy(salarioBruto = valor) }
        recalcular()
    }

    fun onDependentesChange(valor: String) {
        _uiState.update { it.copy(numeroDependentes = valor) }
        recalcular()
    }

    fun onValeTransporteChange(valor: String) {
        _uiState.update { it.copy(descontoValeTransporte = valor) }
        recalcular()
    }

    fun onValeAlimentacaoChange(valor: String) {
        _uiState.update { it.copy(descontoValeAlimentacao = valor) }
        recalcular()
    }

    fun onPlanoSaudeChange(valor: String) {
        _uiState.update { it.copy(descontoPlanoSaude = valor) }
        recalcular()
    }

    fun onAdiantamentosChange(valor: String) {
        _uiState.update { it.copy(adiantamentos = valor) }
        recalcular()
    }

    private fun recalcular() {
        val estadoAtual = _uiState.value
        val salario = estadoAtual.salarioBruto.paraBigDecimalOuNull()
        if (salario == null) {
            _uiState.update { it.copy(resultado = null) }
            return
        }

        val resultado = calculator.calculate(
            SalarioLiquidoInput(
                salarioBruto = salario,
                numeroDependentes = estadoAtual.numeroDependentes.toIntOrNull() ?: 0,
                descontoValeTransporte = estadoAtual.descontoValeTransporte.paraBigDecimalOuNull() ?: BigDecimal.ZERO,
                descontoValeAlimentacao = estadoAtual.descontoValeAlimentacao.paraBigDecimalOuNull() ?: BigDecimal.ZERO,
                descontoPlanoSaude = estadoAtual.descontoPlanoSaude.paraBigDecimalOuNull() ?: BigDecimal.ZERO,
                adiantamentos = estadoAtual.adiantamentos.paraBigDecimalOuNull() ?: BigDecimal.ZERO,
            ),
        )
        _uiState.update { it.copy(resultado = resultado) }
    }
}
