package pro.branium.messenger.domain.model

sealed class DataListState<out T> {
    data object Loading : DataListState<Nothing>()
    data class Success<T>(
        val data: T,
        val timeStamp: Long = System.currentTimeMillis()
    ) : DataListState<T>()

    data class Error(
        val message: String? = null,
        val throwable: Throwable? = null
    ) : DataListState<Nothing>()
}