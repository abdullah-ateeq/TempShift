package com.example.tempshift.ui.home.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempshift.domain.TemperatureUnit

data class QuickTemperatureItem(
    val value: String,
    val unit: TemperatureUnit,
    val label: String,
    val speechLabel: String
)

@Composable
fun QuickValuesRow(
    currentInput: String,
    currentFromUnit: TemperatureUnit,
    onQuickSelected: (String, TemperatureUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickItems = listOf(
        QuickTemperatureItem("0", TemperatureUnit.CELSIUS, "0 °C", "Use zero degrees Celsius"),
        QuickTemperatureItem("25", TemperatureUnit.CELSIUS, "25 °C", "Use twenty-five degrees Celsius"),
        QuickTemperatureItem("37", TemperatureUnit.CELSIUS, "37 °C", "Use thirty-seven degrees Celsius"),
        QuickTemperatureItem("100", TemperatureUnit.CELSIUS, "100 °C", "Use one hundred degrees Celsius"),
        QuickTemperatureItem("32", TemperatureUnit.FAHRENHEIT, "32 °F", "Use thirty-two degrees Fahrenheit"),
        QuickTemperatureItem("212", TemperatureUnit.FAHRENHEIT, "212 °F", "Use two hundred twelve degrees Fahrenheit"),
        QuickTemperatureItem("273.15", TemperatureUnit.KELVIN, "273.15 K", "Use two hundred seventy-three point fifteen Kelvin")
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "QUICK VALUES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickItems.forEach { item ->
                val isSelected = currentInput == item.value && currentFromUnit == item.unit
                FilterChip(
                    selected = isSelected,
                    onClick = { onQuickSelected(item.value, item.unit) },
                    label = {
                        Text(
                            text = item.label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = item.speechLabel
                    }
                )
            }
        }
    }
}
