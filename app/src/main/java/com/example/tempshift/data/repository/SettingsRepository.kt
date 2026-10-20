package com.example.tempshift.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.tempshift.data.model.ThemeMode
import com.example.tempshift.domain.TemperatureUnit
import java.math.RoundingMode

class SettingsRepository(
    context: Context? = null,
    preferences: SharedPreferences? = null
) {

    private val preferences: SharedPreferences =
        preferences ?: context!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "tempshift_settings_prefs"

        private const val KEY_THEME = "key_theme"
        private const val KEY_DYNAMIC_COLOR = "key_dynamic_color"
        private const val KEY_HAPTIC = "key_haptic"
        private const val KEY_ANIMATIONS = "key_animations"
        private const val KEY_DECIMAL_PLACES = "key_decimal_places"
        private const val KEY_ROUNDING_MODE = "key_rounding_mode"
        private const val KEY_SHOW_FORMULA = "key_show_formula"
        private const val KEY_SHOW_TEMP_SCALE = "key_show_temp_scale"
        private const val KEY_CONFIRM_HISTORY_DELETE = "key_confirm_history_delete"
        private const val KEY_PRESERVE_LAST_CONVERSION = "key_preserve_last_conversion"
        private const val KEY_LAST_INPUT = "key_last_input"
        private const val KEY_LAST_FROM_UNIT = "key_last_from_unit"
        private const val KEY_LAST_TO_UNIT = "key_last_to_unit"

        const val DEFAULT_DECIMAL_PLACES = 2
        val DEFAULT_ROUNDING_MODE: RoundingMode = RoundingMode.HALF_UP
        val DEFAULT_THEME_MODE: ThemeMode = ThemeMode.SYSTEM
        val DEFAULT_FROM_UNIT: TemperatureUnit = TemperatureUnit.CELSIUS
        val DEFAULT_TO_UNIT: TemperatureUnit = TemperatureUnit.FAHRENHEIT
        const val DEFAULT_INPUT = "25"
    }

    var themeMode: ThemeMode
        get() {
            val name = preferences.getString(KEY_THEME, DEFAULT_THEME_MODE.name)
            return try {
                ThemeMode.valueOf(name ?: DEFAULT_THEME_MODE.name)
            } catch (e: Exception) {
                DEFAULT_THEME_MODE
            }
        }
        set(value) = preferences.edit().putString(KEY_THEME, value.name).apply()

    var dynamicColor: Boolean
        get() = preferences.getBoolean(KEY_DYNAMIC_COLOR, true)
        set(value) = preferences.edit().putBoolean(KEY_DYNAMIC_COLOR, value).apply()

    var hapticFeedback: Boolean
        get() = preferences.getBoolean(KEY_HAPTIC, true)
        set(value) = preferences.edit().putBoolean(KEY_HAPTIC, value).apply()

    var animations: Boolean
        get() = preferences.getBoolean(KEY_ANIMATIONS, true)
        set(value) = preferences.edit().putBoolean(KEY_ANIMATIONS, value).apply()

    var decimalPlaces: Int
        get() = preferences.getInt(KEY_DECIMAL_PLACES, DEFAULT_DECIMAL_PLACES)
        set(value) = preferences.edit().putInt(KEY_DECIMAL_PLACES, value).apply()

    var roundingMode: RoundingMode
        get() {
            val name = preferences.getString(KEY_ROUNDING_MODE, DEFAULT_ROUNDING_MODE.name)
            return try {
                RoundingMode.valueOf(name ?: DEFAULT_ROUNDING_MODE.name)
            } catch (e: Exception) {
                DEFAULT_ROUNDING_MODE
            }
        }
        set(value) = preferences.edit().putString(KEY_ROUNDING_MODE, value.name).apply()

    var showFormula: Boolean
        get() = preferences.getBoolean(KEY_SHOW_FORMULA, true)
        set(value) = preferences.edit().putBoolean(KEY_SHOW_FORMULA, value).apply()

    var showTemperatureScale: Boolean
        get() = preferences.getBoolean(KEY_SHOW_TEMP_SCALE, true)
        set(value) = preferences.edit().putBoolean(KEY_SHOW_TEMP_SCALE, value).apply()

    var confirmHistoryDelete: Boolean
        get() = preferences.getBoolean(KEY_CONFIRM_HISTORY_DELETE, true)
        set(value) = preferences.edit().putBoolean(KEY_CONFIRM_HISTORY_DELETE, value).apply()

    var preserveLastConversion: Boolean
        get() = preferences.getBoolean(KEY_PRESERVE_LAST_CONVERSION, true)
        set(value) = preferences.edit().putBoolean(KEY_PRESERVE_LAST_CONVERSION, value).apply()

    var lastInput: String
        get() = preferences.getString(KEY_LAST_INPUT, DEFAULT_INPUT) ?: DEFAULT_INPUT
        set(value) = preferences.edit().putString(KEY_LAST_INPUT, value).apply()

    var lastFromUnit: TemperatureUnit
        get() {
            val id = preferences.getString(KEY_LAST_FROM_UNIT, DEFAULT_FROM_UNIT.id)
            return TemperatureUnit.fromId(id)
        }
        set(value) = preferences.edit().putString(KEY_LAST_FROM_UNIT, value.id).apply()

    var lastToUnit: TemperatureUnit
        get() {
            val id = preferences.getString(KEY_LAST_TO_UNIT, DEFAULT_TO_UNIT.id)
            return TemperatureUnit.fromId(id)
        }
        set(value) = preferences.edit().putString(KEY_LAST_TO_UNIT, value.id).apply()

    fun resetSettings() {
        preferences.edit()
            .putString(KEY_THEME, DEFAULT_THEME_MODE.name)
            .putBoolean(KEY_DYNAMIC_COLOR, true)
            .putBoolean(KEY_HAPTIC, true)
            .putBoolean(KEY_ANIMATIONS, true)
            .putInt(KEY_DECIMAL_PLACES, DEFAULT_DECIMAL_PLACES)
            .putString(KEY_ROUNDING_MODE, DEFAULT_ROUNDING_MODE.name)
            .putBoolean(KEY_SHOW_FORMULA, true)
            .putBoolean(KEY_SHOW_TEMP_SCALE, true)
            .putBoolean(KEY_CONFIRM_HISTORY_DELETE, true)
            .putBoolean(KEY_PRESERVE_LAST_CONVERSION, true)
            .apply()
    }
}
