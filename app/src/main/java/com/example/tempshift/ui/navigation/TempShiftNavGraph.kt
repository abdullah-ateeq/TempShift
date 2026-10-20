package com.example.tempshift.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tempshift.ui.TempShiftUiState
import com.example.tempshift.ui.TempShiftViewModel
import com.example.tempshift.ui.about.AboutScreen
import com.example.tempshift.ui.history.HistoryScreen
import com.example.tempshift.ui.home.HomeScreen
import com.example.tempshift.ui.settings.SettingsScreen
import com.example.tempshift.ui.splash.SplashScreen

@Composable
fun TempShiftMainApp(
    viewModel: TempShiftViewModel,
    uiState: TempShiftUiState
) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current

    // Display snackbar when ViewModel emits a snackbar message
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onDismissSnackbar()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute != Screen.Splash.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp
                ) {
                    Screen.bottomNavScreens.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (uiState.hapticFeedback) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                }
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                    contentDescription = screen.title
                                )
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.semantics {
                                contentDescription = "${screen.title} tab"
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    onInputChange = viewModel::onInputChange,
                    onFromUnitChange = viewModel::onFromUnitChange,
                    onToUnitChange = viewModel::onToUnitChange,
                    onSwapUnits = viewModel::onSwapUnits,
                    onQuickSelected = viewModel::onQuickValueSelected,
                    onSaveToHistory = viewModel::onSaveToHistory,
                    onReset = viewModel::onReset
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    uiState = uiState,
                    onRestoreHistory = viewModel::onRestoreHistory,
                    onDeleteHistoryItem = viewModel::onDeleteHistoryItem,
                    onClearHistory = viewModel::onClearHistory,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    uiState = uiState,
                    onThemeChange = viewModel::onThemeChange,
                    onDynamicColorChange = viewModel::onDynamicColorChange,
                    onHapticChange = viewModel::onHapticChange,
                    onAnimationChange = viewModel::onAnimationsChange,
                    onDecimalPlacesChange = viewModel::onDecimalPlacesChange,
                    onRoundingModeChange = viewModel::onRoundingModeChange,
                    onShowFormulaChange = viewModel::onShowFormulaChange,
                    onShowTemperatureScaleChange = viewModel::onShowTemperatureScaleChange,
                    onConfirmHistoryDeleteChange = viewModel::onConfirmHistoryDeleteChange,
                    onPreserveLastConversionChange = viewModel::onPreserveLastConversionChange,
                    onClearHistory = viewModel::onClearHistory,
                    onResetSettings = viewModel::onResetSettings
                )
            }

            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}
