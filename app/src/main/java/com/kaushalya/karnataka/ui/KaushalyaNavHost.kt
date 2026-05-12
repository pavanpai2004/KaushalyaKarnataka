package com.kaushalya.karnataka.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kaushalya.karnataka.ui.auth.ForgotPasswordScreen
import com.kaushalya.karnataka.ui.auth.LoginScreen
import com.kaushalya.karnataka.ui.auth.SignupScreen
import com.kaushalya.karnataka.ui.help.HelpScreen
import com.kaushalya.karnataka.ui.home.HomeScreen
import com.kaushalya.karnataka.ui.notifications.NotificationsScreen
import com.kaushalya.karnataka.ui.profile.ProfileTab
import com.kaushalya.karnataka.ui.profile.WorkerProfileScreen
import com.kaushalya.karnataka.ui.requests.RequestsScreen
import com.kaushalya.karnataka.ui.review.ReviewScreen
import com.kaushalya.karnataka.ui.search.SearchScreen
import com.kaushalya.karnataka.ui.settings.SettingsScreen
import com.kaushalya.karnataka.ui.worker.WorkerDashboardScreen
import com.kaushalya.karnataka.viewmodel.AuthViewModel

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaushalyaNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home.route, "Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem(Screen.Search.route, "Search", Icons.Filled.Search, Icons.Outlined.Search),
        BottomNavItem(Screen.Requests.route, "Requests", Icons.Filled.Assignment, Icons.Outlined.Assignment),
        BottomNavItem(Screen.Profile.route, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    val startDestination = if (authViewModel.isLoggedIn) Screen.Home.route else Screen.Login.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = if (showBottomBar) Modifier.padding(innerPadding) else Modifier
        ) {
            // Auth screens
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToSignup = { navController.navigate(Screen.Signup.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) },
                    onBrowseAsGuest = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Signup.route) {
                SignupScreen(
                    onSignupSuccess = { userId ->
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ForgotPassword.route) {
                ForgotPasswordScreen(onBack = { navController.popBackStack() })
            }

            // Main tabs
            composable(Screen.Home.route) {
                HomeScreen(
                    onWorkerClick = { navController.navigate(Screen.WorkerProfile.createRoute(it)) },
                    onHireClick = { navController.navigate(Screen.WorkerProfile.createRoute(it)) },
                    onCategoryClick = { navController.navigate(Screen.CategoryView.createRoute(it)) },
                    onSearchClick = { navController.navigate(Screen.Search.route) }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onWorkerClick = { navController.navigate(Screen.WorkerProfile.createRoute(it)) },
                    onHireClick = { navController.navigate(Screen.WorkerProfile.createRoute(it)) }
                )
            }

            composable(
                Screen.CategoryView.route,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                SearchScreen(
                    onWorkerClick = { navController.navigate(Screen.WorkerProfile.createRoute(it)) },
                    onHireClick = { navController.navigate(Screen.WorkerProfile.createRoute(it)) },
                    initialCategory = category
                )
            }

            composable(Screen.Requests.route) {
                RequestsScreen(
                    onRequestClick = { /* detail */ }
                )
            }

            composable(Screen.Profile.route) {
                ProfileTab(
                    isLoggedIn = authViewModel.isLoggedIn,
                    userId = authViewModel.currentUserId,
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToHelp = { navController.navigate(Screen.Help.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToDashboard = { navController.navigate(Screen.WorkerDashboard.createRoute(it)) },
                    onLogout = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Detail screens
            composable(
                Screen.WorkerProfile.route,
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: return@composable
                WorkerProfileScreen(
                    workerId = workerId,
                    onBack = { navController.popBackStack() },
                    onWriteReview = { navController.navigate(Screen.ReviewScreen.createRoute(it)) }
                )
            }

            composable(
                Screen.WorkerDashboard.route,
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: return@composable
                WorkerDashboardScreen(
                    workerId = workerId,
                    onBack = { navController.popBackStack() },
                    onViewPublicProfile = { navController.navigate(Screen.WorkerProfile.createRoute(it)) }
                )
            }

            composable(
                Screen.ReviewScreen.route,
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: return@composable
                ReviewScreen(
                    workerId = workerId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Notifications.route) {
                NotificationsScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                    },
                    onHelp = { navController.navigate(Screen.Help.route) },
                    isLoggedIn = authViewModel.isLoggedIn,
                    userName = ""
                )
            }

            composable(Screen.Help.route) {
                HelpScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
