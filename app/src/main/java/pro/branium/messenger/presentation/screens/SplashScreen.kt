package pro.branium.messenger.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pro.branium.messenger.R
import pro.branium.messenger.presentation.theme.DarkGreen
import pro.branium.messenger.presentation.theme.LightGreen

@Composable
fun SplashScreen(content: @Composable () -> Unit = {}) {
    AnimatedVisibility(visible = true) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.messenger1),
                contentDescription = "App Logo",
                modifier = Modifier.size(128.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.primary,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = listOf(DarkGreen, LightGreen)
                    ),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(32.dp))

            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}