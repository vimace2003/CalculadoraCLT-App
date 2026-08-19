package com.calculadoraclt.domain.calculator

import com.calculadoraclt.domain.taxtables.IrrfTable
import java.math.BigDecimal
import java.math.RoundingMode

data class IrrfResultado(
    val baseCalculo: BigDecimal,
    val impostoTradicional: BigDecimal,
    val redutorAplicado: BigDecimal,
    val valorDevido: BigDecimal,
)

class IrrfCalculator {
    fun calcular(
        rendimentoBruto: BigDecimal,
        descontoInss: BigDecimal,
        numeroDependentes: Int,
        tabela: IrrfTable,
    ): IrrfResultado {
        val deducaoDependentes = tabela.deducaoPorDependente.multiply(BigDecimal(numeroDependentes))
        val baseCalculo = rendimentoBruto.subtract(descontoInss).subtract(deducaoDependentes)
            .max(BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP)

        if (rendimentoBruto <= tabela.limiteIsencaoRedutor) {
            return IrrfResultado(baseCalculo, BigDecimal.ZERO.setScale(2), BigDecimal.ZERO.setScale(2), BigDecimal.ZERO.setScale(2))
        }

        val impostoTradicional = calcularImpostoProgressivo(baseCalculo, tabela)

        val redutor = if (rendimentoBruto <= tabela.limiteReducaoParcial) {
            tabela.redutorBase.subtract(tabela.redutorCoeficiente.multiply(rendimentoBruto))
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO.setScale(2)
        }

        val valorDevido = impostoTradicional.subtract(redutor).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP)

        return IrrfResultado(baseCalculo, impostoTradicional, redutor, valorDevido)
    }

    private fun calcularImpostoProgressivo(baseCalculo: BigDecimal, tabela: IrrfTable): BigDecimal {
        if (baseCalculo <= BigDecimal.ZERO) return BigDecimal.ZERO.setScale(2)

        val faixa = tabela.faixas.firstOrNull { it.ate == null || baseCalculo <= it.ate }
            ?: tabela.faixas.last()

        val imposto = baseCalculo.multiply(faixa.aliquota).subtract(faixa.deducao)
        return imposto.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP)
    }
}
