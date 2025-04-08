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
import pro.branium.messenger.domain.model.DataListState
import pro.branium.messenger.domain.model.Message
import pro.branium.messenger.domain.model.MessageList
import pro.branium.messenger.domain.usecase.GetLastMessagesUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLastMessagesUseCase: GetLastMessagesUseCase
) : ViewModel() {
    private val _friendAccounts = MutableStateFlow<List<Account>>(emptyList())
    private val _lastMessages = MutableStateFlow<List<Message>>(emptyList())
    private val _lastMessageListState =
        MutableStateFlow<DataListState<MessageList>>(DataListState.Loading)

    val lastMessages: StateFlow<List<Message>> = _lastMessages.asStateFlow()
    val lastMessageListState: StateFlow<DataListState<*>> = _lastMessageListState.asStateFlow()

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
        private val getLastMessagesUseCase: GetLastMessagesUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                return HomeViewModel(getLastMessagesUseCase) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}