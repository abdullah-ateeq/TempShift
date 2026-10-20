package com.example.tempshift.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tempshift.data.model.TempShiftHistory
import com.example.tempshift.data.model.ThemeMode
import com.example.tempshift.data.repository.SettingsRepository
import com.example.tempshift.data.repository.TempShiftHistoryRepository
import com.example.tempshift.domain.TemperatureCalculator
import com.example.tempshift.domain.TemperatureExplanationBuilder
import com.example.tempshift.domain.TemperatureResult
import com.example.tempshift.domain.TemperatureUnit
import com.example.tempshift.domain.TemperatureValidationResult
import com.example.tempshift.domain.TemperatureValidator
import com.example.tempshift.domain.TempShiftParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class TempShiftViewModel(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: TempShiftHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TempShiftUiState(
            inputText = if (settingsRepository.preserveLastConversion) settingsRepository.lastInput else SettingsRepository.DEFAULT_INPUT,
            fromUnit = if (settingsRepository.preserveLastConversion) settingsRepository.lastFromUnit else SettingsRepository.DEFAULT_FROM_UNIT,
            toUnit = if (settingsRepository.preserveLastConversion) settingsRepository.lastToUnit else SettingsRepository.DEFAULT_TO_UNIT,
            history = historyRepository.getHistory(),
            decimalPlaces = settingsRepository.decimalPlaces,
            roundingMode = settingsRepository.roundingMode,
            showFormula = settingsRepository.showFormula,
            showTemperatureScale = settingsRepository.showTemperatureScale,
            themeMode = settingsRepository.themeMode,
            dynamicColor = settingsRepository.dynamicColor,
            hapticFeedback = settingsRepository.hapticFeedback,
            animations = settingsRepository.animations,
            confirmHistoryDelete = settingsRepository.confirmHistoryDelete,
            preserveLastConversion = settingsRepository.preserveLastConversion
        )
    )
    val uiState: StateFlow<TempShiftUiState> = _uiState.asStateFlow()

    init {
        val currentState = _uiState.value
        calculateConversion(
            input = currentState.inputText,
            from = currentState.fromUnit,
            to = currentState.toUnit,
            decimalPlaces = currentState.decimalPlaces,
            roundingMode = currentState.roundingMode
        )
    }

    fun onInputChange(newInput: String) {
        _uiState.update { it.copy(inputText = newInput) }
        val state = _uiState.value
        calculateConversion(
            input = newInput,
            from = state.fromUnit,
            to = state.toUnit,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )
    }

    fun onFromUnitChange(newFrom: TemperatureUnit) {
        if (_uiState.value.fromUnit == newFrom) return
        _uiState.update { it.copy(fromUnit = newFrom) }
        val state = _uiState.value
        calculateConversion(
            input = state.inputText,
            from = newFrom,
            to = state.toUnit,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )
    }

    fun onToUnitChange(newTo: TemperatureUnit) {
        if (_uiState.value.toUnit == newTo) return
        _uiState.update { it.copy(toUnit = newTo) }
        val state = _uiState.value
        calculateConversion(
            input = state.inputText,
            from = state.fromUnit,
            to = newTo,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )
    }

    fun onSwapUnits() {
        val state = _uiState.value
        val newFrom = state.toUnit
        val newTo = state.fromUnit

        val newInput = if (state.result != null) {
            state.result.resultValue.stripTrailingZeros().toPlainString()
        } else {
            state.inputText
        }

        _uiState.update {
            it.copy(
                fromUnit = newFrom,
                toUnit = newTo,
                inputText = newInput
            )
        }

        calculateConversion(
            input = newInput,
            from = newFrom,
            to = newTo,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )
    }

    fun onQuickValueSelected(value: String, unit: TemperatureUnit) {
        val state = _uiState.value
        val targetTo = if (unit == state.toUnit) {
            when (unit) {
                TemperatureUnit.CELSIUS -> TemperatureUnit.FAHRENHEIT
                TemperatureUnit.FAHRENHEIT -> TemperatureUnit.CELSIUS
                TemperatureUnit.KELVIN -> TemperatureUnit.CELSIUS
            }
        } else {
            state.toUnit
        }

        _uiState.update {
            it.copy(
                inputText = value,
                fromUnit = unit,
                toUnit = targetTo
            )
        }

        calculateConversion(
            input = value,
            from = unit,
            to = targetTo,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )
    }

    fun onSaveToHistory() {
        val state = _uiState.value
        val result = state.result ?: return

        val historyItem = TempShiftHistory(
            inputValue = result.inputValue.stripTrailingZeros().toPlainString(),
            fromUnit = result.fromUnit,
            toUnit = result.toUnit,
            resultValue = result.resultValue.stripTrailingZeros().toPlainString()
        )

        val updatedList = historyRepository.saveHistoryItem(historyItem)
        _uiState.update {
            it.copy(
                history = updatedList,
                snackbarMessage = "Saved to history"
            )
        }
    }

    fun onDeleteHistoryItem(id: String) {
        val updatedList = historyRepository.deleteHistoryItem(id)
        _uiState.update {
            it.copy(
                history = updatedList,
                snackbarMessage = "Conversion removed"
            )
        }
    }

    fun onClearHistory() {
        historyRepository.clearHistory()
        _uiState.update {
            it.copy(
                history = emptyList(),
                snackbarMessage = "History cleared"
            )
        }
    }

    fun onRestoreHistory(history: TempShiftHistory) {
        _uiState.update {
            it.copy(
                inputText = history.inputValue,
                fromUnit = history.fromUnit,
                toUnit = history.toUnit
            )
        }
        val state = _uiState.value
        calculateConversion(
            input = history.inputValue,
            from = history.fromUnit,
            to = history.toUnit,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )
    }

    fun onReset() {
        val defaultInput = SettingsRepository.DEFAULT_INPUT
        val defaultFrom = SettingsRepository.DEFAULT_FROM_UNIT
        val defaultTo = SettingsRepository.DEFAULT_TO_UNIT

        _uiState.update {
            it.copy(
                inputText = defaultInput,
                fromUnit = defaultFrom,
                toUnit = defaultTo
            )
        }

        val state = _uiState.value
        calculateConversion(
            input = defaultInput,
            from = defaultFrom,
            to = defaultTo,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )
    }

    fun onThemeChange(mode: ThemeMode) {
        settingsRepository.themeMode = mode
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun onDynamicColorChange(enabled: Boolean) {
        settingsRepository.dynamicColor = enabled
        _uiState.update { it.copy(dynamicColor = enabled) }
    }

    fun onHapticChange(enabled: Boolean) {
        settingsRepository.hapticFeedback = enabled
        _uiState.update { it.copy(hapticFeedback = enabled) }
    }

    fun onAnimationsChange(enabled: Boolean) {
        settingsRepository.animations = enabled
        _uiState.update { it.copy(animations = enabled) }
    }

    fun onDecimalPlacesChange(places: Int) {
        settingsRepository.decimalPlaces = places
        _uiState.update { it.copy(decimalPlaces = places) }
        val state = _uiState.value
        calculateConversion(
            input = state.inputText,
            from = state.fromUnit,
            to = state.toUnit,
            decimalPlaces = places,
            roundingMode = state.roundingMode
        )
    }

    fun onRoundingModeChange(mode: RoundingMode) {
        settingsRepository.roundingMode = mode
        _uiState.update { it.copy(roundingMode = mode) }
        val state = _uiState.value
        calculateConversion(
            input = state.inputText,
            from = state.fromUnit,
            to = state.toUnit,
            decimalPlaces = state.decimalPlaces,
            roundingMode = mode
        )
    }

    fun onShowFormulaChange(show: Boolean) {
        settingsRepository.showFormula = show
        _uiState.update { it.copy(showFormula = show) }
    }

    fun onShowTemperatureScaleChange(show: Boolean) {
        settingsRepository.showTemperatureScale = show
        _uiState.update { it.copy(showTemperatureScale = show) }
    }

    fun onConfirmHistoryDeleteChange(confirm: Boolean) {
        settingsRepository.confirmHistoryDelete = confirm
        _uiState.update { it.copy(confirmHistoryDelete = confirm) }
    }

    fun onPreserveLastConversionChange(preserve: Boolean) {
        settingsRepository.preserveLastConversion = preserve
        _uiState.update { it.copy(preserveLastConversion = preserve) }
    }

    fun onResetSettings() {
        settingsRepository.resetSettings()
        _uiState.update {
            it.copy(
                themeMode = settingsRepository.themeMode,
                dynamicColor = settingsRepository.dynamicColor,
                hapticFeedback = settingsRepository.hapticFeedback,
                animations = settingsRepository.animations,
                decimalPlaces = settingsRepository.decimalPlaces,
                roundingMode = settingsRepository.roundingMode,
                showFormula = settingsRepository.showFormula,
                showTemperatureScale = settingsRepository.showTemperatureScale,
                confirmHistoryDelete = settingsRepository.confirmHistoryDelete,
                preserveLastConversion = settingsRepository.preserveLastConversion,
                snackbarMessage = "Settings reset to defaults"
            )
        }
        val state = _uiState.value
        calculateConversion(
            input = state.inputText,
            from = state.fromUnit,
            to = state.toUnit,
            decimalPlaces = state.decimalPlaces,
            roundingMode = state.roundingMode
        )
    }

    fun onDismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    private fun calculateConversion(
        input: String,
        from: TemperatureUnit,
        to: TemperatureUnit,
        decimalPlaces: Int,
        roundingMode: RoundingMode
    ) {
        val parseResult = TempShiftParser.parseTemperature(input)
        if (parseResult.isFailure) {
            val message = parseResult.exceptionOrNull()?.message ?: "Enter a valid temperature."
            _uiState.update {
                it.copy(
                    result = null,
                    errorMessage = message,
                    calculationState = CalculationState.Error(message),
                    isCalculating = false
                )
            }
            return
        }

        val parsedValue = parseResult.getOrThrow()
        val validation = TemperatureValidator.validate(parsedValue, from)
        if (validation is TemperatureValidationResult.Invalid) {
            _uiState.update {
                it.copy(
                    result = null,
                    errorMessage = validation.message,
                    calculationState = CalculationState.Error(validation.message),
                    isCalculating = false
                )
            }
            return
        }

        val convertedValue = TemperatureCalculator.convert(parsedValue, from, to)
        val explanation = TemperatureExplanationBuilder.build(
            input = parsedValue,
            result = convertedValue,
            from = from,
            to = to,
            decimalPlaces = decimalPlaces,
            roundingMode = roundingMode
        )

        val tempResult = TemperatureResult(
            inputValue = parsedValue,
            resultValue = convertedValue,
            fromUnit = from,
            toUnit = to,
            formula = explanation.formula,
            calculation = explanation.calculation
        )

        if (settingsRepository.preserveLastConversion) {
            settingsRepository.lastInput = input
            settingsRepository.lastFromUnit = from
            settingsRepository.lastToUnit = to
        }

        _uiState.update {
            it.copy(
                result = tempResult,
                errorMessage = null,
                calculationState = CalculationState.Calculated,
                isCalculating = false
            )
        }
    }

    companion object {
        fun provideFactory(
            settingsRepository: SettingsRepository,
            historyRepository: TempShiftHistoryRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TempShiftViewModel(settingsRepository, historyRepository) as T
            }
        }
    }
}
