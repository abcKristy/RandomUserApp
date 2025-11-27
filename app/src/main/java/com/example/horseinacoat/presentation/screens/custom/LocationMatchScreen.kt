package com.example.horseinacoat.presentation.screens.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.horseinacoat.R
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.model.secondary.Location
import com.example.horseinacoat.domain.model.secondary.Name
import com.example.horseinacoat.domain.model.secondary.Picture
import com.example.horseinacoat.domain.model.secondary.Street
import com.example.horseinacoat.presentation.viewModel.custom.FindFriendViewModel
import com.example.horseinacoat.ui.theme.HorseInACoatTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationMatchScreen(
    navController: NavController,
    viewModel: FindFriendViewModel = hiltViewModel()
) {
    val savedUsers by viewModel.savedUsers.collectAsState()
    val selectedUser by viewModel.selectedUser.collectAsState()
    val foundFriend by viewModel.foundFriend.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = Unit) {
        viewModel.friendSaved.collectLatest { saved ->
            if (saved) {
                snackbarHostState.showSnackbar("Friend added to database")
            }
        }
    }

    FindFriendContent(
        navController = navController,
        savedUsers = savedUsers,
        selectedUser = selectedUser,
        foundFriend = foundFriend,
        isLoading = isLoading,
        error = error,
        snackbarHostState = snackbarHostState,
        onUserSelect = { user ->
            viewModel.selectUser(user)
        },
        onFindFriend = {
            viewModel.findFriendForSelectedUser()
        },
        onSaveFriend = {
            viewModel.saveFoundFriend()
        },
        onClearSelection = {
            viewModel.clearSelection()
        },
        onRetry = {
            viewModel.loadSavedUsers()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindFriendContent(
    navController: NavController,
    savedUsers: List<User>,
    selectedUser: User?,
    foundFriend: User?,
    isLoading: Boolean,
    error: String?,
    snackbarHostState: SnackbarHostState,
    onUserSelect: (User) -> Unit,
    onFindFriend: () -> Unit,
    onSaveFriend: () -> Unit,
    onClearSelection: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Find a friend",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    content = {
                        Text(text = data.visuals.message)
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select a user to find a friend",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (savedUsers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No saved users",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(onClick = onRetry) {
                                Text("Retry")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(savedUsers) { user ->
                            UserSelectionCard(
                                user = user,
                                isSelected = selectedUser?.id == user.id,
                                onSelect = { onUserSelect(user) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                selectedUser?.let { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Selected user:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${user.name.first} ${user.name.last}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Nationality: ${user.nat}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onFindFriend,
                                    modifier = Modifier.weight(1f),
                                    enabled = !isLoading,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    } else {
                                        Text("Find friend")
                                    }
                                }

                                OutlinedButton(
                                    onClick = onClearSelection,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Clear")
                                }
                            }
                        }
                    }
                }

                foundFriend?.let { friend ->
                    Spacer(modifier = Modifier.height(16.dp))
                    FriendFoundCard(
                        friend = friend,
                        onSave = onSaveFriend,
                        isLoading = isLoading
                    )
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun UserSelectionCard(
    user: User,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.picture.thumbnail,
                contentDescription = "User avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${user.name.first} ${user.name.last}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "🌍 ${user.nat} • 📧 ${user.email}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSelected) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_up),
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun FriendFoundCard(
    friend: User,
    onSave: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Found friend!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = friend.picture.large,
                    contentDescription = "Friend Avatar",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.size(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${friend.name.first} ${friend.name.last}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "📞 ${friend.phone}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "📧 ${friend.email}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "🌍 ${friend.nat}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "🏠 ${friend.location.city}, ${friend.location.country}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save friend")
                }
            }
        }
    }
}

private fun createMockUserList(): List<User> {
    return listOf(
        User(
            id = "1",
            gender = "male",
            name = Name(
                first = "John",
                last = "Doe",
                title = "Mr"
            ),
            location = Location(
                street = Street(
                    number = 123,
                    name = "Main St"
                ),
                city = "New York",
                state = "NY",
                country = "USA",
                postcode = "10001"
            ),
            email = "john.doe@example.com",
            phone = "+1-555-123-4567",
            cell = "+1-555-765-4321",
            picture = Picture(
                large = "https://randomuser.me/api/portraits/men/1.jpg",
                medium = "https://randomuser.me/api/portraits/men/1.jpg",
                thumbnail = "https://randomuser.me/api/portraits/men/1.jpg"
            ),
            nat = "US"
        ),
        User(
            id = "2",
            gender = "female",
            name = Name(
                first = "Maria",
                last = "Silva",
                title = "Ms"
            ),
            location = Location(
                street = Street(
                    number = 456,
                    name = "Avenida Paulista"
                ),
                city = "São Paulo",
                state = "SP",
                country = "Brazil",
                postcode = "01310"
            ),
            email = "maria.silva@example.com",
            phone = "+55-11-98765-4321",
            cell = "+55-11-91234-5678",
            picture = Picture(
                large = "https://randomuser.me/api/portraits/women/2.jpg",
                medium = "https://randomuser.me/api/portraits/women/2.jpg",
                thumbnail = "https://randomuser.me/api/portraits/women/2.jpg"
            ),
            nat = "BR"
        )
    )
}

private fun createMockFriend(): User {
    return User(
        id = "3",
        gender = "male",
        name = Name(
            first = "Michael",
            last = "Smith",
            title = "Mr"
        ),
        location = Location(
            street = Street(
                number = 789,
                name = "Broadway"
            ),
            city = "Los Angeles",
            state = "CA",
            country = "USA",
            postcode = "90001"
        ),
        email = "michael.smith@example.com",
        phone = "+1-555-987-6543",
        cell = "+1-555-123-4567",
        picture = Picture(
            large = "https://randomuser.me/api/portraits/men/3.jpg",
            medium = "https://randomuser.me/api/portraits/men/3.jpg",
            thumbnail = "https://randomuser.me/api/portraits/men/3.jpg"
        ),
        nat = "US"
    )
}

@Preview(name = "Find Friend - Initial", showBackground = true)
@Composable
fun FindFriendScreenInitialPreview() {
    HorseInACoatTheme(darkTheme = false) {
        val snackbarHostState = remember { SnackbarHostState() }
        FindFriendContent(
            navController = rememberNavController(),
            savedUsers = createMockUserList(),
            selectedUser = null,
            foundFriend = null,
            isLoading = false,
            error = null,
            snackbarHostState = snackbarHostState,
            onUserSelect = {},
            onFindFriend = {},
            onSaveFriend = {},
            onClearSelection = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Find Friend - User Selected", showBackground = true)
@Composable
fun FindFriendScreenUserSelectedPreview() {
    HorseInACoatTheme(darkTheme = false) {
        val snackbarHostState = remember { SnackbarHostState() }
        FindFriendContent(
            navController = rememberNavController(),
            savedUsers = createMockUserList(),
            selectedUser = createMockUserList().first(),
            foundFriend = null,
            isLoading = false,
            error = null,
            snackbarHostState = snackbarHostState,
            onUserSelect = {},
            onFindFriend = {},
            onSaveFriend = {},
            onClearSelection = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Find Friend - Friend Found", showBackground = true)
@Composable
fun FindFriendScreenFriendFoundPreview() {
    HorseInACoatTheme(darkTheme = false) {
        val snackbarHostState = remember { SnackbarHostState() }
        FindFriendContent(
            navController = rememberNavController(),
            savedUsers = createMockUserList(),
            selectedUser = createMockUserList().first(),
            foundFriend = createMockFriend(),
            isLoading = false,
            error = null,
            snackbarHostState = snackbarHostState,
            onUserSelect = {},
            onFindFriend = {},
            onSaveFriend = {},
            onClearSelection = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Find Friend - Loading", showBackground = true)
@Composable
fun FindFriendScreenLoadingPreview() {
    HorseInACoatTheme(darkTheme = false) {
        val snackbarHostState = remember { SnackbarHostState() }
        FindFriendContent(
            navController = rememberNavController(),
            savedUsers = createMockUserList(),
            selectedUser = createMockUserList().first(),
            foundFriend = null,
            isLoading = true,
            error = null,
            snackbarHostState = snackbarHostState,
            onUserSelect = {},
            onFindFriend = {},
            onSaveFriend = {},
            onClearSelection = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Find Friend - Error", showBackground = true)
@Composable
fun FindFriendScreenErrorPreview() {
    HorseInACoatTheme(darkTheme = false) {
        val snackbarHostState = remember { SnackbarHostState() }
        FindFriendContent(
            navController = rememberNavController(),
            savedUsers = createMockUserList(),
            selectedUser = createMockUserList().first(),
            foundFriend = null,
            isLoading = false,
            error = "Failed to find friend",
            snackbarHostState = snackbarHostState,
            onUserSelect = {},
            onFindFriend = {},
            onSaveFriend = {},
            onClearSelection = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Find Friend - Empty", showBackground = true)
@Composable
fun FindFriendScreenEmptyPreview() {
    HorseInACoatTheme(darkTheme = false) {
        val snackbarHostState = remember { SnackbarHostState() }
        FindFriendContent(
            navController = rememberNavController(),
            savedUsers = emptyList(),
            selectedUser = null,
            foundFriend = null,
            isLoading = false,
            error = null,
            snackbarHostState = snackbarHostState,
            onUserSelect = {},
            onFindFriend = {},
            onSaveFriend = {},
            onClearSelection = {},
            onRetry = {}
        )
    }
}

@Preview(name = "Find Friend - Night", showBackground = true)
@Composable
fun FindFriendScreenNightPreview() {
    HorseInACoatTheme(darkTheme = true) {
        val snackbarHostState = remember { SnackbarHostState() }
        FindFriendContent(
            navController = rememberNavController(),
            savedUsers = createMockUserList(),
            selectedUser = createMockUserList().first(),
            foundFriend = createMockFriend(),
            isLoading = false,
            error = null,
            snackbarHostState = snackbarHostState,
            onUserSelect = {},
            onFindFriend = {},
            onSaveFriend = {},
            onClearSelection = {},
            onRetry = {}
        )
    }
}