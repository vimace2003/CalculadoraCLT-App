package com.calculadoraclt.app.feature.rescisao

import androidx.lifecycle.ViewModel
import com.calculadoraclt.common.format.paraBigDecimalOuNull
import com.calculadoraclt.domain.rescisao.RescisaoCalculator
import com.calculadoraclt.domain.rescisao.RescisaoInput
import com.calculadoraclt.domain.rescisao.RescisaoResult
import com.calculadoraclt.domain.rescisao.TipoAvisoPrevio
import com.calculadoraclt.domain.rescisao.TipoRescisao
import com.calculadoraclt.domain.taxtables.TaxTables2026
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class RescisaoUiState(
    val tipoRescisao: TipoRescisao = TipoRescisao.SEM_JUSTA_CAUSA,
    val salarioBase: String = "",
    val dataAdmissao: LocalDate? = null,
    val dataDemissao: LocalDate? = null,
    val tipoAvisoPrevio: TipoAvisoPrevio = TipoAvisoPrevio.INDENIZADO,
    val possuiFeriasVencidas: Boolean = false,
    val numeroDependentes: String = "0",
    val resultado: RescisaoResult? = null,
)

class RescisaoViewModel(
    private val calculator: RescisaoCalculator = RescisaoCalculator(TaxTables2026),
) : ViewModel() {

    private val _uiState = MutableStateFlow(RescisaoUiState())
    val uiState: StateFlow<RescisaoUiState> = _uiState.asStateFlow()

    fun onTipoRescisaoChange(tipo: TipoRescisao) {
        _uiState.update { it.copy(tipoRescisao = tipo) }
        recalcular()
    }

    fun onSalarioChange(valor: String) {
        _uiState.update { it.copy(salarioBase = valor) }
        recalcular()
    }

    fun onDataAdmissaoChange(data: LocalDate) {
        _uiState.update { it.copy(dataAdmissao = data) }
        recalcular()
    }

    fun onDataDemissaoChange(data: LocalDate) {
        _uiState.update { it.copy(dataDemissao = data) }
        recalcular()
    }

    fun onTipoAvisoPrevioChange(tipo: TipoAvisoPrevio) {
        _uiState.update { it.copy(tipoAvisoPrevio = tipo) }
        recalcular()
    }

    fun onFeriasVencidasChange(valor: Boolean) {
        _uiState.update { it.copy(possuiFeriasVencidas = valor) }
        recalcular()
    }

    fun onDependentesChange(valor: String) {
        _uiState.update { it.copy(numeroDependentes = valor) }
        recalcular()
    }

    private fun recalcular() {
        val estadoAtual = _uiState.value
        val salario = estadoAtual.salarioBase.paraBigDecimalOuNull()
        val dataAdmissao = estadoAtual.dataAdmissao
        val dataDemissao = estadoAtual.dataDemissao
        if (salario == null || dataAdmissao == null || dataDemissao == null || !dataDemissao.isAfter(dataAdmissao)) {
            _uiState.update { it.copy(resultado = null) }
            return
        }

        val resultado = calculator.calculate(
            RescisaoInput(
                tipoRescisao = estadoAtual.tipoRescisao,
                salarioBase = salario,
                dataAdmissao = dataAdmissao,
                dataDemissao = dataDemissao,
                tipoAvisoPrevio = estadoAtual.tipoAvisoPrevio,
                possuiFeriasVencidas = estadoAtual.possuiFeriasVencidas,
                numeroDependentes = estadoAtual.numeroDependentes.toIntOrNull() ?: 0,
            ),
        )
        _uiState.update { it.copy(resultado = resultado) }
    }
}
