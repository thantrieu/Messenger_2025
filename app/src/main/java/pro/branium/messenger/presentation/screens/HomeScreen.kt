package pro.branium.messenger.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import pro.branium.messenger.R
import pro.branium.messenger.domain.model.DataListState
import pro.branium.messenger.domain.model.Message
import pro.branium.messenger.presentation.viewmodel.AuthViewModel
import pro.branium.messenger.presentation.viewmodel.FriendViewModel
import pro.branium.messenger.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToChat: (username: String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSearching: (key: String) -> Unit = {}
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val friendViewModel: FriendViewModel = hiltViewModel()
    val loggedInAccount by authViewModel.account.collectAsState()
    val friendAccounts by friendViewModel.friendAccounts.collectAsState()
    val lastMessageState by homeViewModel.lastMessageListState.collectAsStateWithLifecycle()

    loggedInAccount?.let { account ->
        friendViewModel.getFriends(account.username)
    }
    homeViewModel.setFriendAccounts(friendAccounts)
    val lastMessages: List<Message> = if (lastMessageState is DataListState.Success<*>) {
        (lastMessageState as DataListState.Success<*>).data as List<Message>
    } else {
        emptyList<Message>()
    }

    if (lastMessageState !is DataListState.Success) {
        loggedInAccount?.let { account ->
            homeViewModel.loadLastMessages(account.username)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.recent_chat),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )

            LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
                itemsIndexed(
                    items = friendAccounts,
                    key = { _, account -> account.username }
                ) { index, account ->
                    AccountItem(
                        avatarUrl = account.imageUrl!!,
                        displayName = account.displayName,
                        lastMessage = "Last message",
                        readStatusUrl = account.imageUrl!!
                    )
                    if (index != lastMessages.lastIndex) {
                        HorizontalDivider()
                    }
                }
                items(
                    count = friendAccounts.size,
                    key = { index -> friendAccounts[index].username },
                ) { index ->
                    val account = friendAccounts[index]
                    AccountItem(
                        avatarUrl = account.imageUrl!!,
                        displayName = account.displayName,
                        lastMessage = "Last message",
                        readStatusUrl = account.imageUrl!!
                    )
                    if (index != lastMessages.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun AccountItem(
    avatarUrl: String, // URL or resource for the avatar image
    displayName: String,
    lastMessage: String,
    readStatusUrl: String // URL or resource for the read status image
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                start = dimensionResource(R.dimen.medium_spacing),
                top = dimensionResource(R.dimen.small_spacing),
                end = dimensionResource(R.dimen.medium_spacing),
                bottom = dimensionResource(R.dimen.small_spacing)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar Image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(avatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.desc_avatar_image),
            modifier = Modifier
                .size(
                    width = dimensionResource(R.dimen.account_avatar_width),
                    height = dimensionResource(R.dimen.account_avatar_height)
                )
        )

        // Spacer between avatar and text
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.medium_spacing)))

        // Display Name and Last Message in a Column
        Column(
            modifier = Modifier
                .weight(1f) // Take remaining space between avatar and read status
        ) {
            Text(
                text = displayName,
                fontSize = 16.sp, // Equivalent to TextAppearance.AppCompat.Title
                modifier = Modifier
                    .padding(top = dimensionResource(R.dimen.super_small_spacing))
            )
            Text(
                text = lastMessage,
                fontSize = 14.sp, // Equivalent to TextAppearance.Material3.TitleSmall
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = dimensionResource(R.dimen.super_small_spacing))
            )
        }

        // Read Status Image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(readStatusUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(
                    width = dimensionResource(R.dimen.small_avatar_width),
                    height = dimensionResource(R.dimen.small_avatar_height)
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val authViewModel: AuthViewModel = hiltViewModel()
    HomeScreen(authViewModel)
}