package com.example.tempshift

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.tempshift.data.repository.SettingsRepository
import com.example.tempshift.data.repository.TempShiftHistoryRepository
import com.example.tempshift.ui.TempShiftViewModel
import com.example.tempshift.ui.navigation.TempShiftMainApp
import com.example.tempshift.ui.theme.TempShiftTheme

class MainActivity : ComponentActivity() {

    private val settingsRepository by lazy { SettingsRepository(this) }
    private val historyRepository by lazy { TempShiftHistoryRepository(this) }

    private val viewModel: TempShiftViewModel by viewModels {
        TempShiftViewModel.provideFactory(settingsRepository, historyRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            TempShiftTheme(
                themeMode = uiState.themeMode,
                dynamicColor = uiState.dynamicColor
            ) {
                TempShiftMainApp(
                    viewModel = viewModel,
                    uiState = uiState
                )
            }
        }
    }
}
