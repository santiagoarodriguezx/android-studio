package com.example.myapplication.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object TwoFactor : Screen("two_factor/{email}") {
        fun createRoute(email: String) = "two_factor/$email"
    }
    object DeviceVerification : Screen("device_verification/{email}/{password}/{token}") {
        fun createRoute(email: String, password: String, token: String) =
            "device_verification/$email/$password/$token"
    }
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object MessageLogs : Screen("message_logs")
    object AnalyticsDashboard : Screen("analytics_dashboard")
    object ProductsDashboard : Screen("products_dashboard")
    object ScheduledMessages : Screen("scheduled_messages")
    object CreateScheduledMessage : Screen("create_scheduled_message")
    object ScheduledMessageDetail : Screen("scheduled_message_detail/{messageId}") {
        fun createRoute(messageId: String) = "scheduled_message_detail/$messageId"
    }
    object Agents : Screen("agents")
}

