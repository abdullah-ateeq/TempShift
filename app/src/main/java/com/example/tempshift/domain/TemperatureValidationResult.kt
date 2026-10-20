package com.example.tempshift.domain

sealed interface TemperatureValidationResult {
    data object Valid : TemperatureValidationResult
    data class Invalid(val message: String) : TemperatureValidationResult
}
