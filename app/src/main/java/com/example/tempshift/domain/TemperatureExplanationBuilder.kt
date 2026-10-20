package com.example.tempshift.domain

import java.math.BigDecimal
import java.math.RoundingMode

object TemperatureExplanationBuilder {

    fun build(
        input: BigDecimal,
        result: BigDecimal,
        from: TemperatureUnit,
        to: TemperatureUnit,
        decimalPlaces: Int = 2,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): TemperatureConversionDetails {
        val formula = TemperatureFormulaProvider.formula(from, to)
        val inputStr = TempShiftFormatter.formatNumber(input, decimalPlaces, roundingMode)
        val resultStr = TempShiftFormatter.formatNumber(result, decimalPlaces, roundingMode)

        val calculation = when (from) {
            TemperatureUnit.CELSIUS -> when (to) {
                TemperatureUnit.CELSIUS -> "$inputStr ${from.symbol} = $resultStr ${to.symbol}"
                TemperatureUnit.FAHRENHEIT -> "$inputStr × 9/5 + 32 = $resultStr"
                TemperatureUnit.KELVIN -> "$inputStr + 273.15 = $resultStr"
            }
            TemperatureUnit.FAHRENHEIT -> when (to) {
                TemperatureUnit.CELSIUS -> "($inputStr - 32) × 5/9 = $resultStr"
                TemperatureUnit.FAHRENHEIT -> "$inputStr ${from.symbol} = $resultStr ${to.symbol}"
                TemperatureUnit.KELVIN -> "($inputStr - 32) × 5/9 + 273.15 = $resultStr"
            }
            TemperatureUnit.KELVIN -> when (to) {
                TemperatureUnit.CELSIUS -> "$inputStr - 273.15 = $resultStr"
                TemperatureUnit.FAHRENHEIT -> "($inputStr - 273.15) × 9/5 + 32 = $resultStr"
                TemperatureUnit.KELVIN -> "$inputStr ${from.symbol} = $resultStr ${to.symbol}"
            }
        }

        return TemperatureConversionDetails(
            formula = formula,
            calculation = calculation
        )
    }
}
