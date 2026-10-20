package com.example.tempshift.domain

import java.math.BigDecimal

data class TemperatureResult(
    val inputValue: BigDecimal,
    val resultValue: BigDecimal,
    val fromUnit: TemperatureUnit,
    val toUnit: TemperatureUnit,
    val formula: String,
    val calculation: String
)
