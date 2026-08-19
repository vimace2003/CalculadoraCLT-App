package com.calculadoraclt.domain.segurodesemprego

import com.calculadoraclt.domain.calculator.Calculator
import com.calculadoraclt.domain.taxtables.SeguroDesempregoTable
import com.calculadoraclt.domain.taxtables.TaxTable
import java.math.BigDecimal
import java.math.RoundingMode

data class SeguroDesempregoInput(
    val salarios: List<BigDecimal>,
    val numeroSolicitacoesAnteriores: Int,
    val mesesTrabalhadosUltimoVinculo: Int,
)

data class SeguroDesempregoResult(
    val mediaSalarial: BigDecimal,
    val elegivel: Boolean,
    val motivoInelegibilidade: String?,
    val numeroParcelas: Int,
    val valorParcela: BigDecimal,
)

class SeguroDesempregoCalculator(
    private val tabela: TaxTable,
) : Calculator<SeguroDesempregoInput, SeguroDesempregoResult> {
    override fun calculate(input: SeguroDesempregoInput): SeguroDesempregoResult {
        val mediaSalarial = if (input.salarios.isEmpty()) {
            BigDecimal.ZERO.setScale(2)
        } else {
            input.salarios.reduce(BigDecimal::add)
                .divide(BigDecimal(input.salarios.size), 2, RoundingMode.HALF_UP)
        }

        val mesesMinimos = mesesMinimosExigidos(input.numeroSolicitacoesAnteriores)
        val elegivel = input.mesesTrabalhadosUltimoVinculo >= mesesMinimos

        if (!elegivel) {
            return SeguroDesempregoResult(
                mediaSalarial = mediaSalarial,
                elegivel = false,
                motivoInelegibilidade = "É preciso ter trabalhado pelo menos $mesesMinimos meses no último vínculo para esta solicitação.",
                numeroParcelas = 0,
                valorParcela = BigDecimal.ZERO.setScale(2),
            )
        }

        val numeroParcelas = numeroParcelas(input.numeroSolicitacoesAnteriores, input.mesesTrabalhadosUltimoVinculo)
        val valorParcela = calcularValorParcela(mediaSalarial, tabela.seguroDesemprego)

        return SeguroDesempregoResult(mediaSalarial, true, null, numeroParcelas, valorParcela)
    }

    private fun mesesMinimosExigidos(numeroSolicitacoesAnteriores: Int): Int = when (numeroSolicitacoesAnteriores) {
        0 -> 12
        1 -> 9
        else -> 6
    }

    private fun numeroParcelas(numeroSolicitacoesAnteriores: Int, mesesTrabalhados: Int): Int = when (numeroSolicitacoesAnteriores) {
        0 -> if (mesesTrabalhados >= 24) 5 else 4
        1 -> when {
            mesesTrabalhados >= 24 -> 5
            mesesTrabalhados >= 12 -> 4
            else -> 3
        }
        else -> when {
            mesesTrabalhados >= 24 -> 5
            mesesTrabalhados >= 12 -> 4
            else -> 3
        }
    }

    /**
     * Cada faixa acima da primeira aplica o percentual apenas sobre o valor que excede o
     * limite da faixa anterior, somando ao valor fixo acumulado (padrão da tabela do
     * seguro-desemprego: ex. R$1.777,74 + 50% do que exceder R$2.222,17).
     */
    private fun calcularValorParcela(mediaSalarial: BigDecimal, tabela: SeguroDesempregoTable): BigDecimal {
        if (mediaSalarial <= BigDecimal.ZERO) return BigDecimal.ZERO.setScale(2)

        var limiteAnterior = BigDecimal.ZERO
        var valor = tabela.faixas.last().parcelaFixa
        for (faixa in tabela.faixas) {
            if (faixa.ate == null || mediaSalarial <= faixa.ate) {
                valor = if (faixa.percentual > BigDecimal.ZERO) {
                    faixa.parcelaFixa.add(mediaSalarial.subtract(limiteAnterior).multiply(faixa.percentual))
                } else {
                    faixa.parcelaFixa
                }
                break
            }
            limiteAnterior = faixa.ate
        }

        return valor
            .max(tabela.valorMinimo)
            .min(tabela.valorMaximo)
            .setScale(2, RoundingMode.HALF_UP)
    }
}
