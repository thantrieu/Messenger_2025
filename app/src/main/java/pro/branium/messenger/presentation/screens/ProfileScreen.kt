package pro.branium.messenger.presentation.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import pro.branium.messenger.R
import pro.branium.messenger.domain.model.Account
import pro.branium.messenger.presentation.theme.DarkGreen
import pro.branium.messenger.presentation.theme.LightGreen
import pro.branium.messenger.presentation.theme.MiddleGreen
import pro.branium.messenger.presentation.theme.DarkGreen
import pro.branium.messenger.presentation.theme.Purple80
import pro.branium.messenger.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val loggedInAccount by authViewModel.account.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.title_profile))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = LightGreen
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding) // Apply padding from Scaffold
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            ProfileBackgroundImageSection(null)

            ProfileImageSection(loggedInAccount?.imageUrl)

            UserInfoSection(loggedInAccount)

            Spacer(modifier = Modifier.weight(1f)) // Push button to the bottom

            Button(
                onClick = { },
                shape = RoundedCornerShape(50), // Make it pill-shaped
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 32.dp
                    ) // Padding from the bottom edge
                    .height(50.dp)
            ) {
                Text(
                    text = stringResource(R.string.action_edit_profile),
                    color = Color.White, fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ProfileBackgroundImageSection(backGroundImageUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(backGroundImageUrl)
                .placeholder(R.drawable.avatar_2_raster)
                .error(R.drawable.avatar_2_raster)
                .crossfade(true)
                .build(),
            contentDescription = "Profile Background",
            contentScale = ContentScale.Crop, // Crop the image to fit
            modifier = Modifier.matchParentSize(),
            alpha = 0.75f
        )
        IconButton(
            onClick = { /* TODO: Handle change cover photo? */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp) // Adjust padding for exact position
                .size(40.dp)
                .clip(shape = CircleShape)
                .border(width = 1.dp, color = DarkGreen, shape = CircleShape)
                .background(MaterialTheme.colorScheme.surface, CircleShape) // Background for icon
                .padding(2.dp) // Padding inside the icon background
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "Change Cover Photo", // Or other action
                tint = DarkGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ProfileImageSection(avatarUrl: String?) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp)
            .size(160.dp)
            .offset(y = (-80).dp),
        contentAlignment = Alignment.BottomStart
    ) {
        // Profile Image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .placeholder(R.drawable.avatar_2_raster)
                .error(R.drawable.avatar_2_raster)
                .crossfade(true)
                .build(),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop, // Crop the image to fit
            modifier = Modifier
                .size(160.dp)
                .clip(shape = CircleShape) // Clip the image to a circle
                .border(border = BorderStroke(2.dp, DarkGreen), shape = CircleShape) // Border
                .background(Color.LightGray), // Placeholder background for the image
            alignment = Alignment.BottomStart
        )

        // Camera Icon on Profile Picture
        IconButton(
            onClick = { /* TODO: Handle change profile picture */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 10.dp, end = 10.dp) // Adjust padding for exact position
                .size(36.dp)
                .clip(shape = CircleShape)
                .border(width = 1.dp, color = DarkGreen, shape = CircleShape)
                .background(
                    MaterialTheme.colorScheme.surface,
                    CircleShape
                ) // Small background for icon
                .padding(2.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "Change Profile Picture",
                tint = DarkGreen,
                modifier = Modifier.size(20.dp) // Smaller icon
            )
        }
    }
}

@Composable
fun UserInfoSection(account: Account?) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .fillMaxWidth()
            .offset(y = (-80).dp),
        horizontalAlignment = Alignment.Start // Align text to the left
    ) {
        val displayName = account?.displayName ?: ""
        val numberOfFriends = account?.friends?.size ?: 0
        val slogan = account?.slogan ?: ""
        val createdAt = account?.createdAt ?: ""
        val username = account?.username ?: ""
        Text(
            text = displayName,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.number_of_friends, numberOfFriends),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = slogan,
            fontSize = 18.sp
            // fontStyle = FontStyle.Italic // Optional: if you want italic
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(thickness = 1.dp, color = DarkGreen.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))
        UserInfoRow("Username:", username)
        Spacer(modifier = Modifier.height(8.dp))
        // Using the current date from the image, replace with dynamic data
        UserInfoRow("Created at:", createdAt)
    }
}

@Composable
fun UserInfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            fontSize = 16.sp,
            modifier = Modifier.width(100.dp) // Adjust width as needed for alignment
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


// --- Preview ---
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        val authViewModel: AuthViewModel = viewModel()
        ProfileScreen(authViewModel, NavController(LocalContext.current))
    }
}