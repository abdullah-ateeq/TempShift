package com.example.tempshift.domain

import java.math.BigDecimal

object TemperatureValidator {

    fun validate(
        value: BigDecimal,
        unit: TemperatureUnit
    ): TemperatureValidationResult {
        return when (unit) {
            TemperatureUnit.KELVIN -> {
                if (value.compareTo(TemperatureConstants.ABSOLUTE_ZERO_KELVIN) < 0) {
                    TemperatureValidationResult.Invalid("Kelvin cannot be below 0 K.")
                } else {
                    TemperatureValidationResult.Valid
                }
            }
            TemperatureUnit.CELSIUS -> {
                if (value.compareTo(TemperatureConstants.ABSOLUTE_ZERO_CELSIUS) < 0) {
                    TemperatureValidationResult.Invalid("Temperature is below absolute zero.")
                } else {
                    TemperatureValidationResult.Valid
                }
            }
            TemperatureUnit.FAHRENHEIT -> {
                if (value.compareTo(TemperatureConstants.ABSOLUTE_ZERO_FAHRENHEIT) < 0) {
                    TemperatureValidationResult.Invalid("Temperature is below absolute zero.")
                } else {
                    TemperatureValidationResult.Valid
                }
            }
        }
    }
}
