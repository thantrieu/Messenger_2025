package pro.branium.messenger.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import pro.branium.messenger.presentation.screens.ForgotPasswordScreen
import pro.branium.messenger.presentation.screens.HomeScreen
import pro.branium.messenger.presentation.screens.LoginScreen
import pro.branium.messenger.presentation.screens.ProfileScreen
import pro.branium.messenger.presentation.screens.SearchingScreen
import pro.branium.messenger.presentation.screens.SignupScreen
import pro.branium.messenger.presentation.screens.SplashScreen
import pro.branium.messenger.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String
) {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            NavHost(navController = navController, startDestination = startDestination) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        authViewModel = authViewModel,
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        onNavigateToChat = {
                            navController.navigate(Screen.Chat.route)
                        },
                        onNavigateToProfile = {
                            navController.navigate(Screen.Profile.route)
                        },
                        onNavigateToSettings = {
                            // navController.navigate(Screen.Settings.route)
                        }
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(authViewModel, navController)
                }
                composable(Screen.Chat.route) {
                    // ChatScreen() { }
                }
                composable(Screen.Searching.route) {
                    SearchingScreen()
                }
                composable(Screen.Login.route) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onGoogleLoginClick = {
                            authViewModel.startGoogleSignIn()
                        },
                        onForgotPasswordClick = {
                            navController.navigate(Screen.ForgetPassword.route)
                        },
                        onCreateAccountClick = {
                            navController.navigate(Screen.Signup.route)
                        }
                    )
                }
                composable(Screen.Signup.route) {
                    SignupScreen()
                }
                composable(Screen.ForgetPassword.route) {
                    ForgotPasswordScreen()
                }
            }
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val isLoggedIn = withContext(Dispatchers.IO) {
            authViewModel.isUserLoggedIn()
        }
        delay(2000) // delay for 2 second = 2000 milliseconds
        startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
    }
    if (startDestination == null) {
        SplashScreen()
    } else {
        Navigation(
            navController = navController,
            authViewModel = authViewModel,
            startDestination = startDestination!!
        )
    }
}