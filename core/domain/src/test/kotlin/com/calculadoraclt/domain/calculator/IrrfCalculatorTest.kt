package com.calculadoraclt.domain.calculator

import com.calculadoraclt.domain.taxtables.TaxTables2026
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class IrrfCalculatorTest {

    private val calculator = IrrfCalculator()
    private val tabela = TaxTables2026.irrf

    @Test
    fun `renda ate R$5000 e totalmente isenta mesmo com imposto tradicional maior que zero`() {
        val resultado = calculator.calcular(
            rendimentoBruto = BigDecimal("3000.00"),
            descontoInss = BigDecimal("248.60"),
            numeroDependentes = 0,
            tabela = tabela,
        )
        assertEquals(BigDecimal("0.00"), resultado.valorDevido)
    }

    @Test
    fun `renda de R$7000 aplica reducao parcial do redutor`() {
        val resultado = calculator.calcular(
            rendimentoBruto = BigDecimal("7000.00"),
            descontoInss = BigDecimal("781.51"),
            numeroDependentes = 0,
            tabela = tabela,
        )
        assertEquals(BigDecimal("801.35"), resultado.impostoTradicional)
        assertEquals(BigDecimal("46.61"), resultado.redutorAplicado)
        assertEquals(BigDecimal("754.74"), resultado.valorDevido)
    }

    @Test
    fun `renda acima de R$7350 nao recebe nenhuma reducao`() {
        val resultado = calculator.calcular(
            rendimentoBruto = BigDecimal("8000.00"),
            descontoInss = BigDecimal("921.51"),
            numeroDependentes = 0,
            tabela = tabela,
        )
        assertEquals(BigDecimal.ZERO.setScale(2), resultado.redutorAplicado)
        assertEquals(BigDecimal("1037.85"), resultado.valorDevido)
    }

    @Test
    fun `dependentes reduzem a base de calculo antes do redutor`() {
        val resultado = calculator.calcular(
            rendimentoBruto = BigDecimal("6000.00"),
            descontoInss = BigDecimal("641.51"),
            numeroDependentes = 2,
            tabela = tabela,
        )
        assertEquals(BigDecimal("4979.31"), resultado.baseCalculo)
        assertEquals(BigDecimal("280.83"), resultado.valorDevido)
    }

    @Test
    fun `base de calculo negativa nao gera imposto`() {
        val resultado = calculator.calcular(
            rendimentoBruto = BigDecimal("1800.00"),
            descontoInss = BigDecimal("135.00"),
            numeroDependentes = 10,
            tabela = tabela,
        )
        assertEquals(BigDecimal("0.00"), resultado.valorDevido)
    }
}
