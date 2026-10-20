package com.example.tempshift

import com.example.tempshift.data.model.TempShiftHistory
import com.example.tempshift.data.model.ThemeMode
import com.example.tempshift.data.repository.SettingsRepository
import com.example.tempshift.data.repository.TempShiftHistoryRepository
import com.example.tempshift.domain.TemperatureCalculator
import com.example.tempshift.domain.TemperatureConstants
import com.example.tempshift.domain.TemperatureExplanationBuilder
import com.example.tempshift.domain.TemperatureFormulaProvider
import com.example.tempshift.domain.TemperatureUnit
import com.example.tempshift.domain.TemperatureValidationResult
import com.example.tempshift.domain.TemperatureValidator
import com.example.tempshift.domain.TempShiftFormatter
import com.example.tempshift.domain.TempShiftParser
import com.example.tempshift.ui.CalculationState
import com.example.tempshift.ui.TempShiftViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

class TempShiftComprehensiveTest {

    // 1. Celsius → Celsius
    @Test
    fun test01_celsiusToCelsius() {
        val input = BigDecimal("25")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.CELSIUS)
        assertEquals(BigDecimal("25"), result)
    }

    // 2. Celsius → Fahrenheit
    @Test
    fun test02_celsiusToFahrenheit() {
        val input = BigDecimal("25")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT)
        assertEquals("77", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
        assertEquals("77 °F", TempShiftFormatter.formatTemperature(result, TemperatureUnit.FAHRENHEIT, 2, RoundingMode.HALF_UP))
    }

    // 3. Fahrenheit → Celsius
    @Test
    fun test03_fahrenheitToCelsius() {
        val input = BigDecimal("77")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS)
        assertEquals("25", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
        assertEquals("25 °C", TempShiftFormatter.formatTemperature(result, TemperatureUnit.CELSIUS, 2, RoundingMode.HALF_UP))
    }

    // 4. Celsius → Kelvin
    @Test
    fun test04_celsiusToKelvin() {
        val input = BigDecimal("25")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN)
        assertEquals("298.15", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
        assertEquals("298.15 K", TempShiftFormatter.formatTemperature(result, TemperatureUnit.KELVIN, 2, RoundingMode.HALF_UP))
    }

    // 5. Kelvin → Celsius
    @Test
    fun test05_kelvinToCelsius() {
        val input = BigDecimal("298.15")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.KELVIN, TemperatureUnit.CELSIUS)
        assertEquals("25", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
        assertEquals("25 °C", TempShiftFormatter.formatTemperature(result, TemperatureUnit.CELSIUS, 2, RoundingMode.HALF_UP))
    }

    // 6. Fahrenheit → Kelvin
    @Test
    fun test06_fahrenheitToKelvin() {
        val input = BigDecimal("32")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.FAHRENHEIT, TemperatureUnit.KELVIN)
        assertEquals("273.15", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
        assertEquals("273.15 K", TempShiftFormatter.formatTemperature(result, TemperatureUnit.KELVIN, 2, RoundingMode.HALF_UP))
    }

    // 7. Kelvin → Fahrenheit
    @Test
    fun test07_kelvinToFahrenheit() {
        val input = BigDecimal("273.15")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.KELVIN, TemperatureUnit.FAHRENHEIT)
        assertEquals("32", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
        assertEquals("32 °F", TempShiftFormatter.formatTemperature(result, TemperatureUnit.FAHRENHEIT, 2, RoundingMode.HALF_UP))
    }

    // 8. Zero Celsius
    @Test
    fun test08_zeroCelsius() {
        val input = BigDecimal("0")
        val toF = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT)
        val toK = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN)
        assertEquals("32", TempShiftFormatter.formatNumber(toF, 2, RoundingMode.HALF_UP))
        assertEquals("273.15", TempShiftFormatter.formatNumber(toK, 2, RoundingMode.HALF_UP))
    }

    // 9. Zero Fahrenheit
    @Test
    fun test09_zeroFahrenheit() {
        val input = BigDecimal("0")
        val toC = TemperatureCalculator.convert(input, TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS)
        // (0 - 32) * 5 / 9 = -160 / 9 = -17.777777...
        assertEquals("-17.78", TempShiftFormatter.formatNumber(toC, 2, RoundingMode.HALF_UP))
    }

    // 10. Zero Kelvin
    @Test
    fun test10_zeroKelvin() {
        val input = BigDecimal("0")
        val toC = TemperatureCalculator.convert(input, TemperatureUnit.KELVIN, TemperatureUnit.CELSIUS)
        val toF = TemperatureCalculator.convert(input, TemperatureUnit.KELVIN, TemperatureUnit.FAHRENHEIT)
        assertEquals("-273.15", TempShiftFormatter.formatNumber(toC, 2, RoundingMode.HALF_UP))
        assertEquals("-459.67", TempShiftFormatter.formatNumber(toF, 2, RoundingMode.HALF_UP))
    }

    // 11. 25 Celsius
    @Test
    fun test11_twentyFiveCelsius() {
        val input = BigDecimal("25")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT)
        assertEquals("77", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
    }

    // 12. 100 Celsius
    @Test
    fun test12_hundredCelsius() {
        val input = BigDecimal("100")
        val toF = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT)
        val toK = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN)
        assertEquals("212", TempShiftFormatter.formatNumber(toF, 2, RoundingMode.HALF_UP))
        assertEquals("373.15", TempShiftFormatter.formatNumber(toK, 2, RoundingMode.HALF_UP))
    }

    // 13. 32 Fahrenheit
    @Test
    fun test13_thirtyTwoFahrenheit() {
        val input = BigDecimal("32")
        val toC = TemperatureCalculator.convert(input, TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS)
        val toK = TemperatureCalculator.convert(input, TemperatureUnit.FAHRENHEIT, TemperatureUnit.KELVIN)
        assertEquals("0", TempShiftFormatter.formatNumber(toC, 2, RoundingMode.HALF_UP))
        assertEquals("273.15", TempShiftFormatter.formatNumber(toK, 2, RoundingMode.HALF_UP))
    }

    // 14. 212 Fahrenheit
    @Test
    fun test14_twoHundredTwelveFahrenheit() {
        val input = BigDecimal("212")
        val toC = TemperatureCalculator.convert(input, TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS)
        val toK = TemperatureCalculator.convert(input, TemperatureUnit.FAHRENHEIT, TemperatureUnit.KELVIN)
        assertEquals("100", TempShiftFormatter.formatNumber(toC, 2, RoundingMode.HALF_UP))
        assertEquals("373.15", TempShiftFormatter.formatNumber(toK, 2, RoundingMode.HALF_UP))
    }

    // 15. 273.15 Kelvin
    @Test
    fun test15_twoHundredSeventyThreePointFifteenKelvin() {
        val input = BigDecimal("273.15")
        val toC = TemperatureCalculator.convert(input, TemperatureUnit.KELVIN, TemperatureUnit.CELSIUS)
        val toF = TemperatureCalculator.convert(input, TemperatureUnit.KELVIN, TemperatureUnit.FAHRENHEIT)
        assertEquals("0", TempShiftFormatter.formatNumber(toC, 2, RoundingMode.HALF_UP))
        assertEquals("32", TempShiftFormatter.formatNumber(toF, 2, RoundingMode.HALF_UP))
    }

    // 16. 373.15 Kelvin
    @Test
    fun test16_threeHundredSeventyThreePointFifteenKelvin() {
        val input = BigDecimal("373.15")
        val toC = TemperatureCalculator.convert(input, TemperatureUnit.KELVIN, TemperatureUnit.CELSIUS)
        val toF = TemperatureCalculator.convert(input, TemperatureUnit.KELVIN, TemperatureUnit.FAHRENHEIT)
        assertEquals("100", TempShiftFormatter.formatNumber(toC, 2, RoundingMode.HALF_UP))
        assertEquals("212", TempShiftFormatter.formatNumber(toF, 2, RoundingMode.HALF_UP))
    }

    // 17. Absolute zero Celsius
    @Test
    fun test17_absoluteZeroCelsius() {
        val input = BigDecimal("-273.15")
        val validation = TemperatureValidator.validate(input, TemperatureUnit.CELSIUS)
        assertEquals(TemperatureValidationResult.Valid, validation)
        val toF = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT)
        val toK = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN)
        assertEquals("-459.67", TempShiftFormatter.formatNumber(toF, 2, RoundingMode.HALF_UP))
        assertEquals("0", TempShiftFormatter.formatNumber(toK, 2, RoundingMode.HALF_UP))
    }

    // 18. Absolute zero Fahrenheit
    @Test
    fun test18_absoluteZeroFahrenheit() {
        val input = BigDecimal("-459.67")
        val validation = TemperatureValidator.validate(input, TemperatureUnit.FAHRENHEIT)
        assertEquals(TemperatureValidationResult.Valid, validation)
        val toC = TemperatureCalculator.convert(input, TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS)
        val toK = TemperatureCalculator.convert(input, TemperatureUnit.FAHRENHEIT, TemperatureUnit.KELVIN)
        assertEquals("-273.15", TempShiftFormatter.formatNumber(toC, 2, RoundingMode.HALF_UP))
        assertEquals("0", TempShiftFormatter.formatNumber(toK, 2, RoundingMode.HALF_UP))
    }

    // 19. Absolute zero Kelvin
    @Test
    fun test19_absoluteZeroKelvin() {
        val input = BigDecimal("0")
        val validation = TemperatureValidator.validate(input, TemperatureUnit.KELVIN)
        assertEquals(TemperatureValidationResult.Valid, validation)
        val toC = TemperatureCalculator.convert(input, TemperatureUnit.KELVIN, TemperatureUnit.CELSIUS)
        val toF = TemperatureCalculator.convert(input, TemperatureUnit.KELVIN, TemperatureUnit.FAHRENHEIT)
        assertEquals("-273.15", TempShiftFormatter.formatNumber(toC, 2, RoundingMode.HALF_UP))
        assertEquals("-459.67", TempShiftFormatter.formatNumber(toF, 2, RoundingMode.HALF_UP))
    }

    // 20. Below absolute zero Celsius
    @Test
    fun test20_belowAbsoluteZeroCelsius() {
        val input = BigDecimal("-273.151")
        val validation = TemperatureValidator.validate(input, TemperatureUnit.CELSIUS)
        assertTrue(validation is TemperatureValidationResult.Invalid)
        assertEquals("Temperature is below absolute zero.", (validation as TemperatureValidationResult.Invalid).message)
    }

    // 21. Below absolute zero Fahrenheit
    @Test
    fun test21_belowAbsoluteZeroFahrenheit() {
        val input = BigDecimal("-459.671")
        val validation = TemperatureValidator.validate(input, TemperatureUnit.FAHRENHEIT)
        assertTrue(validation is TemperatureValidationResult.Invalid)
        assertEquals("Temperature is below absolute zero.", (validation as TemperatureValidationResult.Invalid).message)
    }

    // 22. Below absolute zero Kelvin
    @Test
    fun test22_belowAbsoluteZeroKelvin() {
        val input = BigDecimal("-0.001")
        val validation = TemperatureValidator.validate(input, TemperatureUnit.KELVIN)
        assertTrue(validation is TemperatureValidationResult.Invalid)
        assertEquals("Kelvin cannot be below 0 K.", (validation as TemperatureValidationResult.Invalid).message)
    }

    // 23. Negative Celsius
    @Test
    fun test23_negativeCelsius() {
        val input = BigDecimal("-40")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT)
        assertEquals("-40", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
    }

    // 24. Negative Fahrenheit
    @Test
    fun test24_negativeFahrenheit() {
        val input = BigDecimal("-40")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS)
        assertEquals("-40", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
    }

    // 25. Decimal values
    @Test
    fun test25_decimalValues() {
        val input = BigDecimal("37.5")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT)
        assertEquals("99.5", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
    }

    // 26. Leading decimal
    @Test
    fun test26_leadingDecimal() {
        val parsed = TempShiftParser.parseTemperature(".5").getOrThrow()
        assertEquals(BigDecimal("0.5"), parsed)
        val result = TemperatureCalculator.convert(parsed, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT)
        assertEquals("32.9", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
    }

    // 27. Trailing decimal
    @Test
    fun test27_trailingDecimal() {
        val parsed = TempShiftParser.parseTemperature("10.").getOrThrow()
        assertEquals(BigDecimal("10"), parsed)
        val result = TemperatureCalculator.convert(parsed, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT)
        assertEquals("50", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
    }

    // 28. Scientific notation
    @Test
    fun test28_scientificNotation() {
        val parsed = TempShiftParser.parseTemperature("1E+2").getOrThrow()
        val result = TemperatureCalculator.convert(parsed, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT)
        assertEquals("212", TempShiftFormatter.formatNumber(result, 2, RoundingMode.HALF_UP))
    }

    // 29. Very small values
    @Test
    fun test29_verySmallValues() {
        val parsed = TempShiftParser.parseTemperature("0.001").getOrThrow()
        val result = TemperatureCalculator.convert(parsed, TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN)
        assertEquals("273.151", TempShiftFormatter.formatNumber(result, 3, RoundingMode.HALF_UP))
    }

    // 30. Very large values
    @Test
    fun test30_veryLargeValues() {
        val large = "999999999999999999999999"
        val parsed = TempShiftParser.parseTemperature(large).getOrThrow()
        val result = TemperatureCalculator.convert(parsed, TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN)
        // Result should equal 999999999999999999999999 + 273.15
        val expected = BigDecimal(large).add(BigDecimal("273.15"))
        assertEquals(expected, result)
    }

    // 31. BigDecimal precision
    @Test
    fun test31_bigDecimalPrecision() {
        val input = BigDecimal("0.1")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT)
        // 0.1 * 9 / 5 + 32 = 0.9 / 5 + 32 = 0.18 + 32 = 32.18
        assertEquals(BigDecimal("32.18"), result.stripTrailingZeros())
    }

    // 32. Same-unit conversion
    @Test
    fun test32_sameUnitConversion() {
        val input = BigDecimal("25")
        val result = TemperatureCalculator.convert(input, TemperatureUnit.CELSIUS, TemperatureUnit.CELSIUS)
        assertEquals(input, result)
    }

    // 33. Invalid input
    @Test
    fun test33_invalidInput() {
        val result = TempShiftParser.parseTemperature("abc")
        assertTrue(result.isFailure)
        assertEquals("Enter a valid temperature.", result.exceptionOrNull()?.message)
    }

    // 34. Empty input
    @Test
    fun test34_emptyInput() {
        val result = TempShiftParser.parseTemperature("")
        assertTrue(result.isFailure)
        assertEquals("Enter a temperature to begin.", result.exceptionOrNull()?.message)
    }

    // 35. Multiple decimal points
    @Test
    fun test35_multipleDecimalPoints() {
        val result1 = TempShiftParser.parseTemperature("12..5")
        val result2 = TempShiftParser.parseTemperature("1.2.3")
        assertTrue(result1.isFailure)
        assertTrue(result2.isFailure)
        assertEquals("Enter a valid temperature.", result1.exceptionOrNull()?.message)
        assertEquals("Enter a valid temperature.", result2.exceptionOrNull()?.message)
    }

    // 36. Invalid sign
    @Test
    fun test36_invalidSign() {
        val result = TempShiftParser.parseTemperature("--")
        assertTrue(result.isFailure)
        assertEquals("Enter a valid temperature.", result.exceptionOrNull()?.message)
    }

    // 37. Maximum input length
    @Test
    fun test37_maximumInputLength() {
        val longInput = "1".repeat(501)
        val result = TempShiftParser.parseTemperature(longInput)
        assertTrue(result.isFailure)
        assertEquals("Temperature is too long. Maximum 500 digits.", result.exceptionOrNull()?.message)
    }

    // 38. HALF_UP display rounding
    @Test
    fun test38_halfUpRounding() {
        val value = BigDecimal("2.555")
        assertEquals("2.56", TempShiftFormatter.formatNumber(value, 2, RoundingMode.HALF_UP))
    }

    // 39. HALF_DOWN display rounding
    @Test
    fun test39_halfDownRounding() {
        val value = BigDecimal("2.555")
        assertEquals("2.55", TempShiftFormatter.formatNumber(value, 2, RoundingMode.HALF_DOWN))
    }

    // 40. HALF_EVEN display rounding
    @Test
    fun test40_halfEvenRounding() {
        val value1 = BigDecimal("2.525")
        val value2 = BigDecimal("2.535")
        assertEquals("2.52", TempShiftFormatter.formatNumber(value1, 2, RoundingMode.HALF_EVEN))
        assertEquals("2.54", TempShiftFormatter.formatNumber(value2, 2, RoundingMode.HALF_EVEN))
    }

    // 41. UP display rounding
    @Test
    fun test41_upRounding() {
        val value = BigDecimal("2.551")
        assertEquals("2.56", TempShiftFormatter.formatNumber(value, 2, RoundingMode.UP))
    }

    // 42. DOWN display rounding
    @Test
    fun test42_downRounding() {
        val value = BigDecimal("2.559")
        assertEquals("2.55", TempShiftFormatter.formatNumber(value, 2, RoundingMode.DOWN))
    }

    // 43. CEILING display rounding
    @Test
    fun test43_ceilingRounding() {
        val pos = BigDecimal("2.551")
        val neg = BigDecimal("-2.559")
        assertEquals("2.56", TempShiftFormatter.formatNumber(pos, 2, RoundingMode.CEILING))
        assertEquals("-2.55", TempShiftFormatter.formatNumber(neg, 2, RoundingMode.CEILING))
    }

    // 44. FLOOR display rounding
    @Test
    fun test44_floorRounding() {
        val pos = BigDecimal("2.559")
        val neg = BigDecimal("-2.551")
        assertEquals("2.55", TempShiftFormatter.formatNumber(pos, 2, RoundingMode.FLOOR))
        assertEquals("-2.56", TempShiftFormatter.formatNumber(neg, 2, RoundingMode.FLOOR))
    }

    // 45. Trailing-zero formatting
    @Test
    fun test45_trailingZeroFormatting() {
        assertEquals("25", TempShiftFormatter.formatNumber(BigDecimal("25.00"), 2, RoundingMode.HALF_UP))
        assertEquals("77", TempShiftFormatter.formatNumber(BigDecimal("77.0"), 2, RoundingMode.HALF_UP))
        assertEquals("298.15", TempShiftFormatter.formatNumber(BigDecimal("298.150"), 2, RoundingMode.HALF_UP))
    }

    // 46. Formula generation
    @Test
    fun test46_formulaGeneration() {
        assertEquals("°C × 9/5 + 32", TemperatureFormulaProvider.formula(TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT))
        assertEquals("(°F - 32) × 5/9", TemperatureFormulaProvider.formula(TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS))
        assertEquals("°C + 273.15", TemperatureFormulaProvider.formula(TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN))
        assertEquals("K - 273.15", TemperatureFormulaProvider.formula(TemperatureUnit.KELVIN, TemperatureUnit.CELSIUS))
        assertEquals("(°F - 32) × 5/9 + 273.15", TemperatureFormulaProvider.formula(TemperatureUnit.FAHRENHEIT, TemperatureUnit.KELVIN))
        assertEquals("(K - 273.15) × 9/5 + 32", TemperatureFormulaProvider.formula(TemperatureUnit.KELVIN, TemperatureUnit.FAHRENHEIT))
    }

    // 47. Calculation explanation
    @Test
    fun test47_calculationExplanation() {
        val details = TemperatureExplanationBuilder.build(
            input = BigDecimal("25"),
            result = BigDecimal("77"),
            from = TemperatureUnit.CELSIUS,
            to = TemperatureUnit.FAHRENHEIT
        )
        assertEquals("°C × 9/5 + 32", details.formula)
        assertEquals("25 × 9/5 + 32 = 77", details.calculation)

        val reverseDetails = TemperatureExplanationBuilder.build(
            input = BigDecimal("77"),
            result = BigDecimal("25"),
            from = TemperatureUnit.FAHRENHEIT,
            to = TemperatureUnit.CELSIUS
        )
        assertEquals("(°F - 32) × 5/9", reverseDetails.formula)
        assertEquals("(77 - 32) × 5/9 = 25", reverseDetails.calculation)
    }

    // 48. Swap behavior
    @Test
    fun test48_swapBehavior() {
        val prefs = FakeSharedPreferences()
        val settingsRepo = SettingsRepository(preferences = prefs)
        val historyRepo = TempShiftHistoryRepository(preferences = prefs)
        val viewModel = TempShiftViewModel(settingsRepo, historyRepo)

        // Initial state is 25 °C -> 77 °F
        assertEquals("25", viewModel.uiState.value.inputText)
        assertEquals(TemperatureUnit.CELSIUS, viewModel.uiState.value.fromUnit)
        assertEquals(TemperatureUnit.FAHRENHEIT, viewModel.uiState.value.toUnit)
        assertEquals("77", TempShiftFormatter.formatNumber(viewModel.uiState.value.result!!.resultValue, 2, RoundingMode.HALF_UP))

        // Trigger swap
        viewModel.onSwapUnits()
        assertEquals("77", viewModel.uiState.value.inputText)
        assertEquals(TemperatureUnit.FAHRENHEIT, viewModel.uiState.value.fromUnit)
        assertEquals(TemperatureUnit.CELSIUS, viewModel.uiState.value.toUnit)
        assertEquals("25", TempShiftFormatter.formatNumber(viewModel.uiState.value.result!!.resultValue, 2, RoundingMode.HALF_UP))
    }

    // 49. History save
    @Test
    fun test49_historySave() {
        val prefs = FakeSharedPreferences()
        val historyRepo = TempShiftHistoryRepository(preferences = prefs)
        val item = TempShiftHistory(
            inputValue = "25",
            fromUnit = TemperatureUnit.CELSIUS,
            toUnit = TemperatureUnit.FAHRENHEIT,
            resultValue = "77"
        )
        val list = historyRepo.saveHistoryItem(item)
        assertEquals(1, list.size)
        assertEquals("25", list[0].inputValue)
    }

    // 50. History load
    @Test
    fun test50_historyLoad() {
        val prefs = FakeSharedPreferences()
        val historyRepo = TempShiftHistoryRepository(preferences = prefs)
        val item = TempShiftHistory(
            inputValue = "100",
            fromUnit = TemperatureUnit.CELSIUS,
            toUnit = TemperatureUnit.FAHRENHEIT,
            resultValue = "212"
        )
        historyRepo.saveHistoryItem(item)
        val loaded = historyRepo.getHistory()
        assertEquals(1, loaded.size)
        assertEquals("100", loaded[0].inputValue)
        assertEquals("212", loaded[0].resultValue)
    }

    // 51. History limit
    @Test
    fun test51_historyLimit() {
        val prefs = FakeSharedPreferences()
        val historyRepo = TempShiftHistoryRepository(preferences = prefs)
        for (i in 1..15) {
            historyRepo.saveHistoryItem(
                TempShiftHistory(
                    inputValue = i.toString(),
                    fromUnit = TemperatureUnit.CELSIUS,
                    toUnit = TemperatureUnit.FAHRENHEIT,
                    resultValue = (i * 2).toString()
                )
            )
        }
        val list = historyRepo.getHistory()
        assertEquals(10, list.size)
        // Most recent should be 15
        assertEquals("15", list[0].inputValue)
        // Oldest preserved should be 6
        assertEquals("6", list[9].inputValue)
    }

    // 52. History deletion
    @Test
    fun test52_historyDeletion() {
        val prefs = FakeSharedPreferences()
        val historyRepo = TempShiftHistoryRepository(preferences = prefs)
        val item1 = TempShiftHistory(id = "1", inputValue = "10", fromUnit = TemperatureUnit.CELSIUS, toUnit = TemperatureUnit.FAHRENHEIT, resultValue = "50")
        val item2 = TempShiftHistory(id = "2", inputValue = "20", fromUnit = TemperatureUnit.CELSIUS, toUnit = TemperatureUnit.FAHRENHEIT, resultValue = "68")
        historyRepo.saveHistoryItem(item1)
        historyRepo.saveHistoryItem(item2)

        val updated = historyRepo.deleteHistoryItem("1")
        assertEquals(1, updated.size)
        assertEquals("2", updated[0].id)
    }

    // 53. History clear
    @Test
    fun test53_historyClear() {
        val prefs = FakeSharedPreferences()
        val historyRepo = TempShiftHistoryRepository(preferences = prefs)
        historyRepo.saveHistoryItem(TempShiftHistory(inputValue = "10", fromUnit = TemperatureUnit.CELSIUS, toUnit = TemperatureUnit.FAHRENHEIT, resultValue = "50"))
        historyRepo.clearHistory()
        assertEquals(0, historyRepo.getHistory().size)
    }

    // 54. History restore
    @Test
    fun test54_historyRestore() {
        val prefs = FakeSharedPreferences()
        val settingsRepo = SettingsRepository(preferences = prefs)
        val historyRepo = TempShiftHistoryRepository(preferences = prefs)
        val viewModel = TempShiftViewModel(settingsRepo, historyRepo)

        val item = TempShiftHistory(
            inputValue = "100",
            fromUnit = TemperatureUnit.CELSIUS,
            toUnit = TemperatureUnit.KELVIN,
            resultValue = "373.15"
        )
        viewModel.onRestoreHistory(item)
        assertEquals("100", viewModel.uiState.value.inputText)
        assertEquals(TemperatureUnit.CELSIUS, viewModel.uiState.value.fromUnit)
        assertEquals(TemperatureUnit.KELVIN, viewModel.uiState.value.toUnit)
        assertEquals("373.15", TempShiftFormatter.formatNumber(viewModel.uiState.value.result!!.resultValue, 2, RoundingMode.HALF_UP))
    }

    // 55. Settings persistence
    @Test
    fun test55_settingsPersistence() {
        val prefs = FakeSharedPreferences()
        val settingsRepo = SettingsRepository(preferences = prefs)
        settingsRepo.themeMode = ThemeMode.DARK
        settingsRepo.decimalPlaces = 4

        val reloadedSettings = SettingsRepository(preferences = prefs)
        assertEquals(ThemeMode.DARK, reloadedSettings.themeMode)
        assertEquals(4, reloadedSettings.decimalPlaces)
    }

    // 56. Last conversion persistence
    @Test
    fun test56_lastConversionPersistence() {
        val prefs = FakeSharedPreferences()
        val settingsRepo = SettingsRepository(preferences = prefs)
        val historyRepo = TempShiftHistoryRepository(preferences = prefs)
        val viewModel = TempShiftViewModel(settingsRepo, historyRepo)

        viewModel.onInputChange("37")
        viewModel.onFromUnitChange(TemperatureUnit.CELSIUS)
        viewModel.onToUnitChange(TemperatureUnit.FAHRENHEIT)

        val reloadedSettings = SettingsRepository(preferences = prefs)
        assertEquals("37", reloadedSettings.lastInput)
        assertEquals(TemperatureUnit.CELSIUS, reloadedSettings.lastFromUnit)
        assertEquals(TemperatureUnit.FAHRENHEIT, reloadedSettings.lastToUnit)
    }

    // 57. Reset
    @Test
    fun test57_reset() {
        val prefs = FakeSharedPreferences()
        val settingsRepo = SettingsRepository(preferences = prefs)
        val historyRepo = TempShiftHistoryRepository(preferences = prefs)
        val viewModel = TempShiftViewModel(settingsRepo, historyRepo)

        viewModel.onInputChange("100")
        viewModel.onFromUnitChange(TemperatureUnit.KELVIN)
        viewModel.onReset()

        assertEquals("25", viewModel.uiState.value.inputText)
        assertEquals(TemperatureUnit.CELSIUS, viewModel.uiState.value.fromUnit)
        assertEquals(TemperatureUnit.FAHRENHEIT, viewModel.uiState.value.toUnit)
        assertEquals("77", TempShiftFormatter.formatNumber(viewModel.uiState.value.result!!.resultValue, 2, RoundingMode.HALF_UP))
    }

    // 58. Duplicate history protection
    @Test
    fun test58_duplicateHistoryProtection() {
        val prefs = FakeSharedPreferences()
        val historyRepo = TempShiftHistoryRepository(preferences = prefs)
        val item1 = TempShiftHistory(inputValue = "25", fromUnit = TemperatureUnit.CELSIUS, toUnit = TemperatureUnit.FAHRENHEIT, resultValue = "77")
        val item2 = TempShiftHistory(inputValue = "25", fromUnit = TemperatureUnit.CELSIUS, toUnit = TemperatureUnit.FAHRENHEIT, resultValue = "77")

        val list1 = historyRepo.saveHistoryItem(item1)
        val list2 = historyRepo.saveHistoryItem(item2)
        assertEquals(1, list1.size)
        assertEquals(1, list2.size)
    }

    // 59. Rapid input protection
    @Test
    fun test59_rapidInputProtection() {
        val prefs = FakeSharedPreferences()
        val settingsRepo = SettingsRepository(preferences = prefs)
        val historyRepo = TempShiftHistoryRepository(preferences = prefs)
        val viewModel = TempShiftViewModel(settingsRepo, historyRepo)

        viewModel.onInputChange("1")
        viewModel.onInputChange("12")
        viewModel.onInputChange("123")
        viewModel.onInputChange("25")

        assertEquals("25", viewModel.uiState.value.inputText)
        assertEquals("77", TempShiftFormatter.formatNumber(viewModel.uiState.value.result!!.resultValue, 2, RoundingMode.HALF_UP))
        assertEquals(CalculationState.Calculated, viewModel.uiState.value.calculationState)
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
