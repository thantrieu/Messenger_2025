package pro.branium.messenger.presentation.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import pro.branium.messenger.R
import pro.branium.messenger.domain.model.enums.AccountType
import pro.branium.messenger.presentation.components.FormFieldMessage
import pro.branium.messenger.presentation.components.ProcessingDialog
import pro.branium.messenger.presentation.ui.theme.DarkGreen
import pro.branium.messenger.presentation.viewmodel.AuthViewModel

data class SignupFormInput(
    val displayName: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

data class SignupFormState(
    val input: SignupFormInput = SignupFormInput(),
    val displayNameError: Int? = null,
    val usernameError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val isCorrect: Boolean = false
)

data class SignupState(
    val isLoading: Boolean = false,
    val signupComplete: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: Int? = null
)

enum class FieldStatus {
    AVAILABLE, TAKEN, LOADING, IDLE
}

@Composable
fun SignupScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    var signupFormInput by remember { mutableStateOf(SignupFormInput()) }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    val signupFormState by authViewModel.signupFormState.collectAsState()
    val signupState by authViewModel.signupState.collectAsState()
    val usernameStatus by authViewModel.usernameStatus.collectAsState()
    val emailStatus by authViewModel.emailStatus.collectAsState()
    val pleaseCorrectError = stringResource(R.string.please_correct_error)

    var generalErrorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(signupState) {
        if (signupState.signupComplete) {
            navController.navigate("login") {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
                authViewModel.resetValidation()
                authViewModel.resetSignupState()
            }
        }
    }

    LaunchedEffect(usernameStatus, emailStatus, signupFormState.isCorrect) {
        if (signupFormState.isCorrect &&
            usernameStatus == FieldStatus.AVAILABLE &&
            emailStatus == FieldStatus.AVAILABLE
        ) {
            generalErrorMessage = null
            authViewModel.signUp(
                email = signupFormInput.email,
                password = signupFormInput.password,
                displayName = signupFormInput.displayName.trim(),
                username = signupFormInput.username,
                accountType = AccountType.FREE
            )
        } else if (usernameStatus == FieldStatus.TAKEN || emailStatus == FieldStatus.TAKEN) {
            generalErrorMessage = pleaseCorrectError
        } else {
            generalErrorMessage = null
        }
    }

    LaunchedEffect(signupFormInput.username) {
        if (signupFormInput.username.isNotEmpty()) {
            delay(500)
            authViewModel.checkUsername(signupFormInput.username)
            authViewModel.validateSignupForm(signupFormInput)
        } else if (usernameStatus != FieldStatus.IDLE) {
            authViewModel.resetUsernameStatus()
        }
    }

    LaunchedEffect(signupFormInput.email) {
        if (signupFormInput.email.isNotEmpty()) {
            delay(500)
            authViewModel.checkEmail(signupFormInput.email)
            authViewModel.validateSignupForm(signupFormInput)
        } else if (emailStatus != FieldStatus.IDLE) {
            authViewModel.resetEmailStatus()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
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

                Spacer(modifier = Modifier.height(16.dp))
                // Title
                Text(
                    text = stringResource(R.string.label_signup),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )

                // Username field
                OutlinedTextField(
                    value = signupFormInput.username,
                    onValueChange = {
                        signupFormInput = signupFormInput.copy(username = it.trim())
                    },
                    label = { Text(stringResource(R.string.username)) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = signupFormState.usernameError != null ||
                            usernameStatus == FieldStatus.TAKEN,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AlternateEmail,
                            contentDescription = null,
                            tint = DarkGreen
                        )
                    },
                    trailingIcon = {
                        if (signupFormInput.username.isNotEmpty()) {
                            IconButton(onClick = {
                                signupFormInput = signupFormInput.copy(username = "")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.clear_username),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            null
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = DarkGreen,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorTextColor = MaterialTheme.colorScheme.error
                    )
                )
                FormFieldMessage(
                    message = when {
                        signupFormState.usernameError != null ->
                            stringResource(signupFormState.usernameError!!)

                        signupFormInput.username.isNotEmpty() &&
                                usernameStatus == FieldStatus.TAKEN ->
                            stringResource(R.string.username_taken)

                        signupFormInput.username.isNotEmpty() &&
                                usernameStatus == FieldStatus.AVAILABLE ->
                            stringResource(R.string.username_available)

                        else -> null
                    },
                    isError = signupFormState.usernameError != null ||
                            usernameStatus == FieldStatus.TAKEN
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Display Name field
                OutlinedTextField(
                    value = signupFormInput.displayName,
                    onValueChange = { signupFormInput = signupFormInput.copy(displayName = it) },
                    label = { Text(stringResource(R.string.display_name)) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = signupFormState.displayNameError != null,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = DarkGreen
                        )
                    },
                    trailingIcon = {
                        if (signupFormInput.displayName.isNotEmpty()) {
                            IconButton(onClick = {
                                signupFormInput = signupFormInput.copy(displayName = "")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(
                                        R.string.desc_clear_display_name
                                    ),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            null
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = DarkGreen,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorTextColor = MaterialTheme.colorScheme.error
                    )
                )
                FormFieldMessage(
                    message = signupFormState.displayNameError?.let { stringResource(it) },
                    isError = signupFormState.displayNameError != null
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Email field
                OutlinedTextField(
                    value = signupFormInput.email,
                    onValueChange = { signupFormInput = signupFormInput.copy(email = it.trim()) },
                    label = { Text(stringResource(R.string.email)) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = signupFormState.emailError != null ||
                            emailStatus == FieldStatus.TAKEN,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = DarkGreen
                        )
                    },
                    trailingIcon = {
                        if (signupFormInput.email.isNotEmpty()) {
                            IconButton(onClick = {
                                signupFormInput = signupFormInput.copy(email = "")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.desc_clear_email),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            null
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = DarkGreen,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorTextColor = MaterialTheme.colorScheme.error
                    )
                )
                FormFieldMessage(
                    message = when {
                        signupFormState.emailError != null ->
                            stringResource(signupFormState.emailError!!)

                        signupFormInput.email.isNotEmpty() && emailStatus == FieldStatus.TAKEN ->
                            stringResource(
                                R.string.email_taken
                            )

                        signupFormInput.email.isNotEmpty() && emailStatus == FieldStatus.AVAILABLE ->
                            stringResource(
                                R.string.email_available
                            )

                        else -> null
                    },
                    isError = signupFormState.emailError != null || emailStatus == FieldStatus.TAKEN
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Password field
                OutlinedTextField(
                    value = signupFormInput.password,
                    onValueChange = {
                        signupFormInput = signupFormInput.copy(password = it.trim())
                    },
                    label = { Text(stringResource(R.string.password)) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = signupFormState.passwordError != null,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = DarkGreen
                        )
                    },
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = if (showConfirmPassword) {
                                    stringResource(R.string.desc_hide_password)
                                } else {
                                    stringResource(R.string.desc_show_password)
                                },
                                tint = DarkGreen
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = DarkGreen,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorTextColor = MaterialTheme.colorScheme.error
                    )
                )
                FormFieldMessage(
                    message = signupFormState.passwordError?.let { stringResource(it) },
                    isError = signupFormState.passwordError != null
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password field
                OutlinedTextField(
                    value = signupFormInput.confirmPassword,
                    onValueChange = {
                        signupFormInput = signupFormInput.copy(confirmPassword = it.trim())
                    },
                    label = { Text(stringResource(R.string.label_confirm_password)) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    isError = signupFormState.confirmPasswordError != null,
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = DarkGreen
                        )
                    },
                    visualTransformation = if (showConfirmPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = if (showConfirmPassword) {
                                    stringResource(R.string.desc_hide_password)
                                } else {
                                    stringResource(R.string.desc_show_password)
                                },
                                tint = DarkGreen
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = DarkGreen,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorTextColor = MaterialTheme.colorScheme.error
                    )
                )
                FormFieldMessage(
                    message = signupFormState.confirmPasswordError?.let { stringResource(it) },
                    isError = signupFormState.confirmPasswordError != null
                )

                // General Error message
                FormFieldMessage(message = generalErrorMessage, isError = true)

                Spacer(modifier = Modifier.height(24.dp))

                // Signup button
                Button(
                    onClick = { authViewModel.validateSignupForm(signupFormInput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text(text = stringResource(R.string.action_signup))
                }

                Spacer(modifier = Modifier.height(32.dp))
                // Navigate to login screen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("login") },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(R.string.message_already_have_an_account))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.action_login),
                        color = DarkGreen
                    )
                }
            }

            // Show CircularProgressIndicator as a dialog
            if (signupState.isLoading) {
                ProcessingDialog()
            }
        }
    }
}
