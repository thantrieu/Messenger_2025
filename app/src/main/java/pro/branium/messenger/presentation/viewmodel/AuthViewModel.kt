package pro.branium.messenger.presentation.viewmodel

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
import pro.branium.messenger.domain.usecase.GetUserUseCase
import pro.branium.messenger.domain.usecase.LoginUseCase
import pro.branium.messenger.domain.usecase.LogoutUseCase
import pro.branium.messenger.presentation.screens.LoginFormState
import pro.branium.messenger.presentation.screens.LoginState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    private val _account = MutableStateFlow<Account?>(null)
    private val _startGoogleSignIn = MutableStateFlow(false)
    private val _lastError = MutableStateFlow<String?>(null)
    private val _loginFormState = MutableStateFlow(LoginFormState())
    private val _loginState = MutableStateFlow(LoginState())

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

    suspend fun isUserLoggedIn(): Boolean {
        return dataStore.data.first()[IS_LOGGED_IN_KEY] == true
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
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = LoginState(isLoading = true)
            val result = loginUseCase.execute(Account(username = username, password = password))
            _account.value = result
            _isLoggedIn.value = result != null
            if (_isLoggedIn.value) {
                if(rememberMe) {
                    saveLoginStatus()
                }
                _loginState.value = LoginState(isSuccess = true)
            } else {
                _loginState.value =
                    LoginState(isSuccess = false, errorMessage = R.string.login_error)
            }
            _loginFormState.value = LoginFormState()
        }
    }

    private fun saveLoginStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.edit { preferences ->
                preferences[IS_LOGGED_IN_KEY] = _isLoggedIn.value
                preferences[USERNAME_KEY] = _account.value?.username ?: ""
            }
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
                    saveLoginStatus()
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
                logoutUseCase.execute(it)
                _isLoggedIn.value = false
                saveLoginStatus()
            }
        }
    }

    fun cleanError() {
        viewModelScope.launch {
            _lastError.value = null
        }
    }

    fun getUser() {

    }

    class Factory @Inject constructor(
        private val dataStore: DataStore<Preferences>,
        private val loginUseCase: LoginUseCase,
        private val logoutUseCase: LogoutUseCase,
        private val getUserUseCase: GetUserUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(dataStore, loginUseCase, logoutUseCase, getUserUseCase) as T
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