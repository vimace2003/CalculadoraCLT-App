package com.calculadoraclt.domain.salarioporhora

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class SalarioPorHoraCalculatorTest {

    private val calculator = SalarioPorHoraCalculator()

    @Test
    fun `jornada padrao de 44 horas semanais usa divisor 220`() {
        val resultado = calculator.calculate(
            SalarioPorHoraInput(BigDecimal("2200.00"), BigDecimal("44")),
        )
        assertEquals(BigDecimal("10.00"), resultado.valorHora)
        assertEquals(BigDecimal("0.17"), resultado.valorMinuto)
    }

    @Test
    fun `jornada de 30 horas semanais usa divisor 150`() {
        val resultado = calculator.calculate(
            SalarioPorHoraInput(BigDecimal("1500.00"), BigDecimal("30")),
        )
        assertEquals(BigDecimal("10.00"), resultado.valorHora)
    }

    @Test
    fun `carga horaria zero retorna zero sem dividir por zero`() {
        val resultado = calculator.calculate(
            SalarioPorHoraInput(BigDecimal("2000.00"), BigDecimal.ZERO),
        )
        assertEquals(BigDecimal("0.00"), resultado.valorHora)
        assertEquals(BigDecimal("0.00"), resultado.valorMinuto)
    }
}
