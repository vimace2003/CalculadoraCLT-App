package com.calculadoraclt.domain.decimoterceiro

import com.calculadoraclt.domain.calculator.Calculator
import com.calculadoraclt.domain.calculator.InssCalculator
import com.calculadoraclt.domain.calculator.IrrfCalculator
import com.calculadoraclt.domain.taxtables.TaxTable
import java.math.BigDecimal
import java.math.RoundingMode

data class DecimoTerceiroInput(
    val salarioBase: BigDecimal,
    val mesesTrabalhadosNoAno: Int,
    val houveAdiantamento: Boolean,
    val numeroDependentes: Int = 0,
)

data class DecimoTerceiroResult(
    val valorBrutoProporcional: BigDecimal,
    val primeiraParcela: BigDecimal,
    val segundaParcelaBruta: BigDecimal,
    val descontoInss: BigDecimal,
    val descontoIrrf: BigDecimal,
    val segundaParcelaLiquida: BigDecimal,
    val totalLiquido: BigDecimal,
)

class DecimoTerceiroCalculator(
    private val tabela: TaxTable,
    private val inssCalculator: InssCalculator = InssCalculator(),
    private val irrfCalculator: IrrfCalculator = IrrfCalculator(),
) : Calculator<DecimoTerceiroInput, DecimoTerceiroResult> {
    override fun calculate(input: DecimoTerceiroInput): DecimoTerceiroResult {
        val meses = input.mesesTrabalhadosNoAno.coerceIn(0, 12)
        val valorBrutoProporcional = input.salarioBase
            .multiply(BigDecimal(meses))
            .divide(BigDecimal(12), 2, RoundingMode.HALF_UP)

        val primeiraParcela = if (input.houveAdiantamento) {
            valorBrutoProporcional.divide(BigDecimal(2), 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO.setScale(2)
        }
        val segundaParcelaBruta = valorBrutoProporcional.subtract(primeiraParcela).setScale(2, RoundingMode.HALF_UP)

        val descontoInss = inssCalculator.calcular(valorBrutoProporcional, tabela.inss).valorDesconto
        val descontoIrrf = irrfCalculator.calcular(
            rendimentoBruto = valorBrutoProporcional,
            descontoInss = descontoInss,
            numeroDependentes = input.numeroDependentes,
            tabela = tabela.irrf,
        ).valorDevido

        val segundaParcelaLiquida = segundaParcelaBruta.subtract(descontoInss).subtract(descontoIrrf).setScale(2, RoundingMode.HALF_UP)
        val totalLiquido = primeiraParcela.add(segundaParcelaLiquida).setScale(2, RoundingMode.HALF_UP)

        return DecimoTerceiroResult(
            valorBrutoProporcional = valorBrutoProporcional,
            primeiraParcela = primeiraParcela,
            segundaParcelaBruta = segundaParcelaBruta,
            descontoInss = descontoInss,
            descontoIrrf = descontoIrrf,
            segundaParcelaLiquida = segundaParcelaLiquida,
            totalLiquido = totalLiquido,
        )
    }
}
