package com.calculadoraclt.domain.calculator

import com.calculadoraclt.domain.taxtables.InssTable
import java.math.BigDecimal
import java.math.RoundingMode

data class InssResultado(
    val baseCalculo: BigDecimal,
    val aliquotaEfetiva: BigDecimal,
    val valorDesconto: BigDecimal,
)

class InssCalculator {
    fun calcular(baseCalculo: BigDecimal, tabela: InssTable): InssResultado {
        if (baseCalculo <= BigDecimal.ZERO) {
            return InssResultado(baseCalculo, BigDecimal.ZERO, BigDecimal.ZERO)
        }

        var restante = baseCalculo.min(tabela.faixas.last().ate ?: baseCalculo)
        var faixaAnterior = BigDecimal.ZERO
        var total = BigDecimal.ZERO

        for (faixa in tabela.faixas) {
            val limiteFaixa = faixa.ate ?: restante
            val baseNaFaixa = restante.min(limiteFaixa).subtract(faixaAnterior)
            if (baseNaFaixa > BigDecimal.ZERO) {
                total = total.add(baseNaFaixa.multiply(faixa.aliquota))
            }
            faixaAnterior = limiteFaixa
            if (restante <= limiteFaixa) break
        }

        val valorDesconto = total.min(tabela.tetoContribuicao).setScale(2, RoundingMode.HALF_UP)
        val aliquotaEfetiva = if (baseCalculo > BigDecimal.ZERO) {
            valorDesconto.divide(baseCalculo, 4, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        return InssResultado(baseCalculo, aliquotaEfetiva, valorDesconto)
    }
}
