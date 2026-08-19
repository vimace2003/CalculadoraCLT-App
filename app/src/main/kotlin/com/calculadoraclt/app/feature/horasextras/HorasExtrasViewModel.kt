package com.calculadoraclt.app.feature.horasextras

import androidx.lifecycle.ViewModel
import com.calculadoraclt.common.format.paraBigDecimalOuNull
import com.calculadoraclt.domain.horasextras.HorasExtrasCalculator
import com.calculadoraclt.domain.horasextras.HorasExtrasInput
import com.calculadoraclt.domain.horasextras.HorasExtrasResult
import com.calculadoraclt.domain.horasextras.PercentualHoraExtra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal

data class HorasExtrasUiState(
    val salarioBase: String = "",
    val cargaHorariaMensal: String = "220",
    val quantidadeHoras: String = "",
    val percentual: PercentualHoraExtra = PercentualHoraExtra.CINQUENTA,
    val percentualPersonalizado: String = "",
    val resultado: HorasExtrasResult? = null,
)

class HorasExtrasViewModel(
    private val calculator: HorasExtrasCalculator = HorasExtrasCalculator(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(HorasExtrasUiState())
    val uiState: StateFlow<HorasExtrasUiState> = _uiState.asStateFlow()

    fun onSalarioChange(valor: String) {
        _uiState.update { it.copy(salarioBase = valor) }
        recalcular()
    }

    fun onCargaHorariaChange(valor: String) {
        _uiState.update { it.copy(cargaHorariaMensal = valor) }
        recalcular()
    }

    fun onQuantidadeHorasChange(valor: String) {
        _uiState.update { it.copy(quantidadeHoras = valor) }
        recalcular()
    }

    fun onPercentualChange(percentual: PercentualHoraExtra) {
        _uiState.update { it.copy(percentual = percentual) }
        recalcular()
    }

    fun onPercentualPersonalizadoChange(valor: String) {
        _uiState.update { it.copy(percentualPersonalizado = valor) }
        recalcular()
    }

    private fun recalcular() {
        val estadoAtual = _uiState.value
        val salario = estadoAtual.salarioBase.paraBigDecimalOuNull()
        val cargaHoraria = estadoAtual.cargaHorariaMensal.paraBigDecimalOuNull()
        val quantidadeHoras = estadoAtual.quantidadeHoras.paraBigDecimalOuNull()
        if (salario == null || cargaHoraria == null || quantidadeHoras == null) {
            _uiState.update { it.copy(resultado = null) }
            return
        }

        val percentualPersonalizado = estadoAtual.percentualPersonalizado.paraBigDecimalOuNull()
            ?.divide(BigDecimal(100))

        val resultado = calculator.calculate(
            HorasExtrasInput(
                salarioBase = salario,
                cargaHorariaMensal = cargaHoraria,
                quantidadeHoras = quantidadeHoras,
                percentual = estadoAtual.percentual,
                percentualPersonalizado = percentualPersonalizado,
            ),
        )
        _uiState.update { it.copy(resultado = resultado) }
    }
}
