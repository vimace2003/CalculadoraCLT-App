package com.calculadoraclt.domain.calculator

fun interface Calculator<in Input, out Result> {
    fun calculate(input: Input): Result
}
