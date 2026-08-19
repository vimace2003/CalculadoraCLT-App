package com.calculadoraclt.domain.rescisao

import com.calculadoraclt.domain.taxtables.TaxTables2026
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class RescisaoCalculatorTest {

    private val calculator = RescisaoCalculator(TaxTables2026)

    @Test
    fun `rescisao sem justa causa com aviso indenizado - cenario completo`() {
        val resultado = calculator.calculate(
            RescisaoInput(
                tipoRescisao = TipoRescisao.SEM_JUSTA_CAUSA,
                salarioBase = BigDecimal("3000.00"),
                dataAdmissao = LocalDate.of(2024, 1, 10),
                dataDemissao = LocalDate.of(2026, 8, 18),
                tipoAvisoPrevio = TipoAvisoPrevio.INDENIZADO,
                possuiFeriasVencidas = false,
            ),
        )

        assertEquals(36, resultado.diasAvisoPrevio)
        assertEquals(BigDecimal("1800.00"), resultado.saldoSalario)
        assertEquals(BigDecimal("3600.00"), resultado.avisoPrevioIndenizado)
        assertNull(resultado.feriasVencidas)
        assertEquals(BigDecimal("2333.33"), resultado.feriasProporcionais)
        assertEquals(BigDecimal("2000.00"), resultado.decimoTerceiroProporcional)
        assertEquals(BigDecimal("2976.00"), resultado.fgtsMultaRescisoria)
        assertEquals(BigDecimal("0.40"), resultado.percentualMultaFgts)
        assertEquals(BigDecimal("12709.33"), resultado.totalBruto)
        assertEquals(BigDecimal("540.29"), resultado.totalDescontos)
        assertEquals(BigDecimal("12169.04"), resultado.totalLiquido)
    }

    @Test
    fun `aviso previo trabalhado nao gera valor indenizado`() {
        val resultado = calculator.calculate(
            RescisaoInput(
                tipoRescisao = TipoRescisao.SEM_JUSTA_CAUSA,
                salarioBase = BigDecimal("3000.00"),
                dataAdmissao = LocalDate.of(2024, 1, 10),
                dataDemissao = LocalDate.of(2026, 8, 18),
                tipoAvisoPrevio = TipoAvisoPrevio.TRABALHADO,
            ),
        )
        assertNull(resultado.avisoPrevioIndenizado)
    }

    @Test
    fun `pedido de demissao nao gera multa de fgts`() {
        val resultado = calculator.calculate(
            RescisaoInput(
                tipoRescisao = TipoRescisao.PEDIDO_DEMISSAO,
                salarioBase = BigDecimal("3000.00"),
                dataAdmissao = LocalDate.of(2024, 1, 10),
                dataDemissao = LocalDate.of(2026, 8, 18),
                tipoAvisoPrevio = TipoAvisoPrevio.TRABALHADO,
            ),
        )
        assertEquals(BigDecimal("0.00"), resultado.fgtsMultaRescisoria)
        assertEquals(BigDecimal.ZERO, resultado.percentualMultaFgts)
    }

    @Test
    fun `dias de aviso previo sao limitados a 90`() {
        val resultado = calculator.calculate(
            RescisaoInput(
                tipoRescisao = TipoRescisao.SEM_JUSTA_CAUSA,
                salarioBase = BigDecimal("3000.00"),
                dataAdmissao = LocalDate.of(2000, 1, 1),
                dataDemissao = LocalDate.of(2026, 1, 1),
                tipoAvisoPrevio = TipoAvisoPrevio.INDENIZADO,
            ),
        )
        assertEquals(90, resultado.diasAvisoPrevio)
    }

    @Test
    fun `ferias vencidas somam salario mais um terco ao bruto`() {
        val resultado = calculator.calculate(
            RescisaoInput(
                tipoRescisao = TipoRescisao.ACORDO_MUTUO,
                salarioBase = BigDecimal("3000.00"),
                dataAdmissao = LocalDate.of(2024, 1, 10),
                dataDemissao = LocalDate.of(2026, 8, 18),
                tipoAvisoPrevio = TipoAvisoPrevio.DISPENSADO,
                possuiFeriasVencidas = true,
            ),
        )
        assertEquals(BigDecimal("4000.00"), resultado.feriasVencidas)
    }
}
