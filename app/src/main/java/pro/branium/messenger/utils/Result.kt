package pro.branium.messenger.utils

sealed class Result<out E, out V> {
    data class Success<out V>(val value: V) : Result<Nothing, V>()
    data class Failure<out E>(val error: E) : Result<E, Nothing>()
}