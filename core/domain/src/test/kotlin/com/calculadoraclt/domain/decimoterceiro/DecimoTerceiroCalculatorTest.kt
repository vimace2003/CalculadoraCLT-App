package com.calculadoraclt.domain.decimoterceiro

import com.calculadoraclt.domain.taxtables.TaxTables2026
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class DecimoTerceiroCalculatorTest {

    private val calculator = DecimoTerceiroCalculator(TaxTables2026)

    @Test
    fun `12 meses trabalhados gera 13o integral`() {
        val resultado = calculator.calculate(
            DecimoTerceiroInput(salarioBase = BigDecimal("3000.00"), mesesTrabalhadosNoAno = 12, houveAdiantamento = false),
        )
        assertEquals(BigDecimal("3000.00"), resultado.valorBrutoProporcional)
    }

    @Test
    fun `6 meses trabalhados gera metade do 13o`() {
        val resultado = calculator.calculate(
            DecimoTerceiroInput(salarioBase = BigDecimal("3000.00"), mesesTrabalhadosNoAno = 6, houveAdiantamento = false),
        )
        assertEquals(BigDecimal("1500.00"), resultado.valorBrutoProporcional)
    }

    @Test
    fun `com adiantamento a primeira parcela e metade sem desconto`() {
        val resultado = calculator.calculate(
            DecimoTerceiroInput(salarioBase = BigDecimal("3000.00"), mesesTrabalhadosNoAno = 12, houveAdiantamento = true),
        )
        assertEquals(BigDecimal("1500.00"), resultado.primeiraParcela)
        assertEquals(BigDecimal("1500.00"), resultado.segundaParcelaBruta)
    }

    @Test
    fun `sem adiantamento toda a tributacao cai na segunda parcela`() {
        val resultado = calculator.calculate(
            DecimoTerceiroInput(salarioBase = BigDecimal("5000.00"), mesesTrabalhadosNoAno = 12, houveAdiantamento = false),
        )
        assertEquals(BigDecimal("0.00"), resultado.primeiraParcela)
        assertEquals(BigDecimal("501.51"), resultado.descontoInss)
        assertEquals(BigDecimal("0.00"), resultado.descontoIrrf)
        assertEquals(BigDecimal("4498.49"), resultado.segundaParcelaLiquida)
        assertEquals(BigDecimal("4498.49"), resultado.totalLiquido)
    }
}
