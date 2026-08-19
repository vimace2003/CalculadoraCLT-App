package com.calculadoraclt.domain.folhapagamento

import com.calculadoraclt.domain.taxtables.TaxTables2026
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class FolhaPagamentoCalculatorTest {

    private val calculator = FolhaPagamentoCalculator(TaxTables2026)

    @Test
    fun `sem itens adicionais replica o comportamento do salario liquido`() {
        val resultado = calculator.calculate(FolhaPagamentoInput(salarioBase = BigDecimal("5000.00")))
        assertEquals(BigDecimal("5000.00"), resultado.totalProventos)
        assertEquals(BigDecimal("501.51"), resultado.descontos[0].valor)
        assertEquals(BigDecimal("0.00"), resultado.descontos[1].valor)
        assertEquals(BigDecimal("4498.49"), resultado.salarioLiquido)
    }

    @Test
    fun `proventos e descontos adicionais entram no total`() {
        val resultado = calculator.calculate(
            FolhaPagamentoInput(
                salarioBase = BigDecimal("3000.00"),
                proventosAdicionais = listOf(ItemFolha("Comissão", BigDecimal("500.00"))),
                descontosAdicionais = listOf(ItemFolha("Vale-transporte", BigDecimal("100.00"))),
            ),
        )
        assertEquals(BigDecimal("3500.00"), resultado.totalProventos)
        assertEquals(3, resultado.descontos.size)
    }

    @Test
    fun `encargos patronais somam fgts e inss patronal sobre o total de proventos`() {
        val resultado = calculator.calculate(FolhaPagamentoInput(salarioBase = BigDecimal("2000.00")))
        assertEquals(BigDecimal("160.00"), resultado.encargosPatronais.fgts)
        assertEquals(BigDecimal("400.00"), resultado.encargosPatronais.inssPatronal)
        assertEquals(BigDecimal("560.00"), resultado.encargosPatronais.total)
        assertEquals(BigDecimal("2560.00"), resultado.custoTotalEmpresa)
    }
}
