package pro.branium.messenger.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import pro.branium.messenger.R
import pro.branium.messenger.presentation.ui.theme.DarkGreen

@Composable
fun ProcessingDialog() {
    Dialog(
        onDismissRequest = { /* Prevent dismissing by clicking outside */ },
        DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp),
            colors = CardDefaults.cardColors(contentColor = DarkGreen)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp), color = DarkGreen)
                Text(stringResource(R.string.label_signing_up)) // Optional message
            }
        }
    }
}