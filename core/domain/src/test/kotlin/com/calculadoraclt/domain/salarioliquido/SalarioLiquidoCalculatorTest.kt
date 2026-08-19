package com.calculadoraclt.domain.salarioliquido

import com.calculadoraclt.domain.taxtables.TaxTables2026
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class SalarioLiquidoCalculatorTest {

    private val calculator = SalarioLiquidoCalculator(TaxTables2026)

    @Test
    fun `salario de R$5000 sem dependentes nem outros descontos`() {
        val resultado = calculator.calculate(SalarioLiquidoInput(salarioBruto = BigDecimal("5000.00")))
        assertEquals(BigDecimal("501.51"), resultado.descontoInss)
        assertEquals(BigDecimal("0.00"), resultado.descontoIrrf)
        assertEquals(BigDecimal("4498.49"), resultado.salarioLiquido)
    }

    @Test
    fun `outros descontos reduzem o salario liquido`() {
        val resultado = calculator.calculate(
            SalarioLiquidoInput(
                salarioBruto = BigDecimal("3000.00"),
                descontoValeTransporte = BigDecimal("180.00"),
                descontoValeAlimentacao = BigDecimal("50.00"),
                descontoPlanoSaude = BigDecimal("120.00"),
            ),
        )
        assertEquals(BigDecimal("350.00"), resultado.totalOutrosDescontos)
    }

    @Test
    fun `salario acima de R$7350 sofre irrf sem reducao`() {
        val resultado = calculator.calculate(SalarioLiquidoInput(salarioBruto = BigDecimal("8000.00")))
        assertEquals(BigDecimal("921.51"), resultado.descontoInss)
        assertEquals(BigDecimal("1037.85"), resultado.descontoIrrf)
    }
}
