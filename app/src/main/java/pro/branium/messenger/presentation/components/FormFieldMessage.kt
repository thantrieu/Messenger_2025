package pro.branium.messenger.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pro.branium.messenger.presentation.ui.theme.DarkGreen

@Composable
fun FormFieldMessage(
    message: String?,
    isError: Boolean
) {
    if (!message.isNullOrEmpty()) {
        Text(
            text = message,
            color = if (isError) MaterialTheme.colorScheme.error else DarkGreen,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
}
