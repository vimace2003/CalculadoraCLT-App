package com.calculadoraclt.domain.tempoentredatas

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TempoEntreDatasCalculatorTest {

    private val calculator = TempoEntreDatasCalculator()

    @Test
    fun `um ano exato entre datas`() {
        val resultado = calculator.calculate(
            TempoEntreDatasInput(LocalDate.of(2024, 1, 15), LocalDate.of(2025, 1, 15)),
        )
        assertEquals(1, resultado.anos)
        assertEquals(0, resultado.meses)
        assertEquals(0, resultado.dias)
        assertEquals(366L, resultado.totalDias)
    }

    @Test
    fun `anos meses e dias combinados`() {
        val resultado = calculator.calculate(
            TempoEntreDatasInput(LocalDate.of(2020, 3, 10), LocalDate.of(2026, 8, 18)),
        )
        assertEquals(6, resultado.anos)
        assertEquals(5, resultado.meses)
        assertEquals(8, resultado.dias)
    }

    @Test
    fun `datas invertidas produzem o mesmo resultado`() {
        val direto = calculator.calculate(
            TempoEntreDatasInput(LocalDate.of(2020, 3, 10), LocalDate.of(2026, 8, 18)),
        )
        val invertido = calculator.calculate(
            TempoEntreDatasInput(LocalDate.of(2026, 8, 18), LocalDate.of(2020, 3, 10)),
        )
        assertEquals(direto, invertido)
    }

    @Test
    fun `mesma data resulta em zero`() {
        val data = LocalDate.of(2026, 8, 18)
        val resultado = calculator.calculate(TempoEntreDatasInput(data, data))
        assertEquals(0, resultado.anos)
        assertEquals(0, resultado.meses)
        assertEquals(0, resultado.dias)
        assertEquals(0L, resultado.totalDias)
    }
}
