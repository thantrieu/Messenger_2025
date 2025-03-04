package pro.branium.messenger.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.domain.usecase.GetFriendAccountsUseCase
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val getFriendsAccountsUseCase: GetFriendAccountsUseCase
) : ViewModel() {
    private val _friendAccounts = MutableStateFlow<List<Account>>(emptyList())

    val friendAccounts: StateFlow<List<Account>> = _friendAccounts.asStateFlow()

    fun getFriends(username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val friends = getFriendsAccountsUseCase.execute(username)
            _friendAccounts.value = friends
        }
    }

    class Factory @Inject constructor(
        private val getFriendsAccountsUseCase: GetFriendAccountsUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(FriendViewModel::class.java)) {
                return FriendViewModel(getFriendsAccountsUseCase) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}