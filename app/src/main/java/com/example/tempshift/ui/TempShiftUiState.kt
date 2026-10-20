package com.example.tempshift.ui

import com.example.tempshift.data.model.TempShiftHistory
import com.example.tempshift.data.model.ThemeMode
import com.example.tempshift.domain.TemperatureResult
import com.example.tempshift.domain.TemperatureUnit
import java.math.RoundingMode

data class TempShiftUiState(
    val inputText: String = "25",
    val fromUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val toUnit: TemperatureUnit = TemperatureUnit.FAHRENHEIT,
    val result: TemperatureResult? = null,
    val history: List<TempShiftHistory> = emptyList(),
    val decimalPlaces: Int = 2,
    val roundingMode: RoundingMode = RoundingMode.HALF_UP,
    val showFormula: Boolean = true,
    val showTemperatureScale: Boolean = true,
    val errorMessage: String? = null,
    val isCalculating: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val confirmHistoryDelete: Boolean = true,
    val preserveLastConversion: Boolean = true,
    val calculationState: CalculationState = CalculationState.Idle,
    val snackbarMessage: String? = null
)
