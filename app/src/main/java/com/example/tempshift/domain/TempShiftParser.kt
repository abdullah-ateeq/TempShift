package com.example.tempshift.domain

import java.math.BigDecimal

object TempShiftParser {

    fun parseTemperature(input: String): Result<BigDecimal> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("Enter a temperature to begin."))
        }

        if (trimmed.length > TemperatureConstants.MAX_INPUT_LENGTH) {
            return Result.failure(IllegalArgumentException("Temperature is too long. Maximum 500 digits."))
        }

        // Check standalone invalid symbols
        if (trimmed == "." || trimmed == "+." || trimmed == "-." || trimmed == "+" || trimmed == "-") {
            return Result.failure(IllegalArgumentException("Enter a valid temperature."))
        }

        // Normalize leading decimal point
        var normalized = when {
            trimmed.startsWith("+.") -> "+0." + trimmed.substring(2)
            trimmed.startsWith("-.") -> "-0." + trimmed.substring(2)
            trimmed.startsWith(".") -> "0" + trimmed
            else -> trimmed
        }

        // Normalize trailing decimal point (e.g. 10. or 10.E2)
        if (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length - 1)
        }

        return try {
            val bigDecimal = BigDecimal(normalized)
            Result.success(bigDecimal)
        } catch (e: NumberFormatException) {
            Result.failure(IllegalArgumentException("Enter a valid temperature."))
        }
    }
}
