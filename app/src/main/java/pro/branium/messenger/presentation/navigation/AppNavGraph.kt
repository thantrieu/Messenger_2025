package pro.branium.messenger.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pro.branium.messenger.presentation.screens.ForgotPasswordScreen
import pro.branium.messenger.presentation.screens.HomeScreen
import pro.branium.messenger.presentation.screens.LoginScreen
import pro.branium.messenger.presentation.screens.ProfileScreen
import pro.branium.messenger.presentation.screens.SearchingScreen
import pro.branium.messenger.presentation.screens.SignupScreen
import pro.branium.messenger.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val isLoggedInState by authViewModel.isLoggedIn.collectAsState()
    val startDestination = if (isLoggedInState) Screen.Home.route else Screen.Login.route
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            NavHost(navController = navController, startDestination = startDestination) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onLogout = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Home.route) {
                                    inclusive = true
                                }
                            }
                        },
                        onNavigateToChat = {
                            navController.navigate(Screen.Chat.route)
                        }
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen()
                }
                composable(Screen.Chat.route) {
                    // ChatScreen() { }
                }
                composable(Screen.Searching.route) {
                    SearchingScreen()
                }
                composable(Screen.Login.route) {
                    LoginScreen(
                        onLoginClick = {
                            authViewModel.logout()
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) {
                                    inclusive = true
                                }
                            }
                        },
                        onGoogleLoginClick = {
                            authViewModel.loginWithGoogle()
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) {
                                    inclusive = true
                                }
                            }
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
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    Navigation(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        authViewModel = authViewModel
    )
}