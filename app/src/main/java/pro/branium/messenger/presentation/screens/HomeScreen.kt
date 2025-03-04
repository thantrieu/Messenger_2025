package pro.branium.messenger.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pro.branium.messenger.R
import pro.branium.messenger.domain.model.DataListState
import pro.branium.messenger.presentation.viewmodel.FriendViewModel
import pro.branium.messenger.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToChat: (username: String) -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToSearching: (key: String) -> Unit = {}
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val friendViewModel: FriendViewModel = hiltViewModel()
    val friendAccounts = friendViewModel.friendAccounts.collectAsState()
    val lastMessageState by homeViewModel.lastMessageListState.collectAsStateWithLifecycle()

    homeViewModel.setFriendAccounts(friendAccounts.value)

    if(lastMessageState !is DataListState.Success) {
        homeViewModel.loadLastMessages("kitty")
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
            LazyColumn {

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}