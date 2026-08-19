package com.calculadoraclt.common.date

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val PADRAO_BR: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

fun LocalDate.formatBr(): String = format(PADRAO_BR)

fun String.paraLocalDateBrOuNull(): LocalDate? = try {
    LocalDate.parse(trim(), PADRAO_BR)
} catch (e: DateTimeParseException) {
    null
}
