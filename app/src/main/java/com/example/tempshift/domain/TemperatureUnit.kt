package com.example.tempshift.domain

enum class TemperatureUnit(
    val id: String,
    val unitName: String,
    val symbol: String
) {
    CELSIUS("celsius", "Celsius", "°C"),
    FAHRENHEIT("fahrenheit", "Fahrenheit", "°F"),
    KELVIN("kelvin", "Kelvin", "K");

    companion object {
        fun fromId(id: String?): TemperatureUnit {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CELSIUS
        }
    }
}
