package com.kaushalya.karnataka.ui

sealed class Screen(val route: String) {
    object Landing : Screen("landing")
    object Login : Screen("auth/login")
    object Signup : Screen("auth/signup")
    object ForgotPassword : Screen("auth/forgot-password")
    object Home : Screen("home")
    object Search : Screen("search")
    object Requests : Screen("requests")
    object Profile : Screen("profile")
    object WorkerProfile : Screen("worker_profile/{workerId}") {
        fun createRoute(workerId: String) = "worker_profile/$workerId"
    }
    object WorkerDashboard : Screen("worker_dashboard/{workerId}") {
        fun createRoute(workerId: String) = "worker_dashboard/$workerId"
    }
    object CustomerDashboard : Screen("customer_dashboard")
    object AddEditWorker : Screen("add_edit_worker?workerId={workerId}") {
        fun createRoute(workerId: String? = null) =
            if (workerId != null) "add_edit_worker?workerId=$workerId"
            else "add_edit_worker?workerId="
    }
    object CreateService : Screen("services/create/{workerId}") {
        fun createRoute(workerId: String) = "services/create/$workerId"
    }
    object ReviewScreen : Screen("review/{workerId}") {
        fun createRoute(workerId: String) = "review/$workerId"
    }
    object RequestDetail : Screen("requests/{requestId}") {
        fun createRoute(requestId: String) = "requests/$requestId"
    }
    object Notifications : Screen("notifications")
    object Settings : Screen("profile/settings")
    object Help : Screen("help")
    object CategoryView : Screen("categories/{category}") {
        fun createRoute(category: String) = "categories/$category"
    }
}
