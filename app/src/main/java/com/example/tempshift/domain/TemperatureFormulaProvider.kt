package com.example.tempshift.domain

object TemperatureFormulaProvider {

    fun formula(from: TemperatureUnit, to: TemperatureUnit): String {
        return when (from) {
            TemperatureUnit.CELSIUS -> when (to) {
                TemperatureUnit.CELSIUS -> "°C"
                TemperatureUnit.FAHRENHEIT -> "°C × 9/5 + 32"
                TemperatureUnit.KELVIN -> "°C + 273.15"
            }
            TemperatureUnit.FAHRENHEIT -> when (to) {
                TemperatureUnit.CELSIUS -> "(°F - 32) × 5/9"
                TemperatureUnit.FAHRENHEIT -> "°F"
                TemperatureUnit.KELVIN -> "(°F - 32) × 5/9 + 273.15"
            }
            TemperatureUnit.KELVIN -> when (to) {
                TemperatureUnit.CELSIUS -> "K - 273.15"
                TemperatureUnit.FAHRENHEIT -> "(K - 273.15) × 9/5 + 32"
                TemperatureUnit.KELVIN -> "K"
            }
        }
    }
}
