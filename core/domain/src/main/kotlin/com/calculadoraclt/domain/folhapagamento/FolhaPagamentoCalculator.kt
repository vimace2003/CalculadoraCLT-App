package com.calculadoraclt.domain.folhapagamento

import com.calculadoraclt.domain.calculator.Calculator
import com.calculadoraclt.domain.calculator.InssCalculator
import com.calculadoraclt.domain.calculator.IrrfCalculator
import com.calculadoraclt.domain.taxtables.TaxTable
import java.math.BigDecimal
import java.math.RoundingMode

/** Alíquota patronal padrão de INSS no regime geral (CLT) — estável historicamente, não versionada por ano. */
private val ALIQUOTA_INSS_PATRONAL = BigDecimal("0.20")

data class ItemFolha(
    val descricao: String,
    val valor: BigDecimal,
)

data class FolhaPagamentoInput(
    val salarioBase: BigDecimal,
    val proventosAdicionais: List<ItemFolha> = emptyList(),
    val descontosAdicionais: List<ItemFolha> = emptyList(),
    val numeroDependentes: Int = 0,
)

data class EncargosPatronais(
    val fgts: BigDecimal,
    val inssPatronal: BigDecimal,
    val total: BigDecimal,
)

data class FolhaPagamentoResult(
    val proventos: List<ItemFolha>,
    val totalProventos: BigDecimal,
    val descontos: List<ItemFolha>,
    val totalDescontos: BigDecimal,
    val salarioLiquido: BigDecimal,
    val encargosPatronais: EncargosPatronais,
    val custoTotalEmpresa: BigDecimal,
)

class FolhaPagamentoCalculator(
    private val tabela: TaxTable,
    private val inssCalculator: InssCalculator = InssCalculator(),
    private val irrfCalculator: IrrfCalculator = IrrfCalculator(),
) : Calculator<FolhaPagamentoInput, FolhaPagamentoResult> {
    override fun calculate(input: FolhaPagamentoInput): FolhaPagamentoResult {
        val proventos = listOf(ItemFolha("Salário base", input.salarioBase)) + input.proventosAdicionais
        val totalProventos = proventos.sumValores()

        val descontoInss = inssCalculator.calcular(totalProventos, tabela.inss).valorDesconto
        val descontoIrrf = irrfCalculator.calcular(
            rendimentoBruto = totalProventos,
            descontoInss = descontoInss,
            numeroDependentes = input.numeroDependentes,
            tabela = tabela.irrf,
        ).valorDevido

        val descontos = listOf(
            ItemFolha("INSS", descontoInss),
            ItemFolha("IRRF", descontoIrrf),
        ) + input.descontosAdicionais
        val totalDescontos = descontos.sumValores()

        val salarioLiquido = totalProventos.subtract(totalDescontos).setScale(2, RoundingMode.HALF_UP)

        val fgtsPatronal = totalProventos.multiply(tabela.fgts.aliquotaMensal).setScale(2, RoundingMode.HALF_UP)
        val inssPatronal = totalProventos.multiply(ALIQUOTA_INSS_PATRONAL).setScale(2, RoundingMode.HALF_UP)
        val encargosPatronais = EncargosPatronais(
            fgts = fgtsPatronal,
            inssPatronal = inssPatronal,
            total = fgtsPatronal.add(inssPatronal).setScale(2, RoundingMode.HALF_UP),
        )

        val custoTotalEmpresa = totalProventos.add(encargosPatronais.total).setScale(2, RoundingMode.HALF_UP)

        return FolhaPagamentoResult(
            proventos = proventos,
            totalProventos = totalProventos,
            descontos = descontos,
            totalDescontos = totalDescontos,
            salarioLiquido = salarioLiquido,
            encargosPatronais = encargosPatronais,
            custoTotalEmpresa = custoTotalEmpresa,
        )
    }

    private fun List<ItemFolha>.sumValores(): BigDecimal =
        fold(BigDecimal.ZERO) { acc, item -> acc.add(item.valor) }.setScale(2, RoundingMode.HALF_UP)
}
