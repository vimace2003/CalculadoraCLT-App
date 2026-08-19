package com.calculadoraclt.domain.fgts

import com.calculadoraclt.domain.calculator.Calculator
import com.calculadoraclt.domain.rescisao.TipoRescisao
import com.calculadoraclt.domain.rescisao.TipoRescisao.ACORDO_MUTUO
import com.calculadoraclt.domain.rescisao.TipoRescisao.SEM_JUSTA_CAUSA
import com.calculadoraclt.domain.taxtables.FgtsConfig
import java.math.BigDecimal
import java.math.RoundingMode

data class FgtsInput(
    val salarioBase: BigDecimal,
    val mesesTrabalhados: Int,
    val simularMulta: Boolean = false,
    val tipoRescisaoParaMulta: TipoRescisao? = null,
)

data class FgtsResult(
    val depositoMensal: BigDecimal,
    val saldoEstimadoTotal: BigDecimal,
    val multaRescisoria: BigDecimal?,
    val percentualMulta: BigDecimal?,
)

class FgtsCalculator(
    private val config: FgtsConfig,
) : Calculator<FgtsInput, FgtsResult> {
    override fun calculate(input: FgtsInput): FgtsResult {
        val depositoMensal = input.salarioBase.multiply(config.aliquotaMensal).setScale(2, RoundingMode.HALF_UP)
        val saldoEstimadoTotal = depositoMensal.multiply(BigDecimal(input.mesesTrabalhados))

        val percentualMulta = if (input.simularMulta) percentualMultaPara(input.tipoRescisaoParaMulta) else null
        val multaRescisoria = percentualMulta?.let {
            saldoEstimadoTotal.multiply(it).setScale(2, RoundingMode.HALF_UP)
        }

        return FgtsResult(
            depositoMensal = depositoMensal,
            saldoEstimadoTotal = saldoEstimadoTotal,
            multaRescisoria = multaRescisoria,
            percentualMulta = percentualMulta,
        )
    }

    private fun percentualMultaPara(tipo: TipoRescisao?): BigDecimal = when (tipo) {
        SEM_JUSTA_CAUSA -> config.multaSemJustaCausa
        ACORDO_MUTUO -> config.multaAcordoMutuo
        else -> BigDecimal.ZERO
    }
}
