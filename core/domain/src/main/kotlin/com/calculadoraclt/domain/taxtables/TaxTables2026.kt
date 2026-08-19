package com.calculadoraclt.domain.taxtables

import java.math.BigDecimal

/**
 * Fonte: tabelas oficiais de INSS, IRRF, salário mínimo e seguro-desemprego vigentes em 2026.
 *
 * IRRF: além das 5 faixas progressivas clássicas, a partir de 01/2026 (Lei nº 15.270/2025)
 * há um redutor que isenta integralmente quem ganha até R$5.000/mês e reduz parcialmente
 * o imposto de quem ganha entre R$5.000,01 e R$7.350,00 — ver [IrrfCalculator].
 */
object TaxTables2026 : TaxTable {
    override val ano = 2026

    override val salarioMinimo: BigDecimal = BigDecimal("1621.00")

    override val inss = InssTable(
        faixas = listOf(
            InssFaixa(ate = BigDecimal("1621.00"), aliquota = BigDecimal("0.075")),
            InssFaixa(ate = BigDecimal("2902.84"), aliquota = BigDecimal("0.09")),
            InssFaixa(ate = BigDecimal("4354.27"), aliquota = BigDecimal("0.12")),
            InssFaixa(ate = BigDecimal("8475.55"), aliquota = BigDecimal("0.14")),
        ),
        tetoContribuicao = BigDecimal("988.09"),
    )

    override val irrf = IrrfTable(
        faixas = listOf(
            IrrfFaixa(ate = BigDecimal("2428.80"), aliquota = BigDecimal.ZERO, deducao = BigDecimal.ZERO),
            IrrfFaixa(ate = BigDecimal("2826.65"), aliquota = BigDecimal("0.075"), deducao = BigDecimal("182.16")),
            IrrfFaixa(ate = BigDecimal("3751.05"), aliquota = BigDecimal("0.15"), deducao = BigDecimal("394.16")),
            IrrfFaixa(ate = BigDecimal("4664.68"), aliquota = BigDecimal("0.225"), deducao = BigDecimal("675.49")),
            IrrfFaixa(ate = null, aliquota = BigDecimal("0.275"), deducao = BigDecimal("908.73")),
        ),
        deducaoPorDependente = BigDecimal("189.59"),
        limiteIsencaoRedutor = BigDecimal("5000.00"),
        limiteReducaoParcial = BigDecimal("7350.00"),
        redutorBase = BigDecimal("978.62"),
        redutorCoeficiente = BigDecimal("0.133145"),
    )

    override val seguroDesemprego = SeguroDesempregoTable(
        faixas = listOf(
            SeguroDesempregoFaixa(ate = BigDecimal("2222.17"), percentual = BigDecimal("0.80"), parcelaFixa = BigDecimal.ZERO),
            SeguroDesempregoFaixa(ate = BigDecimal("3703.99"), percentual = BigDecimal("0.50"), parcelaFixa = BigDecimal("1777.74")),
            SeguroDesempregoFaixa(ate = null, percentual = BigDecimal.ZERO, parcelaFixa = BigDecimal("2518.65")),
        ),
        valorMinimo = BigDecimal("1621.00"),
        valorMaximo = BigDecimal("2518.65"),
    )

    override val fgts = FgtsConfig(
        aliquotaMensal = BigDecimal("0.08"),
        multaSemJustaCausa = BigDecimal("0.40"),
        multaAcordoMutuo = BigDecimal("0.20"),
    )
}
