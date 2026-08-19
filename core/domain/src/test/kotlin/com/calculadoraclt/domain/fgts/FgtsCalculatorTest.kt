package com.calculadoraclt.domain.fgts

import com.calculadoraclt.domain.rescisao.TipoRescisao
import com.calculadoraclt.domain.taxtables.TaxTables2026
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.math.BigDecimal

class FgtsCalculatorTest {

    private val calculator = FgtsCalculator(TaxTables2026.fgts)

    @Test
    fun `deposito mensal e 8 por cento do salario`() {
        val resultado = calculator.calculate(FgtsInput(BigDecimal("2000.00"), mesesTrabalhados = 1))
        assertEquals(BigDecimal("160.00"), resultado.depositoMensal)
    }

    @Test
    fun `saldo estimado acumula ao longo dos meses`() {
        val resultado = calculator.calculate(FgtsInput(BigDecimal("2000.00"), mesesTrabalhados = 12))
        assertEquals(BigDecimal("1920.00"), resultado.saldoEstimadoTotal)
    }

    @Test
    fun `sem simular multa retorna nulo`() {
        val resultado = calculator.calculate(FgtsInput(BigDecimal("2000.00"), mesesTrabalhados = 12))
        assertNull(resultado.multaRescisoria)
        assertNull(resultado.percentualMulta)
    }

    @Test
    fun `multa de 40 por cento para demissao sem justa causa`() {
        val resultado = calculator.calculate(
            FgtsInput(
                salarioBase = BigDecimal("2000.00"),
                mesesTrabalhados = 12,
                simularMulta = true,
                tipoRescisaoParaMulta = TipoRescisao.SEM_JUSTA_CAUSA,
            ),
        )
        assertEquals(BigDecimal("768.00"), resultado.multaRescisoria)
    }

    @Test
    fun `multa de 20 por cento para acordo mutuo`() {
        val resultado = calculator.calculate(
            FgtsInput(
                salarioBase = BigDecimal("2000.00"),
                mesesTrabalhados = 12,
                simularMulta = true,
                tipoRescisaoParaMulta = TipoRescisao.ACORDO_MUTUO,
            ),
        )
        assertEquals(BigDecimal("384.00"), resultado.multaRescisoria)
    }

    @Test
    fun `sem multa para pedido de demissao`() {
        val resultado = calculator.calculate(
            FgtsInput(
                salarioBase = BigDecimal("2000.00"),
                mesesTrabalhados = 12,
                simularMulta = true,
                tipoRescisaoParaMulta = TipoRescisao.PEDIDO_DEMISSAO,
            ),
        )
        assertEquals(BigDecimal("0.00"), resultado.multaRescisoria)
    }
}
