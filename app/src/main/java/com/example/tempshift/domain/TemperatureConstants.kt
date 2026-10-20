package com.example.tempshift.domain

import java.math.BigDecimal
import java.math.MathContext

object TemperatureConstants {
    val ZERO: BigDecimal = BigDecimal.ZERO
    val NINE: BigDecimal = BigDecimal("9")
    val FIVE: BigDecimal = BigDecimal("5")
    val THIRTY_TWO: BigDecimal = BigDecimal("32")
    val KELVIN_OFFSET: BigDecimal = BigDecimal("273.15")

    val ABSOLUTE_ZERO_CELSIUS: BigDecimal = BigDecimal("-273.15")
    val ABSOLUTE_ZERO_FAHRENHEIT: BigDecimal = BigDecimal("-459.67")
    val ABSOLUTE_ZERO_KELVIN: BigDecimal = BigDecimal("0")

    val DEFAULT_MATH_CONTEXT: MathContext = MathContext.DECIMAL128
    const val MAX_INPUT_LENGTH: Int = 500
}
