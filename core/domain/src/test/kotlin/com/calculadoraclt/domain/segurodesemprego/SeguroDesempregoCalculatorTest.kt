package com.calculadoraclt.domain.segurodesemprego

import com.calculadoraclt.domain.taxtables.TaxTables2026
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class SeguroDesempregoCalculatorTest {

    private val calculator = SeguroDesempregoCalculator(TaxTables2026)

    @Test
    fun `media abaixo do piso recebe o valor minimo`() {
        val resultado = calculator.calculate(
            SeguroDesempregoInput(
                salarios = listOf(BigDecimal("2000"), BigDecimal("2000"), BigDecimal("2000")),
                numeroSolicitacoesAnteriores = 0,
                mesesTrabalhadosUltimoVinculo = 12,
            ),
        )
        assertEquals(BigDecimal("2000.00"), resultado.mediaSalarial)
        assertEquals(BigDecimal("1621.00"), resultado.valorParcela)
    }

    @Test
    fun `media na segunda faixa aplica percentual sobre o excedente`() {
        val resultado = calculator.calculate(
            SeguroDesempregoInput(
                salarios = listOf(BigDecimal("3000"), BigDecimal("3000"), BigDecimal("3000")),
                numeroSolicitacoesAnteriores = 0,
                mesesTrabalhadosUltimoVinculo = 12,
            ),
        )
        assertEquals(BigDecimal("2166.66"), resultado.valorParcela)
    }

    @Test
    fun `media alta recebe o teto`() {
        val resultado = calculator.calculate(
            SeguroDesempregoInput(
                salarios = listOf(BigDecimal("5000"), BigDecimal("5000"), BigDecimal("5000")),
                numeroSolicitacoesAnteriores = 0,
                mesesTrabalhadosUltimoVinculo = 12,
            ),
        )
        assertEquals(BigDecimal("2518.65"), resultado.valorParcela)
    }

    @Test
    fun `primeira solicitacao com menos de 12 meses e inelegivel`() {
        val resultado = calculator.calculate(
            SeguroDesempregoInput(
                salarios = listOf(BigDecimal("2000")),
                numeroSolicitacoesAnteriores = 0,
                mesesTrabalhadosUltimoVinculo = 6,
            ),
        )
        assertFalse(resultado.elegivel)
        assertEquals(0, resultado.numeroParcelas)
    }

    @Test
    fun `primeira solicitacao com 24 meses ou mais gera 5 parcelas`() {
        val resultado = calculator.calculate(
            SeguroDesempregoInput(
                salarios = listOf(BigDecimal("2000")),
                numeroSolicitacoesAnteriores = 0,
                mesesTrabalhadosUltimoVinculo = 24,
            ),
        )
        assertTrue(resultado.elegivel)
        assertEquals(5, resultado.numeroParcelas)
    }

    @Test
    fun `terceira solicitacao com 6 meses ja e elegivel com 3 parcelas`() {
        val resultado = calculator.calculate(
            SeguroDesempregoInput(
                salarios = listOf(BigDecimal("2000")),
                numeroSolicitacoesAnteriores = 2,
                mesesTrabalhadosUltimoVinculo = 6,
            ),
        )
        assertTrue(resultado.elegivel)
        assertEquals(3, resultado.numeroParcelas)
    }
}
