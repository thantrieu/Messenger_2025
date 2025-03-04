package pro.branium.messenger.utils

import java.util.Date
import java.util.Locale

object AppUtils {
    fun Date.toDateString(): String {
        val pattern = "dd/MM/yyyy"
        return java.text.SimpleDateFormat(pattern, Locale.getDefault()).format(this)
    }
}