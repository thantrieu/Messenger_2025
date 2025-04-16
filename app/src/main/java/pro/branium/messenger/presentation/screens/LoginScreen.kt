package pro.branium.messenger.presentation.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import pro.branium.messenger.R
import pro.branium.messenger.presentation.theme.DarkGreen
import pro.branium.messenger.presentation.theme.GoogleBlue
import pro.branium.messenger.presentation.theme.LightGrey
import pro.branium.messenger.presentation.theme.TextGrey
import pro.branium.messenger.presentation.viewmodel.AuthViewModel

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean? = null,
    val errorMessage: Int? = null
)

data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isCorrect: Boolean = false
)

data class LoginFormInput(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false
)

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit = {},
    onGoogleLoginClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {}
) {
    var loginFormInput by remember { mutableStateOf(LoginFormInput()) }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginFormState by authViewModel.loginFormState.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var triggerLogin by remember { mutableStateOf(false) }

//    val context = LocalContext.current
    val focusManager = LocalFocusManager.current


    // Trigger actual login after form validation succeeds
    LaunchedEffect(loginFormState.isCorrect) {
        if (loginFormState.isCorrect && triggerLogin) {
            authViewModel.login(
                username = loginFormInput.email,
                password = loginFormInput.password,
                rememberMe = loginFormInput.rememberMe
            )
            triggerLogin = false // Reset trigger
        }
    }

    // Observe login success and trigger navigation/snackbar
    LaunchedEffect(loginState.isSuccess) {
        if (loginState.isSuccess == true) {
            onLoginSuccess()
        }
    }

    // Observe login error and show Snackbar
    val errorMessage = stringResource(loginState.errorMessage ?: R.string.message_login_failed)
    LaunchedEffect(errorMessage, loginState) {
        if (loginState.errorMessage != null) {
            snackbarHostState.showSnackbar(message = errorMessage)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = LightGrey) // Light gray background
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Logo in a header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.messenger1),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.title_login),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Email Field
                OutlinedTextField(
                    value = loginFormInput.email,
                    onValueChange = { loginFormInput = loginFormInput.copy(email = it) },
                    label = {
                        Text(stringResource(R.string.email_or_username), color = Color(0xFF333333))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = DarkGreen
                        )
                    },
                    placeholder = {
                        Text(
                            stringResource(R.string.email_placeholder),
                            color = TextGrey
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = loginFormState.usernameError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                FormFieldMessage(
                    message = loginFormState.usernameError?.let { stringResource(it) },
                    isError = loginFormState.usernameError != null
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Password Field
                OutlinedTextField(
                    value = loginFormInput.password,
                    onValueChange = { loginFormInput = loginFormInput.copy(password = it) },
                    label = {
                        Text(stringResource(R.string.label_password), color = Color(0xFF333333))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Icon",
                            tint = DarkGreen
                        )
                    },
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Filled.Visibility
                                } else {
                                    Icons.Filled.VisibilityOff
                                },
                                contentDescription = if (passwordVisible) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                },
                                tint = DarkGreen
                            )
                        }
                    },
                    placeholder = {
                        Text(
                            stringResource(R.string.password_placeholder),
                            color = TextGrey
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = loginFormState.passwordError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                FormFieldMessage(
                    message = loginFormState.passwordError?.let { stringResource(it) },
                    isError = loginFormState.passwordError != null
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Remember Me and Forgot Password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            loginFormInput =
                                loginFormInput.copy(rememberMe = !loginFormInput.rememberMe)
                        }
                    ) {
                        Checkbox(
                            checked = loginFormInput.rememberMe,
                            onCheckedChange = {
                                loginFormInput = loginFormInput.copy(rememberMe = it)
                            },
                            modifier = Modifier.size(24.dp),
                            colors = CheckboxDefaults.colors(
                                checkedColor = DarkGreen,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.remember_me), color = Color(0xFF333333))
                    }
                    Text(
                        text = stringResource(R.string.forgot_password),
                        color = DarkGreen, // Deep purple Color(0xFF6B46C1)
                        modifier = Modifier.clickable { onForgotPasswordClick() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = {
                        authViewModel.onLoginClicked(loginFormInput.email, loginFormInput.password)
                        triggerLogin = true // set trigger login after validation
                        // force close the keyboard
                        // approach 1: using InputMethodManager
//                    val imm =
//                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(
//                        (context as? Activity)?.currentFocus?.windowToken,
//                        0
//                    )
                        // approach 2: using LocalFocusManager to clear focus
                        focusManager.clearFocus()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !loginState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen) // Deep purple
                ) {
                    Text(text = stringResource(R.string.action_login), color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onGoogleLoginClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !loginState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black // Default text color for contrast
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = GoogleBlue
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.action_login_with_google),
                            color = GoogleBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Create Account
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCreateAccountClick() },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.label_dont_have_account),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.action_create_account),
                        color = DarkGreen
                    )
                }
            }
            if (loginState.isLoading) {
                ProcessingDialog()
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenPreview() {
    val authViewModel: AuthViewModel = hiltViewModel()
    LoginScreen(authViewModel = authViewModel)
}