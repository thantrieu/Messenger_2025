package pro.branium.messenger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.usecase.GetUserUseCase
import pro.branium.messenger.domain.usecase.LoginUseCase
import pro.branium.messenger.domain.usecase.LogoutUseCase
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow(false)
    private val _account = MutableStateFlow<Account?>(null)
    private val _startGoogleSignIn = MutableStateFlow(false)

    val account: StateFlow<Account?> = _account
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
    val startGoogleSignIn: StateFlow<Boolean> = _startGoogleSignIn

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // todo: check if user is logged in
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = loginUseCase.execute(Account(username = username, password = password))
            _account.value = result
            _isLoggedIn.value = result != null
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
                    //
                    onSuccess()
                } else {
                    throw Exception("Invalid Google ID")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Google login failed")
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            account.value?.let {
                logoutUseCase.execute(it)
                _isLoggedIn.value = false
            }
        }
    }

    fun getUser() {

    }

    class Factory @Inject constructor(
        private val loginUseCase: LoginUseCase,
        private val logoutUseCase: LogoutUseCase,
        private val getUserUseCase: GetUserUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(loginUseCase, logoutUseCase, getUserUseCase) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}