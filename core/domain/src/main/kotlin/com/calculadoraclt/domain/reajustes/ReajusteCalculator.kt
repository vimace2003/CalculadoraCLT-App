package com.calculadoraclt.domain.reajustes

import com.calculadoraclt.domain.calculator.Calculator
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit.MONTHS

data class ReajusteInput(
    val salarioAtual: BigDecimal,
    val percentualReajuste: BigDecimal,
    val dataBase: LocalDate,
    val dataReferencia: LocalDate = LocalDate.now(),
)

data class ReajusteResult(
    val salarioNovo: BigDecimal,
    val diferenca: BigDecimal,
    val mesesRetroativos: Int,
    val valorRetroativoTotal: BigDecimal?,
)

class ReajusteCalculator : Calculator<ReajusteInput, ReajusteResult> {
    override fun calculate(input: ReajusteInput): ReajusteResult {
        val fator = BigDecimal.ONE.add(input.percentualReajuste.divide(BigDecimal(100)))
        val salarioNovo = input.salarioAtual.multiply(fator).setScale(2, RoundingMode.HALF_UP)
        val diferenca = salarioNovo.subtract(input.salarioAtual).setScale(2, RoundingMode.HALF_UP)

        val mesesRetroativos = MONTHS.between(input.dataBase, input.dataReferencia).toInt().coerceAtLeast(0)
        val valorRetroativoTotal = if (mesesRetroativos > 0) {
            diferenca.multiply(BigDecimal(mesesRetroativos)).setScale(2, RoundingMode.HALF_UP)
        } else {
            null
        }

        return ReajusteResult(salarioNovo, diferenca, mesesRetroativos, valorRetroativoTotal)
    }
}
