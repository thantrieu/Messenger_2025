package pro.branium.messenger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.model.AccountIdentity
import pro.branium.messenger.domain.model.DataListState
import pro.branium.messenger.domain.model.Message
import pro.branium.messenger.domain.model.MessageList
import pro.branium.messenger.domain.model.UserProfile
import pro.branium.messenger.domain.model.error.AuthError
import pro.branium.messenger.domain.model.error.ProfileError
import pro.branium.messenger.domain.usecase.GetInitialUserUseCase
import pro.branium.messenger.domain.usecase.GetLastMessagesUseCase
import pro.branium.messenger.domain.usecase.GetProfileUseCase
import pro.branium.messenger.presentation.screens.HomeState
import pro.branium.messenger.utils.Result
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLastMessagesUseCase: GetLastMessagesUseCase,
    private val getInitialUserUseCase: GetInitialUserUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {
    private val _friendAccounts = MutableStateFlow<List<Account>>(emptyList())
    private val _lastMessages = MutableStateFlow<List<Message>>(emptyList())
    private val _lastMessageListState =
        MutableStateFlow<DataListState<MessageList>>(DataListState.Loading)
    private val _homeState = MutableStateFlow(HomeState())

    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()
    val lastMessages: StateFlow<List<Message>> = _lastMessages.asStateFlow()
    val lastMessageListState: StateFlow<DataListState<MessageList>> =
        _lastMessageListState.asStateFlow()

    init {
        checkInitialSession()
    }

    private fun checkInitialSession() {
        // Ensure state reflects auth check is starting
        _homeState.update { it.copy(isCheckingAuth = true, authError = null) }

        viewModelScope.launch {
            val sessionResult: Result<AuthError, AccountIdentity> =
                getInitialUserUseCase.execute() // Assume execute() takes no params

            when (sessionResult) {
                is pro.branium.messenger.utils.Result.Success -> {
                    val accountIdentity = sessionResult.value
                    _homeState.update {
                        it.copy(
                            isCheckingAuth = false,
                            authError = null,
                            loggedInUserIdentity = accountIdentity // Store identity
                        )
                    }
                    // Session is valid, now fetch the detailed profile
                    fetchUserProfile(accountIdentity.userId)
                }

                is Result.Failure -> {
                    val authError = sessionResult.error
                    // Handle specific auth errors if needed for different UI states
                    val errorMessage = when (authError) {
                        is AuthError.NotLoggedIn -> "Please log in."
                        is AuthError.SessionExpired -> "Your session expired. Please log in again."
                        is AuthError.NetworkError -> authError.message
                            ?: "Network error checking session."

                        is AuthError.Unknown -> authError.message ?: "Failed to verify session."
                    }
                    _homeState.update {
                        it.copy(
                            isCheckingAuth = false,
                            authError = errorMessage, // Set auth error message
                            loggedInUserIdentity = null // Ensure no user is set
                        )
                    }
                    // Do not proceed to fetch profile if session check fails
                }
            }
        }
    }


    fun fetchUserProfile(userId: String) { // Accept userId as parameter
        // Don't fetch if already loading or if auth check failed
        if (_homeState.value.isLoadingProfile || _homeState.value.authError != null) return

        _homeState.update {
            it.copy(
                isLoadingProfile = true,
                profileErrorMessage = null // Clear previous profile error
            )
        }

        viewModelScope.launch {
            // Use the provided userId
            val profileResult: Result<ProfileError, UserProfile> =
                getProfileUseCase.execute(userId) // Pass userId to use case

            // Process the result
            when (profileResult) {
                is Result.Success -> {
                    _homeState.update {
                        it.copy(
                            isLoadingProfile = false,
                            userProfile = profileResult.value, // Set the profile data
                            profileErrorMessage = null
                        )
                    }
                }

                is Result.Failure -> {
                    val profileError = profileResult.error // The Domain ProfileError

                    // Handle specific domain errors to show appropriate messages
                    val userFriendlyMessage = when (profileError) {
                        is ProfileError.NotFound ->
                            "Could not find your profile information."

                        is ProfileError.NetworkError ->
                            profileError.message ?: "Check your internet connection."

                        is ProfileError.UpdateFailed -> // Less likely for getProfile, but example
                            profileError.message ?: "Profile update failed."

                        is ProfileError.Unknown ->
                            profileError.message ?: "An unexpected error occurred."
                        // Add cases for any other ProfileError types you defined
                    }

                    _homeState.update {
                        it.copy(
                            isLoadingProfile = false,
                            userProfile = null, // Clear profile data on error
                            profileErrorMessage = userFriendlyMessage
                        )
                    }
                }
            }
        }
    }

    fun clearProfileError() {
        _homeState.update { it.copy(profileErrorMessage = null) }
    }

    /**
     * Call this from the UI to clear displayed auth error messages.
     */
    fun clearAuthError() {
        _homeState.update { it.copy(authError = null) }
    }

    fun setFriendAccounts(accounts: List<Account>) {
        _friendAccounts.value = accounts
    }

    fun loadLastMessages(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getLastMessagesUseCase.execute(username)
            result.onSuccess { messageList: MessageList ->
                _lastMessageListState.value = DataListState.Success<MessageList>(messageList)
            }.onFailure {
                _lastMessageListState.value =
                    DataListState.Error(message = it.message, throwable = it)
            }
        }
    }

    fun clear() {
        _friendAccounts.value = emptyList()
        _lastMessages.value = emptyList()
    }

    class Factory @Inject constructor(
        private val getLastMessagesUseCase: GetLastMessagesUseCase,
        private val getInitialUserUseCase: GetInitialUserUseCase,
        private val getProfileUseCase: GetProfileUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(
                    getLastMessagesUseCase,
                    getInitialUserUseCase,
                    getProfileUseCase
                ) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}