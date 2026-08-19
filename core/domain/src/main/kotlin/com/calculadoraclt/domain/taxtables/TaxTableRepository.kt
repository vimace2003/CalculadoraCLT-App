package com.calculadoraclt.domain.taxtables

import java.time.LocalDate

interface TaxTableRepository {
    fun getTable(ano: Int = LocalDate.now().year): TaxTable
}

class TaxTableRepositoryImpl(
    private val tables: Map<Int, TaxTable> = mapOf(2026 to TaxTables2026),
) : TaxTableRepository {
    override fun getTable(ano: Int): TaxTable =
        tables[ano] ?: tables.getValue(tables.keys.max())
}
