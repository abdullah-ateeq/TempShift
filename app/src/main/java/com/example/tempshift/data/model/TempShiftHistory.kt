package com.example.tempshift.data.model

import com.example.tempshift.domain.TemperatureUnit
import java.util.UUID

data class TempShiftHistory(
    val id: String = UUID.randomUUID().toString(),
    val inputValue: String,
    val fromUnit: TemperatureUnit,
    val toUnit: TemperatureUnit,
    val resultValue: String,
    val calculatedAt: Long = System.currentTimeMillis()
)
