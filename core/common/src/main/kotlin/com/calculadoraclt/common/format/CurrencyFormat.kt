package com.calculadoraclt.common.format

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormatSymbols
import java.util.Locale

private val PT_BR = Locale.Builder().setLanguage("pt").setRegion("BR").build()

fun BigDecimal.formatMoeda(): String {
    val symbols = DecimalFormatSymbols.getInstance(PT_BR)
    val valor = setScale(2, RoundingMode.HALF_UP)
    val inteiro = valor.toBigInteger().abs().toString()
    val centavos = valor.remainder(BigDecimal.ONE).abs().movePointRight(2).toInt().toString().padStart(2, '0')

    val inteiroAgrupado = inteiro.reversed().chunked(3).joinToString(symbols.groupingSeparator.toString()).reversed()
    val sinal = if (valor.signum() < 0) "-" else ""

    return "R$ $sinal$inteiroAgrupado${symbols.decimalSeparator}$centavos"
}

fun String.paraBigDecimalOuNull(): BigDecimal? {
    val normalizado = trim()
        .replace(".", "")
        .replace(",", ".")
        .replace("R$", "", ignoreCase = true)
        .trim()
    if (normalizado.isEmpty()) return null
    return normalizado.toBigDecimalOrNull()
}
