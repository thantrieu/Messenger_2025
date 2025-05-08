package pro.branium.messenger.presentation.viewmodel

import androidx.compose.ui.res.stringResource
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pro.branium.messenger.R
import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.model.enums.AccountType
import pro.branium.messenger.domain.model.error.SignupError
import pro.branium.messenger.domain.usecase.CheckEmailUseCase
import pro.branium.messenger.domain.usecase.CheckUsernameUseCase
import pro.branium.messenger.domain.usecase.ForgotPasswordUseCase
import pro.branium.messenger.domain.usecase.GetProfileUseCase
import pro.branium.messenger.domain.usecase.LoginUseCase
import pro.branium.messenger.domain.usecase.LogoutUseCase
import pro.branium.messenger.domain.usecase.ResetPasswordUseCase
import pro.branium.messenger.domain.usecase.SignupUseCase
import pro.branium.messenger.presentation.screens.FieldStatus
import pro.branium.messenger.presentation.screens.LoginFormState
import pro.branium.messenger.presentation.screens.LoginState
import pro.branium.messenger.presentation.screens.SignupFormInput
import pro.branium.messenger.presentation.screens.SignupFormState
import pro.branium.messenger.presentation.screens.SignupState
import pro.branium.messenger.utils.Result
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val signupUseCase: SignupUseCase,
    private val checkUsernameUseCase: CheckUsernameUseCase,
    private val checkEmailUseCase: CheckEmailUseCase
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    private val _account = MutableStateFlow<Account?>(null)
    private val _startGoogleSignIn = MutableStateFlow(false)
    private val _lastError = MutableStateFlow<String?>(null)
    private val _loginFormState = MutableStateFlow(LoginFormState())
    private val _loginState = MutableStateFlow(LoginState())
    private val _signupState = MutableStateFlow(SignupState())
    private val _signupFormState = MutableStateFlow(SignupFormState())
    private val _usernameStatus = MutableStateFlow<FieldStatus?>(null)
    private val _emailStatus = MutableStateFlow<FieldStatus?>(null)

    val isLoggedIn: StateFlow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is Exception) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_LOGGED_IN_KEY] == true
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    val account: StateFlow<Account?> = _account
    val startGoogleSignIn: StateFlow<Boolean> = _startGoogleSignIn
    val lastError: StateFlow<String?> = _lastError.asStateFlow()
    val loginFormState: StateFlow<LoginFormState> = _loginFormState.asStateFlow()
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    val signupState: StateFlow<SignupState> = _signupState.asStateFlow()
    val signupFormState: StateFlow<SignupFormState> = _signupFormState.asStateFlow()
    val usernameStatus: StateFlow<FieldStatus?> = _usernameStatus.asStateFlow()
    val emailStatus: StateFlow<FieldStatus?> = _emailStatus.asStateFlow()

    suspend fun isUserLoggedIn(): Boolean {
        val isLoggedIn = dataStore.data.first()[IS_LOGGED_IN_KEY] == true
        loadLoggedInUser()
        return isLoggedIn
    }

    private fun loadLoggedInUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val username = dataStore.data.first()[USERNAME_KEY]
            if (!username.isNullOrEmpty()) {
                val result = getProfileUseCase.execute(username)
//                _account.value = result
            } else {
                _account.value = null
            }
        }
    }

    fun onLoginClicked(username: String, password: String) {
        val usernameError = if (username.length < 3) R.string.error_username else null
        val passwordError = if (password.length < 6) R.string.error_password else null
        val isCorrect = usernameError == null && passwordError == null

        _loginFormState.value = LoginFormState(
            usernameError = usernameError,
            passwordError = passwordError,
            isCorrect = isCorrect
        )
    }

    fun login(username: String, password: String, rememberMe: Boolean = false) {
        _loginState.value = LoginState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val loginResult = loginUseCase.execute(username, password, rememberMe)
            when (loginResult) {
                is Result.Success -> {
                    val accountIdentity = loginResult.value
                    _loginState.value = LoginState(
                        isLoading = false,
                        error = null,
                        loggedInUser = accountIdentity,
                        loginComplete = true
                    )
                }

                is Result.Failure -> {
                    val loginError = loginResult.error
                    val errorMessage = loginError.message ?: "An unknown login error occurred."
                    _loginState.value = LoginState(
                        isLoading = false,
                        error = errorMessage,
                        loggedInUser = null,
                        loginComplete = false
                    )
                }
            }
            _loginFormState.value = LoginFormState()
        }
    }

    fun startGoogleSignIn() {
        viewModelScope.launch {
            _startGoogleSignIn.value = true
        }
    }

    fun onGoogleSignInStarted() {
        viewModelScope.launch {
            _startGoogleSignIn.value = false
        }
    }

    fun onGoogleSignInResult(idToken: String?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!idToken.isNullOrEmpty()) {
                    // todo
                    onSuccess()
                    _lastError.value = null
                } else {
                    throw Exception("Invalid Google ID")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Google login failed")
                _lastError.value = e.message
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            account.value?.let {
                logoutUseCase.execute()
                _isLoggedIn.value = false
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            forgotPasswordUseCase.execute(email)
        }
    }

    fun resetPassword(newPassword: String, confirmPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (newPassword == confirmPassword) {
                account.value?.let {
                    it.passwordHash = newPassword
//                    resetPasswordUseCase.execute(it)
                }
            }
        }
    }

    fun checkUsername(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _usernameStatus.value = FieldStatus.LOADING
            val isTaken = checkUsernameUseCase.execute(username)
            _usernameStatus.value = if (isTaken) FieldStatus.TAKEN else FieldStatus.AVAILABLE
        }
    }

    fun resetUsernameStatus() {
        _usernameStatus.value = null
    }

    fun checkEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _emailStatus.value = FieldStatus.LOADING
            val isTaken = checkEmailUseCase.execute(email)
            _emailStatus.value = if (isTaken) FieldStatus.TAKEN else FieldStatus.AVAILABLE
        }
    }

    fun resetEmailStatus() {
        _emailStatus.value = null
    }

    fun validateSignupForm(signupFormInput: SignupFormInput) {
        val displayNameError =
            if (signupFormInput.displayName.length < 3) R.string.error_display_name else null
        val usernameError =
            if (signupFormInput.username.length < 3) R.string.error_username else null
        val emailError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(signupFormInput.email)
                .matches() or signupFormInput.email.isEmpty()
        ) R.string.error_email else null
        val passwordError =
            if (signupFormInput.password.length < 6) R.string.error_password else null
        val confirmPasswordError =
            if (signupFormInput.password != signupFormInput.confirmPassword ||
                signupFormInput.confirmPassword.isEmpty()
            ) {
                R.string.error_confirm_password
            } else {
                null
            }
        val isCorrect = displayNameError == null && usernameError == null
                && emailError == null && passwordError == null
                && confirmPasswordError == null

        _signupFormState.value = SignupFormState(
            displayNameError = displayNameError,
            usernameError = usernameError,
            emailError = emailError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            isCorrect = isCorrect
        )
    }

    fun resetValidation() {
        _signupFormState.value = SignupFormState()
    }

    fun resetSignupState() {
        _signupState.value = SignupState()
    }

    fun signUp(
        email: String,
        password: String,
        displayName: String,
        username: String,
        accountType: AccountType
    ) {
        _signupState.value = SignupState(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val signupResult: Result<SignupError, String> = signupUseCase.execute(
                email = email,
                password = password,
                displayName = displayName,
                username = username,
                accountType = accountType
            )
            when (signupResult) {
                is Result.Success -> {
                    _signupState.value = SignupState(
                        isLoading = false,
                        signupComplete = true,
                        successMessage = R.string.signup_success,
                        errorMessage = null
                    )
                }

                is Result.Failure -> {
                    val signupError = signupResult.error
                    val errorMessage = signupError.message ?: "An unknown signup error occurred."
                    _signupState.value = SignupState(
                        isLoading = false,
                        signupComplete = false,
                        successMessage = null,
                        errorMessage = errorMessage
                    )
                }
            }
        }
    }

    fun clearLoginStatus() {
        _loginState.value = _loginState.value.copy(
            loginComplete = false,
            error = null
        )
    }

    fun cleanError() {
        viewModelScope.launch {
            _lastError.value = null
        }
    }

    class Factory @Inject constructor(
        private val dataStore: DataStore<Preferences>,
        private val loginUseCase: LoginUseCase,
        private val logoutUseCase: LogoutUseCase,
        private val getProfileUseCase: GetProfileUseCase,
        private val resetPasswordUseCase: ResetPasswordUseCase,
        private val forgotPasswordUseCase: ForgotPasswordUseCase,
        private val signupUseCase: SignupUseCase,
        private val checkUsernameUseCase: CheckUsernameUseCase,
        private val checkEmailUseCase: CheckEmailUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(
                    dataStore,
                    loginUseCase,
                    logoutUseCase,
                    getProfileUseCase,
                    resetPasswordUseCase,
                    forgotPasswordUseCase,
                    signupUseCase,
                    checkUsernameUseCase,
                    checkEmailUseCase
                ) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    companion object {
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        private val USERNAME_KEY = stringPreferencesKey("username")
    }
}