package com.example.tempshift.ui.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempshift.domain.TemperatureCalculator
import com.example.tempshift.domain.TemperatureResult
import com.example.tempshift.domain.TemperatureUnit
import com.example.tempshift.ui.theme.TempCold
import com.example.tempshift.ui.theme.TempHot
import com.example.tempshift.ui.theme.TempMild
import com.example.tempshift.ui.theme.TempWarm
import java.math.BigDecimal

@Composable
fun TemperatureScaleVisual(
    result: TemperatureResult?,
    animationsEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    // Determine normalized position based on Celsius equivalent (-100°C to +150°C range)
    val celsiusValue: Double = if (result != null) {
        try {
            val c = TemperatureCalculator.convert(
                result.inputValue,
                result.fromUnit,
                TemperatureUnit.CELSIUS
            )
            // Clamp to float representation for decorative visual
            c.toDouble().coerceIn(-100.0, 150.0)
        } catch (e: Exception) {
            25.0
        }
    } else {
        25.0
    }

    // Map -100..150 to fraction 0.0..1.0
    val targetFraction = ((celsiusValue - (-100.0)) / (150.0 - (-100.0))).toFloat().coerceIn(0.02f, 0.98f)

    val animatedFraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = if (animationsEnabled) tween(durationMillis = 350) else tween(durationMillis = 0),
        label = "temp_indicator"
    )

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cold",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TempCold
                )

                Text(
                    text = "Relative Temperature Scale",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Hot",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TempHot
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Bar
            val primaryColor = MaterialTheme.colorScheme.primary
            val onSurfaceColor = MaterialTheme.colorScheme.onSurface
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            ) {
                val barHeight = 8.dp.toPx()
                val yOffset = (size.height - barHeight) / 2
                val cornerRadius = CornerRadius(barHeight / 2, barHeight / 2)

                // Gradient background track
                val gradient = Brush.horizontalGradient(
                    colors = listOf(
                        TempCold,
                        TempMild,
                        TempWarm,
                        TempHot
                    )
                )
                drawRoundRect(
                    brush = gradient,
                    topLeft = Offset(0f, yOffset),
                    size = Size(size.width, barHeight),
                    cornerRadius = cornerRadius
                )

                // Current temperature indicator marker
                val indicatorX = size.width * animatedFraction
                val indicatorRadius = 7.dp.toPx()

                // Outer circle (halo)
                drawCircle(
                    color = onSurfaceColor,
                    radius = indicatorRadius + 2.dp.toPx(),
                    center = Offset(indicatorX, size.height / 2)
                )
                // Inner circle
                drawCircle(
                    color = primaryColor,
                    radius = indicatorRadius,
                    center = Offset(indicatorX, size.height / 2)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scale Reference Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Absolute Zero:\n-273.15 °C | 0 K",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    lineHeight = 13.sp
                )

                Text(
                    text = "Freezing:\n0 °C | 32 °F | 273.15 K",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    lineHeight = 13.sp
                )

                Text(
                    text = "Boiling:\n100 °C | 212 °F",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    lineHeight = 13.sp
                )
            }
        }
    }
}
