package com.example.tempshift.domain

import java.math.BigDecimal
import java.math.MathContext

object TemperatureCalculator {

    fun convert(
        value: BigDecimal,
        from: TemperatureUnit,
        to: TemperatureUnit,
        mathContext: MathContext = TemperatureConstants.DEFAULT_MATH_CONTEXT
    ): BigDecimal {
        if (from == to) {
            return value
        }

        val celsiusValue = when (from) {
            TemperatureUnit.CELSIUS -> value
            TemperatureUnit.FAHRENHEIT -> {
                value.subtract(TemperatureConstants.THIRTY_TWO, mathContext)
                    .multiply(TemperatureConstants.FIVE, mathContext)
                    .divide(TemperatureConstants.NINE, mathContext)
            }
            TemperatureUnit.KELVIN -> {
                value.subtract(TemperatureConstants.KELVIN_OFFSET, mathContext)
            }
        }

        return when (to) {
            TemperatureUnit.CELSIUS -> celsiusValue
            TemperatureUnit.FAHRENHEIT -> {
                celsiusValue.multiply(TemperatureConstants.NINE, mathContext)
                    .divide(TemperatureConstants.FIVE, mathContext)
                    .add(TemperatureConstants.THIRTY_TWO, mathContext)
            }
            TemperatureUnit.KELVIN -> {
                celsiusValue.add(TemperatureConstants.KELVIN_OFFSET, mathContext)
            }
        }
    }
}
