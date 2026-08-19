package com.calculadoraclt.domain.salarioliquido

import com.calculadoraclt.domain.calculator.Calculator
import com.calculadoraclt.domain.calculator.InssCalculator
import com.calculadoraclt.domain.calculator.IrrfCalculator
import com.calculadoraclt.domain.taxtables.TaxTable
import java.math.BigDecimal
import java.math.RoundingMode

data class SalarioLiquidoInput(
    val salarioBruto: BigDecimal,
    val numeroDependentes: Int = 0,
    val descontoValeTransporte: BigDecimal = BigDecimal.ZERO,
    val descontoValeAlimentacao: BigDecimal = BigDecimal.ZERO,
    val descontoPlanoSaude: BigDecimal = BigDecimal.ZERO,
    val adiantamentos: BigDecimal = BigDecimal.ZERO,
)

data class SalarioLiquidoResult(
    val descontoInss: BigDecimal,
    val descontoIrrf: BigDecimal,
    val totalOutrosDescontos: BigDecimal,
    val salarioLiquido: BigDecimal,
)

class SalarioLiquidoCalculator(
    private val tabela: TaxTable,
    private val inssCalculator: InssCalculator = InssCalculator(),
    private val irrfCalculator: IrrfCalculator = IrrfCalculator(),
) : Calculator<SalarioLiquidoInput, SalarioLiquidoResult> {
    override fun calculate(input: SalarioLiquidoInput): SalarioLiquidoResult {
        val descontoInss = inssCalculator.calcular(input.salarioBruto, tabela.inss).valorDesconto
        val descontoIrrf = irrfCalculator.calcular(
            rendimentoBruto = input.salarioBruto,
            descontoInss = descontoInss,
            numeroDependentes = input.numeroDependentes,
            tabela = tabela.irrf,
        ).valorDevido

        val totalOutrosDescontos = input.descontoValeTransporte
            .add(input.descontoValeAlimentacao)
            .add(input.descontoPlanoSaude)
            .add(input.adiantamentos)
            .setScale(2, RoundingMode.HALF_UP)

        val salarioLiquido = input.salarioBruto
            .subtract(descontoInss)
            .subtract(descontoIrrf)
            .subtract(totalOutrosDescontos)
            .setScale(2, RoundingMode.HALF_UP)

        return SalarioLiquidoResult(descontoInss, descontoIrrf, totalOutrosDescontos, salarioLiquido)
    }
}
