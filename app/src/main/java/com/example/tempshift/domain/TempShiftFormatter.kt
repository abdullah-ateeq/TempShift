package com.example.tempshift.domain

import java.math.BigDecimal
import java.math.RoundingMode

object TempShiftFormatter {

    fun formatNumber(
        value: BigDecimal,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ): String {
        val scaled = value.setScale(decimalPlaces, roundingMode)
        return if (scaled.compareTo(BigDecimal.ZERO) == 0) {
            "0"
        } else {
            scaled.stripTrailingZeros().toPlainString()
        }
    }

    fun formatTemperature(
        value: BigDecimal,
        unit: TemperatureUnit,
        decimalPlaces: Int = 2,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val formattedNumber = formatNumber(value, decimalPlaces, roundingMode)
        return "$formattedNumber ${unit.symbol}"
    }

    fun formatConversion(
        result: TemperatureResult,
        decimalPlaces: Int = 2,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val fromText = formatTemperature(result.inputValue, result.fromUnit, decimalPlaces, roundingMode)
        val toText = formatTemperature(result.resultValue, result.toUnit, decimalPlaces, roundingMode)
        return "$fromText = $toText"
    }

    fun formatFormula(
        result: TemperatureResult
    ): String {
        return result.formula
    }

    fun formatCopyText(
        result: TemperatureResult,
        decimalPlaces: Int = 2,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val conversion = formatConversion(result, decimalPlaces, roundingMode)
        return "TempShift\n\n$conversion\n\nFormula:\n${result.calculation}"
    }

    fun formatShareText(
        result: TemperatureResult,
        decimalPlaces: Int = 2,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): String {
        val conversion = formatConversion(result, decimalPlaces, roundingMode)
        return "TempShift\n$conversion"
    }
}
