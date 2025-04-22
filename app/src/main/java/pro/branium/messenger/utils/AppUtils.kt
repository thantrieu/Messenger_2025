package pro.branium.messenger.utils

import java.util.Date
import java.util.Locale

object AppUtils {
    fun Date.toDateString(): String {
        val pattern = "dd/MM/yyyy"
        return try {
            java.text.SimpleDateFormat(pattern, Locale.getDefault()).format(this)
        } catch (_: Exception) {
            "01/01/2000"
        }
    }

    fun String.stringToDate(): Date {
        val pattern = "dd/MM/yyyy"
        return try {
            java.text.SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
        } catch (_: Exception) {
            Date()
        }
    }
}