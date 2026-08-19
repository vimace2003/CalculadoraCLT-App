package com.calculadoraclt.domain.reajustes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class ReajusteCalculatorTest {

    private val calculator = ReajusteCalculator()

    @Test
    fun `reajuste de 10 por cento aumenta o salario proporcionalmente`() {
        val resultado = calculator.calculate(
            ReajusteInput(
                salarioAtual = BigDecimal("2000.00"),
                percentualReajuste = BigDecimal("10"),
                dataBase = LocalDate.of(2026, 1, 1),
                dataReferencia = LocalDate.of(2026, 1, 1),
            ),
        )
        assertEquals(BigDecimal("2200.00"), resultado.salarioNovo)
        assertEquals(BigDecimal("200.00"), resultado.diferenca)
    }

    @Test
    fun `sem meses retroativos nao calcula valor retroativo`() {
        val resultado = calculator.calculate(
            ReajusteInput(
                salarioAtual = BigDecimal("2000.00"),
                percentualReajuste = BigDecimal("10"),
                dataBase = LocalDate.of(2026, 6, 1),
                dataReferencia = LocalDate.of(2026, 6, 1),
            ),
        )
        assertEquals(0, resultado.mesesRetroativos)
        assertNull(resultado.valorRetroativoTotal)
    }

    @Test
    fun `retroativo de 3 meses multiplica a diferenca`() {
        val resultado = calculator.calculate(
            ReajusteInput(
                salarioAtual = BigDecimal("3000.00"),
                percentualReajuste = BigDecimal("5"),
                dataBase = LocalDate.of(2026, 1, 1),
                dataReferencia = LocalDate.of(2026, 4, 1),
            ),
        )
        assertEquals(3, resultado.mesesRetroativos)
        assertEquals(BigDecimal("150.00"), resultado.diferenca)
        assertEquals(BigDecimal("450.00"), resultado.valorRetroativoTotal)
    }
}
