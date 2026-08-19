package com.calculadoraclt.domain.calculator

import com.calculadoraclt.domain.taxtables.TaxTables2026
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class InssCalculatorTest {

    private val calculator = InssCalculator()
    private val tabela = TaxTables2026.inss

    @Test
    fun `salario de R$5000 gera desconto de R$501,51`() {
        val resultado = calculator.calcular(BigDecimal("5000.00"), tabela)
        assertEquals(BigDecimal("501.51"), resultado.valorDesconto)
    }

    @Test
    fun `salario no piso da primeira faixa usa aliquota de 7,5 por cento`() {
        val resultado = calculator.calcular(BigDecimal("1621.00"), tabela)
        assertEquals(BigDecimal("121.58"), resultado.valorDesconto)
    }

    @Test
    fun `salario acima do teto e limitado ao desconto maximo`() {
        val resultado = calculator.calcular(BigDecimal("50000.00"), tabela)
        assertEquals(BigDecimal("988.09"), resultado.valorDesconto)
    }

    @Test
    fun `salario exatamente no teto de contribuicao`() {
        val resultado = calculator.calcular(BigDecimal("8475.55"), tabela)
        assertEquals(BigDecimal("988.09"), resultado.valorDesconto)
    }

    @Test
    fun `salario zero nao gera desconto`() {
        val resultado = calculator.calcular(BigDecimal.ZERO, tabela)
        assertEquals(BigDecimal.ZERO, resultado.valorDesconto)
    }
}
