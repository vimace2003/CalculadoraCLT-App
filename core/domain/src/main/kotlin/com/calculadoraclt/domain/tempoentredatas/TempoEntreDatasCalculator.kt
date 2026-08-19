package com.calculadoraclt.domain.tempoentredatas

import com.calculadoraclt.domain.calculator.Calculator
import java.time.LocalDate
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.MONTHS
import java.time.temporal.ChronoUnit.YEARS

data class TempoEntreDatasInput(
    val dataInicial: LocalDate,
    val dataFinal: LocalDate,
)

data class TempoEntreDatasResult(
    val anos: Int,
    val meses: Int,
    val dias: Int,
    val totalDias: Long,
    val totalSemanas: Long,
    val totalMeses: Long,
)

class TempoEntreDatasCalculator : Calculator<TempoEntreDatasInput, TempoEntreDatasResult> {
    override fun calculate(input: TempoEntreDatasInput): TempoEntreDatasResult {
        val inicio = minOf(input.dataInicial, input.dataFinal)
        val fim = maxOf(input.dataInicial, input.dataFinal)

        val anos = YEARS.between(inicio, fim).toInt()
        val meses = MONTHS.between(inicio.plusYears(anos.toLong()), fim).toInt()
        val dias = DAYS.between(inicio.plusYears(anos.toLong()).plusMonths(meses.toLong()), fim).toInt()

        return TempoEntreDatasResult(
            anos = anos,
            meses = meses,
            dias = dias,
            totalDias = DAYS.between(inicio, fim),
            totalSemanas = DAYS.between(inicio, fim) / 7,
            totalMeses = MONTHS.between(inicio, fim),
        )
    }
}
