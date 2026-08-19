package com.calculadoraclt.domain.ferias

import com.calculadoraclt.domain.taxtables.TaxTables2026
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class FeriasCalculatorTest {

    private val calculator = FeriasCalculator(TaxTables2026)

    @Test
    fun `30 dias de ferias sem abono nem adiantamento`() {
        val resultado = calculator.calculate(
            FeriasInput(salarioBase = BigDecimal("3000.00"), diasFerias = 30),
        )
        assertEquals(BigDecimal("3000.00"), resultado.valorFerias)
        assertEquals(BigDecimal("1000.00"), resultado.tercoConstitucional)
        assertNull(resultado.valorAbono)
        assertNull(resultado.adiantamento13)
        assertEquals(BigDecimal("368.60"), resultado.descontoInss)
        assertEquals(BigDecimal("0.00"), resultado.descontoIrrf)
        assertEquals(BigDecimal("3631.40"), resultado.totalLiquido)
    }

    @Test
    fun `abono pecuniario vende 10 dias e soma o terco do abono`() {
        val resultado = calculator.calculate(
            FeriasInput(salarioBase = BigDecimal("3000.00"), diasFerias = 20, venderAbonoPecuniario = true),
        )
        assertEquals(BigDecimal("1000.00"), resultado.valorAbono)
        assertEquals(BigDecimal("333.33"), resultado.tercoAbono)
    }

    @Test
    fun `adiantamento de 13o soma metade do salario ao total bruto`() {
        val resultado = calculator.calculate(
            FeriasInput(salarioBase = BigDecimal("3000.00"), diasFerias = 30, adiantar13 = true),
        )
        assertEquals(BigDecimal("1500.00"), resultado.adiantamento13)
    }
}
