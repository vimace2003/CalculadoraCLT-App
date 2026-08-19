package com.calculadoraclt.domain.horasextras

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class HorasExtrasCalculatorTest {

    private val calculator = HorasExtrasCalculator()

    @Test
    fun `hora extra a 50 por cento sobre salario de R$2200 e divisor 220`() {
        val resultado = calculator.calculate(
            HorasExtrasInput(
                salarioBase = BigDecimal("2200.00"),
                cargaHorariaMensal = BigDecimal("220"),
                quantidadeHoras = BigDecimal("10"),
                percentual = PercentualHoraExtra.CINQUENTA,
            ),
        )
        assertEquals(BigDecimal("10.00"), resultado.valorHoraNormal)
        assertEquals(BigDecimal("15.00"), resultado.valorHoraExtra)
        assertEquals(BigDecimal("150.00"), resultado.totalHorasExtras)
        assertNull(resultado.reflexoDsr)
    }

    @Test
    fun `hora extra a 100 por cento dobra o valor da hora`() {
        val resultado = calculator.calculate(
            HorasExtrasInput(
                salarioBase = BigDecimal("2200.00"),
                quantidadeHoras = BigDecimal("5"),
                percentual = PercentualHoraExtra.CEM,
            ),
        )
        assertEquals(BigDecimal("20.00"), resultado.valorHoraExtra)
        assertEquals(BigDecimal("100.00"), resultado.totalHorasExtras)
    }

    @Test
    fun `reflexo de dsr e somado ao total geral quando habilitado`() {
        val resultado = calculator.calculate(
            HorasExtrasInput(
                salarioBase = BigDecimal("2200.00"),
                quantidadeHoras = BigDecimal("10"),
                percentual = PercentualHoraExtra.CINQUENTA,
                calcularReflexoDsr = true,
                diasUteisMes = 25,
                domingosEFeriadosMes = 5,
            ),
        )
        assertEquals(BigDecimal("30.00"), resultado.reflexoDsr)
        assertEquals(BigDecimal("180.00"), resultado.totalGeral)
    }
}
