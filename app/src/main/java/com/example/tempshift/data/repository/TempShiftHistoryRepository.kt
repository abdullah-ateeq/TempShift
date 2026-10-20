package com.example.tempshift.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.tempshift.data.model.TempShiftHistory
import com.example.tempshift.domain.TemperatureUnit

class TempShiftHistoryRepository(
    context: Context? = null,
    preferences: SharedPreferences? = null
) {

    private val preferences: SharedPreferences =
        preferences ?: context!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "tempshift_history_prefs"
        private const val KEY_HISTORY = "key_history_items"
        const val MAX_HISTORY_ITEMS = 10
        private const val ITEM_SEPARATOR = "|||ITEM|||"
        private const val FIELD_SEPARATOR = "|||FIELD|||"
    }

    fun getHistory(): List<TempShiftHistory> {
        val serialized = preferences.getString(KEY_HISTORY, null) ?: return emptyList()
        return deserializeHistory(serialized)
    }

    fun saveHistoryItem(item: TempShiftHistory): List<TempShiftHistory> {
        val currentList = getHistory().toMutableList()

        // Duplicate history check:
        // Do not create unnecessary duplicate entries when input, fromUnit, toUnit
        // are identical to the most recent saved conversion.
        val mostRecent = currentList.firstOrNull()
        if (mostRecent != null &&
            mostRecent.inputValue == item.inputValue &&
            mostRecent.fromUnit == item.fromUnit &&
            mostRecent.toUnit == item.toUnit
        ) {
            return currentList
        }

        // Add to front (newest first)
        currentList.add(0, item)

        // Limit to 10 entries (remove oldest from end if size > 10)
        while (currentList.size > MAX_HISTORY_ITEMS) {
            currentList.removeAt(currentList.lastIndex)
        }

        saveList(currentList)
        return currentList
    }

    fun deleteHistoryItem(id: String): List<TempShiftHistory> {
        val currentList = getHistory().toMutableList()
        currentList.removeAll { it.id == id }
        saveList(currentList)
        return currentList
    }

    fun clearHistory() {
        preferences.edit().remove(KEY_HISTORY).apply()
    }

    private fun saveList(list: List<TempShiftHistory>) {
        val serialized = list.joinToString(ITEM_SEPARATOR) { item ->
            listOf(
                item.id,
                item.inputValue,
                item.fromUnit.id,
                item.toUnit.id,
                item.resultValue,
                item.calculatedAt.toString()
            ).joinToString(FIELD_SEPARATOR)
        }
        preferences.edit().putString(KEY_HISTORY, serialized).apply()
    }

    private fun deserializeHistory(data: String): List<TempShiftHistory> {
        if (data.isBlank()) return emptyList()
        return try {
            data.split(ITEM_SEPARATOR).mapNotNull { itemStr ->
                val fields = itemStr.split(FIELD_SEPARATOR)
                if (fields.size >= 6) {
                    TempShiftHistory(
                        id = fields[0],
                        inputValue = fields[1],
                        fromUnit = TemperatureUnit.fromId(fields[2]),
                        toUnit = TemperatureUnit.fromId(fields[3]),
                        resultValue = fields[4],
                        calculatedAt = fields[5].toLongOrNull() ?: System.currentTimeMillis()
                    )
                } else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
