package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.*
import com.example.myapplication.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: AuthViewModel,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    // 🔧 Fix: Use remember to keep startDestination stable after initial composition
    // This prevents NavHost from resetting during logout navigation
    val startDestination = remember { 
        if (isLoggedIn) Screen.Home.route else Screen.Login.route 
    }

    // 🔒 Handle authentication state changes
    LaunchedEffect(isLoggedIn) {
        val currentRoute = navController.currentDestination?.route
        
        when {
            // User logged out - navigate to Login
            !isLoggedIn && currentRoute != null && currentRoute != Screen.Login.route -> {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            // User logged in while on Login screen - navigate to Home
            isLoggedIn && currentRoute == Screen.Login.route -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onNavigateTo2FA = { email ->
                    navController.navigate(Screen.TwoFactor.createRoute(email))
                },
                onNavigateToDeviceVerification = { email, password, token ->
                    navController.navigate(
                        Screen.DeviceVerification.createRoute(email, password, token)
                    )
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.TwoFactor.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            TwoFactorScreen(
                viewModel = viewModel,
                email = email,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onVerificationSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.DeviceVerification.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("password") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val password = backStackEntry.arguments?.getString("password") ?: ""
            val token = backStackEntry.arguments?.getString("token") ?: ""

            DeviceVerificationScreen(
                viewModel = viewModel,
                email = email,
                password = password,
                verificationToken = token,
                onVerificationSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla principal con barra de navegación inferior
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToAnalytics = {
                    navController.navigate(Screen.AnalyticsDashboard.route)
                },
                onNavigateToMessageLogs = {
                    navController.navigate(Screen.MessageLogs.route)
                },
                onNavigateToScheduledMessages = {
                    navController.navigate(Screen.ScheduledMessages.route)
                },
                onNavigateToProducts = {
                    navController.navigate(Screen.ProductsDashboard.route)
                },
                onNavigateToAgents = {
                    navController.navigate(Screen.Agents.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Perfil
        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSecurity = {
                    // TODO: Crear pantalla de seguridad
                },
                onNavigateToDevices = {
                    // TODO: Crear pantalla de dispositivos
                },
                onNavigateToLoginHistory = {
                    // TODO: Crear pantalla de historial
                },
                onLogout = {
                    // Only call logout - navigation is handled by LaunchedEffect
                    viewModel.logout()
                }
            )
        }

        // Configuración
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                isDarkMode = isDarkMode,
                onThemeChange = onThemeChange
            )
        }

        composable(Screen.AnalyticsDashboard.route) {
            AnalyticsDashboardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.MessageLogs.route) {
            MessageLogsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ProductsDashboard.route) {
            ProductsDashboardScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Agentes de IA
        composable(Screen.Agents.route) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val agentsViewModel = remember {
                com.example.myapplication.viewmodel.AgentsViewModel(context)
            }

            AgentsScreen(
                viewModel = agentsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Mensajes Programados
        composable(Screen.ScheduledMessages.route) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val repository = com.example.myapplication.data.repository.ScheduledMessagesRepository(context)
            val scheduledMessagesViewModel = remember {
                com.example.myapplication.viewmodel.ScheduledMessagesViewModel(repository)
            }

            ScheduledMessagesScreen(
                viewModel = scheduledMessagesViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateScheduledMessage.route)
                },
                onNavigateToDetail = { messageId ->
                    navController.navigate(Screen.ScheduledMessageDetail.createRoute(messageId))
                }
            )
        }

        composable(Screen.CreateScheduledMessage.route) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val repository = com.example.myapplication.data.repository.ScheduledMessagesRepository(context)
            val scheduledMessagesViewModel = remember {
                com.example.myapplication.viewmodel.ScheduledMessagesViewModel(repository)
            }

            CreateScheduledMessageScreen(
                viewModel = scheduledMessagesViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ScheduledMessageDetail.route,
            arguments = listOf(navArgument("messageId") { type = NavType.StringType })
        ) { backStackEntry ->
            val messageId = backStackEntry.arguments?.getString("messageId") ?: ""
            val context = androidx.compose.ui.platform.LocalContext.current
            val repository = com.example.myapplication.data.repository.ScheduledMessagesRepository(context)
            val scheduledMessagesViewModel = remember {
                com.example.myapplication.viewmodel.ScheduledMessagesViewModel(repository)
            }

            ScheduledMessageDetailScreen(
                messageId = messageId,
                viewModel = scheduledMessagesViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

