package com.calculadoraclt.domain.salarioporhora

import com.calculadoraclt.domain.calculator.Calculator
import java.math.BigDecimal
import java.math.RoundingMode

data class SalarioPorHoraInput(
    val salarioMensal: BigDecimal,
    val cargaHorariaSemanal: BigDecimal,
)

data class SalarioPorHoraResult(
    val valorHora: BigDecimal,
    val valorMinuto: BigDecimal,
)

private val DIVISOR_SEMANAL_PARA_MENSAL = BigDecimal("5")
private val MINUTOS_POR_HORA = BigDecimal("60")

class SalarioPorHoraCalculator : Calculator<SalarioPorHoraInput, SalarioPorHoraResult> {
    override fun calculate(input: SalarioPorHoraInput): SalarioPorHoraResult {
        if (input.cargaHorariaSemanal <= BigDecimal.ZERO) {
            return SalarioPorHoraResult(BigDecimal.ZERO.setScale(2), BigDecimal.ZERO.setScale(2))
        }

        // divisor CLT: carga semanal ÷ 6 dias úteis × 30 dias/mês, equivalente a semanal × 5
        // (ex: jornada de 44h/semana resulta no divisor padrão de 220h/mês)
        val horasMensais = input.cargaHorariaSemanal.multiply(DIVISOR_SEMANAL_PARA_MENSAL)
        val valorHora = input.salarioMensal.divide(horasMensais, 2, RoundingMode.HALF_UP)
        val valorMinuto = valorHora.divide(MINUTOS_POR_HORA, 2, RoundingMode.HALF_UP)

        return SalarioPorHoraResult(valorHora, valorMinuto)
    }
}
