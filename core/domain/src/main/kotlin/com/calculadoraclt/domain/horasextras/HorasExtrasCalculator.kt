package com.calculadoraclt.domain.horasextras

import com.calculadoraclt.domain.calculator.Calculator
import java.math.BigDecimal
import java.math.RoundingMode

enum class PercentualHoraExtra { CINQUENTA, CEM, PERSONALIZADO }

data class HorasExtrasInput(
    val salarioBase: BigDecimal,
    val cargaHorariaMensal: BigDecimal = BigDecimal("220"),
    val quantidadeHoras: BigDecimal,
    val percentual: PercentualHoraExtra,
    val percentualPersonalizado: BigDecimal? = null,
    val calcularReflexoDsr: Boolean = false,
    val diasUteisMes: Int? = null,
    val domingosEFeriadosMes: Int? = null,
)

data class HorasExtrasResult(
    val valorHoraNormal: BigDecimal,
    val valorHoraExtra: BigDecimal,
    val totalHorasExtras: BigDecimal,
    val reflexoDsr: BigDecimal?,
    val totalGeral: BigDecimal,
)

class HorasExtrasCalculator : Calculator<HorasExtrasInput, HorasExtrasResult> {
    override fun calculate(input: HorasExtrasInput): HorasExtrasResult {
        val percentualEfetivo = when (input.percentual) {
            PercentualHoraExtra.CINQUENTA -> BigDecimal("0.50")
            PercentualHoraExtra.CEM -> BigDecimal("1.00")
            PercentualHoraExtra.PERSONALIZADO -> input.percentualPersonalizado ?: BigDecimal("0.50")
        }

        val valorHoraNormal = input.salarioBase.divide(input.cargaHorariaMensal, 2, RoundingMode.HALF_UP)
        val valorHoraExtra = valorHoraNormal.multiply(BigDecimal.ONE.add(percentualEfetivo)).setScale(2, RoundingMode.HALF_UP)
        val totalHorasExtras = valorHoraExtra.multiply(input.quantidadeHoras).setScale(2, RoundingMode.HALF_UP)

        val reflexoDsr = if (input.calcularReflexoDsr && input.diasUteisMes != null && input.diasUteisMes > 0 && input.domingosEFeriadosMes != null) {
            totalHorasExtras.divide(BigDecimal(input.diasUteisMes), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal(input.domingosEFeriadosMes))
                .setScale(2, RoundingMode.HALF_UP)
        } else {
            null
        }

        val totalGeral = totalHorasExtras.add(reflexoDsr ?: BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP)

        return HorasExtrasResult(valorHoraNormal, valorHoraExtra, totalHorasExtras, reflexoDsr, totalGeral)
    }
}
