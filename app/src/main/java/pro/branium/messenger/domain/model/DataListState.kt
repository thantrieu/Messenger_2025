package pro.branium.messenger.domain.model

sealed class DataListState<T> {
    data object Loading : DataListState<Nothing>()
    data object Empty : DataListState<Nothing>()
    data class Success<T>(val data: List<T>) : DataListState<T>()
    data class Error(val message: String) : DataListState<Nothing>()
}