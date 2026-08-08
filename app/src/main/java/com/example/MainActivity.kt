package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.PixelBgCanvas
import com.example.ui.components.PixelBottomNavBar
import com.example.ui.screens.AICoachScreen
import com.example.ui.screens.AppListScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FocusSessionScreen
import com.example.ui.screens.PermissionsWizardScreen
import com.example.ui.screens.PointsDetailScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Guided Path", Icons.Default.Dashboard)
    object Focus : Screen("focus", "Focus", Icons.Default.Timer)
    object Apps : Screen("apps", "App Shield", Icons.Default.Apps)
    object Permissions : Screen("permissions", "Engine", Icons.Default.Shield)
    object AICoach : Screen("aicoach", "AI Coach", Icons.Default.AutoAwesome)
    object Quests : Screen("quests", "Quests & Points", Icons.Default.Star)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Attempt starting foreground monitoring service if permissions already granted
        viewModel.startForegroundServiceIfPermissionsGranted(this)

        setContent {
            MyApplicationTheme(darkTheme = true) {
                // Lifecycle observer to refresh permission status when returning from Settings
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            viewModel.checkPermissions()
                            viewModel.calculateTotalScreenTime()
                            viewModel.startForegroundServiceIfPermissionsGranted(this@MainActivity)
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    val navigationItems = listOf(
        Screen.Dashboard,
        Screen.Apps,
        Screen.Focus,
        Screen.Permissions,
        Screen.AICoach
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PixelBgCanvas,
        bottomBar = {
            PixelBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToApps = { navController.navigate(Screen.Apps.route) },
                    onNavigateToFocus = { navController.navigate(Screen.Focus.route) },
                    onNavigateToPermissions = { navController.navigate(Screen.Permissions.route) },
                    onNavigateToAICoach = { navController.navigate(Screen.AICoach.route) },
                    onNavigateToQuests = { navController.navigate(Screen.Quests.route) }
                )
            }

            composable(Screen.Quests.route) {
                PointsDetailScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Apps.route) {
                AppListScreen(viewModel = viewModel)
            }

            composable(Screen.Focus.route) {
                FocusSessionScreen(viewModel = viewModel)
            }

            composable(Screen.Permissions.route) {
                PermissionsWizardScreen(viewModel = viewModel)
            }

            composable(Screen.AICoach.route) {
                AICoachScreen(viewModel = viewModel)
            }
        }
    }
}
