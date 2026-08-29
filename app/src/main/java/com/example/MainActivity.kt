package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.collectAsState
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
import com.example.ui.screens.AppListScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FocusSessionScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PermissionsWizardScreen
import com.example.ui.screens.PointsDetailScreen
import com.example.ui.screens.ProfileSettingsScreen
import com.example.ui.theme.AlabasterSand
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateBlue
import com.example.ui.viewmodel.MainViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Guided Path", Icons.Default.Dashboard)
    object Focus : Screen("focus", "Prayer & Location", Icons.Default.Explore)
    object Apps : Screen("apps", "Apps you want to block", Icons.Default.Apps)
    object Permissions : Screen("permissions", "Engine", Icons.Default.Shield)
    object Leaderboard : Screen("leaderboard", "Leaderboard", Icons.Default.Star)
    object Quests : Screen("quests", "Quests & Points", Icons.Default.Star)
    object Onboarding : Screen("onboarding", "Diagnostic", Icons.Default.Shield)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

class MainActivity : FragmentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Enforce FLAG_SECURE to prevent screenshot scraping, screen recording & accessibility overlays
        com.example.utils.SecurityGuard.enforceFlagSecure(this)

        // Attempt starting foreground monitoring service if permissions already granted
        viewModel.startForegroundServiceIfPermissionsGranted(this)

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val scaffoldBg = if (isDarkMode) Color(0xFF080C14) else AlabasterSand
            MyApplicationTheme(darkTheme = isDarkMode) {
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

                MainAppContent(viewModel = viewModel, isDarkMode = isDarkMode, scaffoldBg = scaffoldBg)
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: MainViewModel,
    isDarkMode: Boolean = false,
    scaffoldBg: Color = AlabasterSand
) {
    val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

    if (!isOnboardingCompleted) {
        OnboardingScreen(
            viewModel = viewModel,
            onComplete = {
                // Onboarding complete transitions directly into Main Dashboard
            }
        )
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

    val navigationItems = listOf(
        Screen.Dashboard,
        Screen.Apps,
        Screen.Focus,
        Screen.Permissions,
        Screen.Leaderboard
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = scaffoldBg,
        contentColor = if (isDarkMode) Color(0xFFF8FAFC) else SlateBlue,
        bottomBar = {
            if (currentRoute != Screen.Onboarding.route && currentRoute != Screen.Profile.route) {
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                val context = androidx.compose.ui.platform.LocalContext.current
                val fragmentActivity = context as? FragmentActivity
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToApps = { navController.navigate(Screen.Apps.route) },
                    onNavigateToFocus = { navController.navigate(Screen.Focus.route) },
                    onNavigateToPermissions = { navController.navigate(Screen.Permissions.route) },
                    onNavigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) },
                    onNavigateToQuests = { navController.navigate(Screen.Quests.route) },
                    onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) },
                    onNavigateToProfile = {
                        if (fragmentActivity != null && com.example.utils.BiometricAuthHelper.isBiometricAvailable(fragmentActivity)) {
                            com.example.utils.BiometricAuthHelper.promptBiometricAuthentication(
                                activity = fragmentActivity,
                                title = "Unlock Settings & Profile",
                                subtitle = "Biometric authorization required to access settings and streak controls",
                                onSuccess = {
                                    navController.navigate(Screen.Profile.route)
                                },
                                onError = { err ->
                                    android.widget.Toast.makeText(context, "Biometric check: $err", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            navController.navigate(Screen.Profile.route)
                        }
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileSettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) },
                    onNavigateToApps = { navController.navigate(Screen.Apps.route) },
                    onNavigateToPermissionsWizard = { navController.navigate(Screen.Permissions.route) }
                )
            }

            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    viewModel = viewModel,
                    onComplete = { navController.popBackStack() }
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

            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(viewModel = viewModel)
            }
        }
    }
}
