package com.calculadoraclt.domain.rescisao

import com.calculadoraclt.domain.calculator.Calculator
import com.calculadoraclt.domain.calculator.InssCalculator
import com.calculadoraclt.domain.calculator.IrrfCalculator
import com.calculadoraclt.domain.fgts.FgtsCalculator
import com.calculadoraclt.domain.fgts.FgtsInput
import com.calculadoraclt.domain.taxtables.TaxTable
import com.calculadoraclt.domain.tempoentredatas.TempoEntreDatasCalculator
import com.calculadoraclt.domain.tempoentredatas.TempoEntreDatasInput
import com.calculadoraclt.domain.tempoentredatas.TempoEntreDatasResult
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

enum class TipoAvisoPrevio { INDENIZADO, TRABALHADO, DISPENSADO }

data class RescisaoInput(
    val tipoRescisao: TipoRescisao,
    val salarioBase: BigDecimal,
    val dataAdmissao: LocalDate,
    val dataDemissao: LocalDate,
    val tipoAvisoPrevio: TipoAvisoPrevio,
    val possuiFeriasVencidas: Boolean = false,
    val numeroDependentes: Int = 0,
)

data class RescisaoResult(
    val diasAvisoPrevio: Int,
    val saldoSalario: BigDecimal,
    val avisoPrevioIndenizado: BigDecimal?,
    val feriasVencidas: BigDecimal?,
    val feriasProporcionais: BigDecimal,
    val decimoTerceiroProporcional: BigDecimal,
    val fgtsMultaRescisoria: BigDecimal,
    val percentualMultaFgts: BigDecimal,
    val totalBruto: BigDecimal,
    val totalDescontos: BigDecimal,
    val totalLiquido: BigDecimal,
)

/**
 * Estimativa: aviso prévio indenizado, férias vencidas/proporcionais + 1/3 e a multa do FGTS são
 * tratados como isentos de INSS/IRRF (correto na regra geral); saldo de salário e férias
 * proporcionais são somados numa única base tributável simplificada, e o 13º proporcional é
 * tributado separadamente — o que já reflete como a legislação trata essa verba. Casos reais
 * podem ter nuances adicionais (ex: convenções coletivas); use como estimativa.
 */
class RescisaoCalculator(
    private val tabela: TaxTable,
    private val tempoEntreDatasCalculator: TempoEntreDatasCalculator = TempoEntreDatasCalculator(),
    private val fgtsCalculator: FgtsCalculator = FgtsCalculator(tabela.fgts),
    private val inssCalculator: InssCalculator = InssCalculator(),
    private val irrfCalculator: IrrfCalculator = IrrfCalculator(),
) : Calculator<RescisaoInput, RescisaoResult> {

    override fun calculate(input: RescisaoInput): RescisaoResult {
        val tempoServico = tempoEntreDatasCalculator.calculate(
            TempoEntreDatasInput(input.dataAdmissao, input.dataDemissao),
        )

        val valorDia = input.salarioBase.divide(BigDecimal(30), 10, RoundingMode.HALF_UP)

        val diasAvisoPrevio = (30 + 3 * tempoServico.anos).coerceAtMost(90)

        val saldoSalario = valorDia.multiply(BigDecimal(input.dataDemissao.dayOfMonth)).setScale(2, RoundingMode.HALF_UP)

        val avisoPrevioIndenizado = if (input.tipoAvisoPrevio == TipoAvisoPrevio.INDENIZADO) {
            valorDia.multiply(BigDecimal(diasAvisoPrevio)).setScale(2, RoundingMode.HALF_UP)
        } else {
            null
        }

        val feriasVencidas = if (input.possuiFeriasVencidas) {
            input.salarioBase.add(input.salarioBase.divide(BigDecimal(3), 2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP)
        } else {
            null
        }

        val mesesFerias = mesesProporcionais(tempoServico)
        val feriasProporcionaisBase = input.salarioBase.multiply(BigDecimal(mesesFerias))
            .divide(BigDecimal(12), 2, RoundingMode.HALF_UP)
        val feriasProporcionais = feriasProporcionaisBase
            .add(feriasProporcionaisBase.divide(BigDecimal(3), 2, RoundingMode.HALF_UP))
            .setScale(2, RoundingMode.HALF_UP)

        val tempo13 = tempoEntreDatasCalculator.calculate(
            TempoEntreDatasInput(inicioPeriodoAquisitivo13(input.dataAdmissao, input.dataDemissao), input.dataDemissao),
        )
        val meses13 = mesesProporcionais(tempo13)
        val decimoTerceiroProporcional = input.salarioBase.multiply(BigDecimal(meses13))
            .divide(BigDecimal(12), 2, RoundingMode.HALF_UP)

        val fgtsResult = fgtsCalculator.calculate(
            FgtsInput(
                salarioBase = input.salarioBase,
                mesesTrabalhados = tempoServico.totalMeses.toInt(),
                simularMulta = true,
                tipoRescisaoParaMulta = input.tipoRescisao,
            ),
        )
        val percentualMultaFgts = fgtsResult.percentualMulta ?: BigDecimal.ZERO
        val fgtsMultaRescisoria = fgtsResult.multaRescisoria ?: BigDecimal.ZERO.setScale(2)

        val baseTributavelVerbasSalariais = saldoSalario
            .add(feriasVencidas ?: BigDecimal.ZERO)
            .add(feriasProporcionais)
        val inssVerbasSalariais = inssCalculator.calcular(baseTributavelVerbasSalariais, tabela.inss).valorDesconto
        val irrfVerbasSalariais = irrfCalculator.calcular(
            rendimentoBruto = baseTributavelVerbasSalariais,
            descontoInss = inssVerbasSalariais,
            numeroDependentes = input.numeroDependentes,
            tabela = tabela.irrf,
        ).valorDevido

        val inss13 = inssCalculator.calcular(decimoTerceiroProporcional, tabela.inss).valorDesconto
        val irrf13 = irrfCalculator.calcular(
            rendimentoBruto = decimoTerceiroProporcional,
            descontoInss = inss13,
            numeroDependentes = input.numeroDependentes,
            tabela = tabela.irrf,
        ).valorDevido

        val totalDescontos = inssVerbasSalariais.add(irrfVerbasSalariais).add(inss13).add(irrf13)
            .setScale(2, RoundingMode.HALF_UP)

        val totalBruto = saldoSalario
            .add(avisoPrevioIndenizado ?: BigDecimal.ZERO)
            .add(feriasVencidas ?: BigDecimal.ZERO)
            .add(feriasProporcionais)
            .add(decimoTerceiroProporcional)
            .add(fgtsMultaRescisoria)
            .setScale(2, RoundingMode.HALF_UP)

        val totalLiquido = totalBruto.subtract(totalDescontos).setScale(2, RoundingMode.HALF_UP)

        return RescisaoResult(
            diasAvisoPrevio = diasAvisoPrevio,
            saldoSalario = saldoSalario,
            avisoPrevioIndenizado = avisoPrevioIndenizado,
            feriasVencidas = feriasVencidas,
            feriasProporcionais = feriasProporcionais,
            decimoTerceiroProporcional = decimoTerceiroProporcional,
            fgtsMultaRescisoria = fgtsMultaRescisoria,
            percentualMultaFgts = percentualMultaFgts,
            totalBruto = totalBruto,
            totalDescontos = totalDescontos,
            totalLiquido = totalLiquido,
        )
    }

    private fun mesesProporcionais(tempo: TempoEntreDatasResult): Int {
        val meses = tempo.meses + if (tempo.dias >= 15) 1 else 0
        return meses.coerceIn(0, 12)
    }

    private fun inicioPeriodoAquisitivo13(dataAdmissao: LocalDate, dataDemissao: LocalDate): LocalDate =
        if (dataAdmissao.year == dataDemissao.year) dataAdmissao else LocalDate.of(dataDemissao.year, 1, 1)
}
