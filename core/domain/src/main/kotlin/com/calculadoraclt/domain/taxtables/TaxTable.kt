package com.calculadoraclt.domain.taxtables

import java.math.BigDecimal

data class InssFaixa(
    val ate: BigDecimal?,
    val aliquota: BigDecimal,
)

data class InssTable(
    val faixas: List<InssFaixa>,
    val tetoContribuicao: BigDecimal,
)

data class IrrfFaixa(
    val ate: BigDecimal?,
    val aliquota: BigDecimal,
    val deducao: BigDecimal,
)

data class IrrfTable(
    val faixas: List<IrrfFaixa>,
    val deducaoPorDependente: BigDecimal,
    val limiteIsencaoRedutor: BigDecimal,
    val limiteReducaoParcial: BigDecimal,
    val redutorBase: BigDecimal,
    val redutorCoeficiente: BigDecimal,
)

data class SeguroDesempregoFaixa(
    val ate: BigDecimal?,
    val percentual: BigDecimal,
    val parcelaFixa: BigDecimal,
)

data class SeguroDesempregoTable(
    val faixas: List<SeguroDesempregoFaixa>,
    val valorMinimo: BigDecimal,
    val valorMaximo: BigDecimal,
)

data class FgtsConfig(
    val aliquotaMensal: BigDecimal,
    val multaSemJustaCausa: BigDecimal,
    val multaAcordoMutuo: BigDecimal,
)

interface TaxTable {
    val ano: Int
    val salarioMinimo: BigDecimal
    val inss: InssTable
    val irrf: IrrfTable
    val seguroDesemprego: SeguroDesempregoTable
    val fgts: FgtsConfig
}
