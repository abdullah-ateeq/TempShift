package com.example.tempshift.ui.settings

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempshift.data.model.ThemeMode
import com.example.tempshift.ui.TempShiftUiState
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: TempShiftUiState,
    onThemeChange: (ThemeMode) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onHapticChange: (Boolean) -> Unit,
    onAnimationChange: (Boolean) -> Unit,
    onDecimalPlacesChange: (Int) -> Unit,
    onRoundingModeChange: (RoundingMode) -> Unit,
    onShowFormulaChange: (Boolean) -> Unit,
    onShowTemperatureScaleChange: (Boolean) -> Unit,
    onConfirmHistoryDeleteChange: (Boolean) -> Unit,
    onPreserveLastConversionChange: (Boolean) -> Unit,
    onClearHistory: () -> Unit,
    onResetSettings: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }
    var showRoundingModeDialog by remember { mutableStateOf(false) }

    fun triggerHaptic() {
        if (uiState.hapticFeedback) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // APPEARANCE CARD
        SettingsSectionCard(title = "APPEARANCE") {
            Text(
                text = "Theme",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val themeOptions = listOf(ThemeMode.SYSTEM to "System", ThemeMode.LIGHT to "Light", ThemeMode.DARK to "Dark")
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                themeOptions.forEachIndexed { index, (mode, label) ->
                    SegmentedButton(
                        selected = uiState.themeMode == mode,
                        onClick = {
                            triggerHaptic()
                            onThemeChange(mode)
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = themeOptions.size)
                    ) {
                        Text(text = label, fontSize = 12.sp)
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Spacer(modifier = Modifier.height(14.dp))
                SettingsSwitchRow(
                    title = "Dynamic Color",
                    subtitle = "Use colors matching your system wallpaper",
                    checked = uiState.dynamicColor,
                    onCheckedChange = {
                        triggerHaptic()
                        onDynamicColorChange(it)
                    }
                )
            }
        }

        // BEHAVIOR CARD
        SettingsSectionCard(title = "BEHAVIOR") {
            SettingsSwitchRow(
                title = "Haptic Feedback",
                subtitle = "Vibrate on interactions and adjustments",
                checked = uiState.hapticFeedback,
                onCheckedChange = {
                    triggerHaptic()
                    onHapticChange(it)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSwitchRow(
                title = "Animations",
                subtitle = "Smooth visual transitions between units and values",
                checked = uiState.animations,
                onCheckedChange = {
                    triggerHaptic()
                    onAnimationChange(it)
                }
            )
        }

        // NUMBER DISPLAY CARD
        SettingsSectionCard(title = "NUMBER DISPLAY") {
            Text(
                text = "Decimal Places",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val decimalOptions = listOf(0, 1, 2, 4, 6, 10)
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                decimalOptions.forEachIndexed { index, places ->
                    SegmentedButton(
                        selected = uiState.decimalPlaces == places,
                        onClick = {
                            triggerHaptic()
                            onDecimalPlacesChange(places)
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = decimalOptions.size)
                    ) {
                        Text(text = places.toString(), fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        triggerHaptic()
                        showRoundingModeDialog = true
                    }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Rounding Mode",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = uiState.roundingMode.name.replace("_", " "),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                TextButton(onClick = {
                    triggerHaptic()
                    showRoundingModeDialog = true
                }) {
                    Text(text = "Change")
                }
            }
        }

        // TEMPERATURE FEATURES CARD
        SettingsSectionCard(title = "TEMPERATURE") {
            SettingsSwitchRow(
                title = "Show Conversion Formula",
                subtitle = "Display step-by-step arithmetic on Home screen",
                checked = uiState.showFormula,
                onCheckedChange = {
                    triggerHaptic()
                    onShowFormulaChange(it)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSwitchRow(
                title = "Show Temperature Scale",
                subtitle = "Display visual relative indicator on Home screen",
                checked = uiState.showTemperatureScale,
                onCheckedChange = {
                    triggerHaptic()
                    onShowTemperatureScaleChange(it)
                }
            )
        }

        // HISTORY SETTINGS CARD
        SettingsSectionCard(title = "HISTORY & PERSISTENCE") {
            SettingsSwitchRow(
                title = "Confirm History Delete",
                subtitle = "Ask before deleting individual items",
                checked = uiState.confirmHistoryDelete,
                onCheckedChange = {
                    triggerHaptic()
                    onConfirmHistoryDeleteChange(it)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsSwitchRow(
                title = "Preserve Last Conversion",
                subtitle = "Restore your previous inputs upon relaunch",
                checked = uiState.preserveLastConversion,
                onCheckedChange = {
                    triggerHaptic()
                    onPreserveLastConversionChange(it)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        triggerHaptic()
                        showClearHistoryDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(text = "Clear History", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        triggerHaptic()
                        showResetSettingsDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Text(text = "Reset Settings", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Rounding Mode Dialog
    if (showRoundingModeDialog) {
        val roundingModes = listOf(
            RoundingMode.HALF_UP to "Half Up (Default standard rounding)",
            RoundingMode.HALF_DOWN to "Half Down",
            RoundingMode.HALF_EVEN to "Half Even (Banker's rounding)",
            RoundingMode.UP to "Up (Away from zero)",
            RoundingMode.DOWN to "Down (Towards zero)",
            RoundingMode.CEILING to "Ceiling (Towards positive infinity)",
            RoundingMode.FLOOR to "Floor (Towards negative infinity)"
        )

        AlertDialog(
            onDismissRequest = { showRoundingModeDialog = false },
            title = { Text(text = "Display Rounding Mode") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Affects display formatting only. Internal conversion remains authoritative.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    roundingModes.forEach { (mode, description) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    triggerHaptic()
                                    onRoundingModeChange(mode)
                                    showRoundingModeDialog = false
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.roundingMode == mode,
                                onClick = {
                                    triggerHaptic()
                                    onRoundingModeChange(mode)
                                    showRoundingModeDialog = false
                                }
                            )
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    text = mode.name.replace("_", " "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoundingModeDialog = false }) {
                    Text(text = "Close")
                }
            }
        )
    }

    // Clear History Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text(text = "Clear History") },
            text = { Text(text = "Clear all recent temperature conversions?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text(text = "Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    // Reset Settings Dialog
    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text(text = "Reset Settings") },
            text = { Text(text = "Reset all application settings to their default values?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onResetSettings()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text(text = "Reset", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text(text = "Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.semantics { contentDescription = title }
        )
    }
}
