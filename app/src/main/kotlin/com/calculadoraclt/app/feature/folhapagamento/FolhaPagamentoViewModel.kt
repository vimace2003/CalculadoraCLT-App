package com.calculadoraclt.app.feature.folhapagamento

import androidx.lifecycle.ViewModel
import com.calculadoraclt.common.format.paraBigDecimalOuNull
import com.calculadoraclt.domain.folhapagamento.FolhaPagamentoCalculator
import com.calculadoraclt.domain.folhapagamento.FolhaPagamentoInput
import com.calculadoraclt.domain.folhapagamento.FolhaPagamentoResult
import com.calculadoraclt.domain.folhapagamento.ItemFolha
import com.calculadoraclt.domain.taxtables.TaxTables2026
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class FolhaPagamentoUiState(
    val salarioBase: String = "",
    val numeroDependentes: String = "0",
    val descricaoProventoAdicional: String = "",
    val valorProventoAdicional: String = "",
    val descricaoDescontoAdicional: String = "",
    val valorDescontoAdicional: String = "",
    val resultado: FolhaPagamentoResult? = null,
)

class FolhaPagamentoViewModel(
    private val calculator: FolhaPagamentoCalculator = FolhaPagamentoCalculator(TaxTables2026),
) : ViewModel() {

    private val _uiState = MutableStateFlow(FolhaPagamentoUiState())
    val uiState: StateFlow<FolhaPagamentoUiState> = _uiState.asStateFlow()

    fun onSalarioChange(valor: String) {
        _uiState.update { it.copy(salarioBase = valor) }
        recalcular()
    }

    fun onDependentesChange(valor: String) {
        _uiState.update { it.copy(numeroDependentes = valor) }
        recalcular()
    }

    fun onDescricaoProventoChange(valor: String) {
        _uiState.update { it.copy(descricaoProventoAdicional = valor) }
        recalcular()
    }

    fun onValorProventoChange(valor: String) {
        _uiState.update { it.copy(valorProventoAdicional = valor) }
        recalcular()
    }

    fun onDescricaoDescontoChange(valor: String) {
        _uiState.update { it.copy(descricaoDescontoAdicional = valor) }
        recalcular()
    }

    fun onValorDescontoChange(valor: String) {
        _uiState.update { it.copy(valorDescontoAdicional = valor) }
        recalcular()
    }

    private fun recalcular() {
        val estadoAtual = _uiState.value
        val salario = estadoAtual.salarioBase.paraBigDecimalOuNull()
        if (salario == null) {
            _uiState.update { it.copy(resultado = null) }
            return
        }

        val proventosAdicionais = buildList {
            val valor = estadoAtual.valorProventoAdicional.paraBigDecimalOuNull()
            if (valor != null) {
                val descricao = estadoAtual.descricaoProventoAdicional.ifBlank { "Provento adicional" }
                add(ItemFolha(descricao, valor))
            }
        }
        val descontosAdicionais = buildList {
            val valor = estadoAtual.valorDescontoAdicional.paraBigDecimalOuNull()
            if (valor != null) {
                val descricao = estadoAtual.descricaoDescontoAdicional.ifBlank { "Desconto adicional" }
                add(ItemFolha(descricao, valor))
            }
        }

        val resultado = calculator.calculate(
            FolhaPagamentoInput(
                salarioBase = salario,
                proventosAdicionais = proventosAdicionais,
                descontosAdicionais = descontosAdicionais,
                numeroDependentes = estadoAtual.numeroDependentes.toIntOrNull() ?: 0,
            ),
        )
        _uiState.update { it.copy(resultado = resultado) }
    }
}
