package pro.branium.messenger.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object Signup : Screen("signup")
    data object Login : Screen("login")
    data object ForgetPassword : Screen("forgetPassword")
    data object Searching : Screen("searching")
    data object Chat : Screen("chat/{userId}") {
        fun createRoute(userId: Int) = "chat/$userId"
    }
}