package pro.branium.messenger.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import pro.branium.messenger.R
import pro.branium.messenger.domain.model.AccountIdentity
import pro.branium.messenger.domain.model.DataListState
import pro.branium.messenger.domain.model.Message
import pro.branium.messenger.domain.model.MessageList
import pro.branium.messenger.domain.model.UserProfile
import pro.branium.messenger.presentation.ui.theme.DarkGreen
import pro.branium.messenger.presentation.ui.theme.LightGreen
import pro.branium.messenger.presentation.viewmodel.AuthViewModel
import pro.branium.messenger.presentation.viewmodel.FriendViewModel
import pro.branium.messenger.presentation.viewmodel.HomeViewModel

data class HomeState(
    // Auth Check State
    val isCheckingAuth: Boolean = true, // Start checking auth initially
    val authError: String? = null, // Error during initial auth check
    val loggedInUserIdentity: AccountIdentity? = null, // Store basic identity if needed

    // Profile Fetch State
    val isLoadingProfile: Boolean = false,
    val userProfile: UserProfile? = null,
    val profileErrorMessage: String? = null
)

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

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val drawerScope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val drawerItems = remember(loggedInAccount) {
        listOf(
            DrawerItemData("Home", Icons.Default.Home) {},
            DrawerItemData("Profile", Icons.Default.Person) { onNavigateToProfile() },
            DrawerItemData("Settings", Icons.Default.Settings) { onNavigateToSettings() },
            DrawerItemData("Logout", Icons.AutoMirrored.Filled.Logout) { onLogout() }
        )
    }

    LaunchedEffect(loggedInAccount) {
        loggedInAccount?.let { account ->
            friendViewModel.getFriends(account.username)
        }
    }

    LaunchedEffect(friendAccounts) {
        homeViewModel.setFriendAccounts(friendAccounts)
    }

    val lastMessages: List<Message> = remember(lastMessageState) {
        if (lastMessageState is DataListState.Success<MessageList>) {
            (lastMessageState as DataListState.Success<MessageList>).data.messages
        } else {
            emptyList<Message>()
        }
    }

    LaunchedEffect(lastMessages, loggedInAccount) {
        if (lastMessageState !is DataListState.Success) {
            loggedInAccount?.let { account ->
                homeViewModel.loadLastMessages(account.username)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                colors = listOf(DarkGreen, LightGreen)
                            ),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Start
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                HorizontalDivider()
                Spacer(Modifier.height(8.dp))
                drawerItems.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            drawerScope.launch {
                                drawerState.close()
                                selectedItemIndex = 0
                            }
                            item.action()
                        },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                CustomSearchAppBar(
                    profileImageUrl = loggedInAccount?.avatar, // Pass the image URL
                    onMenuClick = {
                        drawerScope.launch {
                            drawerState.open()
                        }
                    },      // Wire up menu click
                    onProfileClick = onNavigateToProfile,    // Wire up profile click
                    onSearchClick = {
                        // Decide what happens on search click
                        // Option 1: Navigate to a dedicated search screen
                        // navigateToSearchScreen() // You'd need to add this navigation event

                        // Option 2: Call existing search function (maybe with empty query?)
                        onNavigateToSearching("")
                    }
                )

                Text(
                    text = stringResource(R.string.recent_chat),
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                if (lastMessageState is DataListState.Loading) {
                    LoadingScreen()
                } else {
                    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp)) {
                        items(
                            count = friendAccounts.size,
                            key = { index -> friendAccounts[index].username },
                        ) { index ->
                            val account = friendAccounts[index]
                            AccountItem(
                                avatarUrl = account.avatar!!,
                                displayName = account.displayName,
                                lastMessage = "Last message",
                                readStatusUrl = account.avatar!!
                            )
                            if (index != lastMessages.lastIndex) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomSearchAppBar(
    modifier: Modifier = Modifier,
    profileImageUrl: String?, // Accept profile image URL
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit // Add search click listener
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(50),
        color = Color(0xFFF5EDE5), // Consider using MaterialTheme colors if applicable
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSearchClick) // Make the row clickable for search
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Menu Icon Button
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.DarkGray // Consider MaterialTheme colors
                )
            }

            // Search Text
            Text(
                text = stringResource(R.string.hint_search_chat), // Or pass as parameter if needed
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                color = Color.DarkGray, // Consider MaterialTheme colors
                fontSize = 16.sp,
                textAlign = TextAlign.Start
            )

            // Profile Image Button - Make the image itself clickable
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profileImageUrl)
                    .crossfade(true)
                    // Placeholder while loading
                    .placeholder(android.R.drawable.sym_def_app_icon)
                    // Image to show if loading fails
                    .error(android.R.drawable.ic_menu_gallery) // Example error drawable
                    .build(),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onProfileClick)
                    .background(Color.Gray) // Background for placeholder/error state
            )
            // Add slight padding after image if needed
            Spacer(modifier = Modifier.width(8.dp))
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
                .clip(shape = CircleShape)
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
                fontWeight = FontWeight.SemiBold,
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
                .clip(shape = CircleShape)
        )
    }
}

data class DrawerItemData(
    val label: String,
    val icon: ImageVector,
    val action: () -> Unit
)

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val authViewModel: AuthViewModel = hiltViewModel()
    HomeScreen(authViewModel)
}