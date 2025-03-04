package pro.branium.messenger.presentation.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pro.branium.messenger.R

@Composable
fun MessageItem(
    messageText: String,
    @DrawableRes imageRes: Int,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                start = dimensionResource(id = R.dimen.spacing_48),
                top = dimensionResource(id = R.dimen.small_spacing),
                end = dimensionResource(id = R.dimen.medium_spacing),
                bottom = dimensionResource(id = R.dimen.small_spacing)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = stringResource(id = R.string.desc_item_message_image),
                modifier = Modifier
                    .size(
                        width = dimensionResource(id = R.dimen.small_avatar_width),
                        height = dimensionResource(id = R.dimen.small_avatar_height)
                    )
                    .padding(start = dimensionResource(id = R.dimen.medium_spacing))
            )

            Text(
                text = messageText,
                color = Color.White,
                fontSize = dimensionResource(id = R.dimen.text_medium).value.sp,
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.spacing_48))
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Preview
@Composable
fun MessageItemPreview() {
    MessageItem(
        messageText = "Hello, how are you?",
        imageRes = R.drawable.ic_launcher_foreground,
        isLoading = false
    )
}
