package com.calculadoraclt.domain.ferias

import com.calculadoraclt.domain.calculator.Calculator
import com.calculadoraclt.domain.calculator.InssCalculator
import com.calculadoraclt.domain.calculator.IrrfCalculator
import com.calculadoraclt.domain.taxtables.TaxTable
import java.math.BigDecimal
import java.math.RoundingMode

private val DIAS_MES = BigDecimal("30")
private const val DIAS_MAXIMOS_ABONO = 10

data class FeriasInput(
    val salarioBase: BigDecimal,
    val diasFerias: Int,
    val venderAbonoPecuniario: Boolean = false,
    val adiantar13: Boolean = false,
    val numeroDependentes: Int = 0,
)

data class FeriasResult(
    val valorFerias: BigDecimal,
    val tercoConstitucional: BigDecimal,
    val valorAbono: BigDecimal?,
    val tercoAbono: BigDecimal?,
    val adiantamento13: BigDecimal?,
    val totalBruto: BigDecimal,
    val descontoInss: BigDecimal,
    val descontoIrrf: BigDecimal,
    val totalLiquido: BigDecimal,
)

/**
 * Simplificação assumida: INSS/IRRF incidem apenas sobre férias gozadas + 1/3 constitucional.
 * O abono pecuniário (venda de dias) e o adiantamento de 13º são tratados como isentos nesta
 * calculadora, seguindo a regra geral — casos específicos podem variar.
 */
class FeriasCalculator(
    private val tabela: TaxTable,
    private val inssCalculator: InssCalculator = InssCalculator(),
    private val irrfCalculator: IrrfCalculator = IrrfCalculator(),
) : Calculator<FeriasInput, FeriasResult> {
    override fun calculate(input: FeriasInput): FeriasResult {
        val dias = input.diasFerias.coerceIn(1, 30)
        val valorDia = input.salarioBase.divide(DIAS_MES, 10, RoundingMode.HALF_UP)

        val valorFerias = valorDia.multiply(BigDecimal(dias)).setScale(2, RoundingMode.HALF_UP)
        val tercoConstitucional = valorFerias.divide(BigDecimal(3), 2, RoundingMode.HALF_UP)

        val valorAbono = if (input.venderAbonoPecuniario) {
            valorDia.multiply(BigDecimal(DIAS_MAXIMOS_ABONO)).setScale(2, RoundingMode.HALF_UP)
        } else {
            null
        }
        val tercoAbono = valorAbono?.divide(BigDecimal(3), 2, RoundingMode.HALF_UP)

        val adiantamento13 = if (input.adiantar13) {
            input.salarioBase.divide(BigDecimal(2), 2, RoundingMode.HALF_UP)
        } else {
            null
        }

        val baseTributavel = valorFerias.add(tercoConstitucional)
        val descontoInss = inssCalculator.calcular(baseTributavel, tabela.inss).valorDesconto
        val descontoIrrf = irrfCalculator.calcular(
            rendimentoBruto = baseTributavel,
            descontoInss = descontoInss,
            numeroDependentes = input.numeroDependentes,
            tabela = tabela.irrf,
        ).valorDevido

        val totalBruto = valorFerias
            .add(tercoConstitucional)
            .add(valorAbono ?: BigDecimal.ZERO)
            .add(tercoAbono ?: BigDecimal.ZERO)
            .add(adiantamento13 ?: BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP)

        val totalLiquido = totalBruto.subtract(descontoInss).subtract(descontoIrrf).setScale(2, RoundingMode.HALF_UP)

        return FeriasResult(
            valorFerias = valorFerias,
            tercoConstitucional = tercoConstitucional,
            valorAbono = valorAbono,
            tercoAbono = tercoAbono,
            adiantamento13 = adiantamento13,
            totalBruto = totalBruto,
            descontoInss = descontoInss,
            descontoIrrf = descontoIrrf,
            totalLiquido = totalLiquido,
        )
    }
}
