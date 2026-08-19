package com.calculadoraclt.app.feature.salarioporhora

import androidx.lifecycle.ViewModel
import com.calculadoraclt.common.format.paraBigDecimalOuNull
import com.calculadoraclt.domain.salarioporhora.SalarioPorHoraCalculator
import com.calculadoraclt.domain.salarioporhora.SalarioPorHoraInput
import com.calculadoraclt.domain.salarioporhora.SalarioPorHoraResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SalarioPorHoraUiState(
    val salarioMensal: String = "",
    val cargaHorariaSemanal: String = "",
    val resultado: SalarioPorHoraResult? = null,
)

class SalarioPorHoraViewModel(
    private val calculator: SalarioPorHoraCalculator = SalarioPorHoraCalculator(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(SalarioPorHoraUiState())
    val uiState: StateFlow<SalarioPorHoraUiState> = _uiState.asStateFlow()

    fun onSalarioMensalChange(valor: String) {
        _uiState.update { it.copy(salarioMensal = valor) }
        recalcular()
    }

    fun onCargaHorariaChange(valor: String) {
        _uiState.update { it.copy(cargaHorariaSemanal = valor) }
        recalcular()
    }

    private fun recalcular() {
        val estadoAtual = _uiState.value
        val salario = estadoAtual.salarioMensal.paraBigDecimalOuNull()
        val carga = estadoAtual.cargaHorariaSemanal.paraBigDecimalOuNull()
        if (salario == null || carga == null) {
            _uiState.update { it.copy(resultado = null) }
            return
        }

        val resultado = calculator.calculate(SalarioPorHoraInput(salario, carga))
        _uiState.update { it.copy(resultado = resultado) }
    }
}
