package com.example.horseinacoat.presentation.screens.custom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.horseinacoat.R
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.presentation.navigation.NavigationRoutes
import com.example.horseinacoat.presentation.viewModel.custom.AllUsersPaginationViewModel
import com.example.horseinacoat.presentation.viewModel.custom.PaginationState
import com.example.horseinacoat.ui.theme.HorseInACoatTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllUsersScreen(
    navController: NavController,
    viewModel: AllUsersPaginationViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    val paginationState by viewModel.paginationState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
    val deleteInProgress by viewModel.deleteInProgress.collectAsState()

    AllUsersContent(
        navController = navController,
        users = users,
        paginationState = paginationState,
        isLoading = isLoading,
        error = error,
        showDeleteDialog = showDeleteDialog,
        deleteInProgress = deleteInProgress,
        onRefresh = { viewModel.refresh() },
        onLoadMore = { viewModel.loadNextPage() },
        onAddUserClick = {
            navController.navigate(NavigationRoutes.CUSTOM_ADD_NEW_RANDOM_USER_SCREEN)
        },
        onDeleteClick = { user -> viewModel.showDeleteConfirmation(user) },
        onConfirmDelete = { userId -> viewModel.deleteUser(userId) },
        onDismissDelete = { viewModel.hideDeleteConfirmation() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllUsersContent(
    navController: NavController,
    users: List<User>,
    paginationState: PaginationState,
    isLoading: Boolean,
    error: String?,
    showDeleteDialog: User?,
    deleteInProgress: String?,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onAddUserClick: () -> Unit,
    onDeleteClick: (User) -> Unit,
    onConfirmDelete: (String) -> Unit,
    onDismissDelete: () -> Unit
) {
    var expandedUserId by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 5 && paginationState.canLoadMore && !isLoading
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            onLoadMore()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "All Users (${paginationState.totalItems})",
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddUserClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "Add user"
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
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    if (isLoading && users.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading users...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else if (error != null && users.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onRefresh,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Retry")
                            }
                        }
                    } else if (users.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No Users Found",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Add users to see them in the list",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(users) { user ->
                                CustomUserCard(
                                    user = user,
                                    isExpanded = expandedUserId == user.id,
                                    isDeleting = deleteInProgress == user.id,
                                    onCardClick = {
                                        expandedUserId = if (expandedUserId == user.id) {
                                            null
                                        } else {
                                            user.id
                                        }
                                    },
                                    onDeleteClick = { onDeleteClick(user) }
                                )
                            }

                            if (paginationState.isLoading && users.isNotEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            if (paginationState.isLastPage && users.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "All users loaded",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (error != null && users.isNotEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = onRefresh,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError
                                    )
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }

                if (showDeleteDialog != null) {
                    DeleteConfirmationDialog(
                        user = showDeleteDialog,
                        onConfirm = { onConfirmDelete(showDeleteDialog.id) },
                        onDismiss = onDismissDelete
                    )
                }
            }
        }
    }
}

@Composable
fun CustomUserCard(
    user: User,
    isExpanded: Boolean,
    isDeleting: Boolean,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 8.dp else 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCardClick() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = user.picture.large,
                        error = painterResource(id = R.drawable.horse)
                    ),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.size(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${user.name.first} ${user.name.last}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    user.dob?.age?.let { age ->
                        Text(
                            text = "$age years",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                } else {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_del),
                            contentDescription = "Delete user",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ),
                                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                )
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                ExpandedTabButton(
                                    icon = R.drawable.ic_user,
                                    isSelected = selectedTab == 0,
                                    onClick = { selectedTab = 0 },
                                    isFirst = true
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                ExpandedTabButton(
                                    icon = R.drawable.ic_phone,
                                    isSelected = selectedTab == 1,
                                    onClick = { selectedTab = 1 }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                ExpandedTabButton(
                                    icon = R.drawable.ic_mail,
                                    isSelected = selectedTab == 2,
                                    onClick = { selectedTab = 2 }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                ExpandedTabButton(
                                    icon = R.drawable.ic_location,
                                    isSelected = selectedTab == 3,
                                    onClick = { selectedTab = 3 },
                                    isLast = true
                                )
                            }
                        }

                        when (selectedTab) {
                            0 -> ExpandedProfileContent(user)
                            1 -> ExpandedPhoneContent(user)
                            2 -> ExpandedEmailContent(user)
                            3 -> ExpandedLocationContent(user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    user: User,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete User",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to delete this user?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${user.name.first} ${user.name.last}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onErrorContainer,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.outline,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ExpandedTabButton(
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        Color.Transparent
    }

    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onPrimary
    }

    val shape = when {
        isFirst && isSelected -> RoundedCornerShape(topStart = 12.dp)
        isLast && isSelected -> RoundedCornerShape(topEnd = 12.dp)
        isFirst -> RoundedCornerShape(topStart = 12.dp)
        isLast -> RoundedCornerShape(topEnd = 12.dp)
        else -> RoundedCornerShape(0.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = backgroundColor, shape = shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = iconColor
        )
    }
}

@Composable
fun ExpandedProfileContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExpandedInfoRow("Title:", user.name.title)
        ExpandedInfoRow("First Name:", user.name.first)
        ExpandedInfoRow("Last Name:", user.name.last)
        ExpandedInfoRow("Gender:", user.gender.replaceFirstChar { it.uppercase() })
        ExpandedInfoRow("Nationality:", user.nat)
        user.dob?.age?.let { age ->
            ExpandedInfoRow("Age:", "$age years")
        }
        ExpandedInfoRow("Birth Date:", formatDate(user.dob?.date))
    }
}

private fun formatDate(dateString: String?): String {
    return try {
        if (dateString == null) return "Not shown"
        val instant = Instant.parse(dateString)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        DateTimeFormatter.ofPattern("dd.MM.yyyy").format(localDate)
    } catch (e: Exception) {
        "Not shown"
    }
}

@Composable
fun ExpandedPhoneContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExpandedInfoRow("Phone:", user.phone)
        ExpandedInfoRow("Cell:", user.cell)
    }
}

@Composable
fun ExpandedEmailContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExpandedInfoRow("Email:", user.email)
    }
}

@Composable
fun ExpandedLocationContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExpandedInfoRow("Street:", "${user.location.street.number} ${user.location.street.name}")
        ExpandedInfoRow("City:", user.location.city)
        ExpandedInfoRow("State:", user.location.state)
        ExpandedInfoRow("Country:", user.location.country)
        ExpandedInfoRow("Postcode:", user.location.postcode.toString())
    }
}

@Composable
fun ExpandedInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(name = "All Users - Loading", showBackground = true)
@Composable
fun AllUsersScreenLoadingPreview() {
    HorseInACoatTheme(darkTheme = false) {
        AllUsersContent(
            navController = rememberNavController(),
            users = emptyList(),
            paginationState = PaginationState(
                currentPage = 0,
                pageSize = 20,
                isLastPage = false,
                totalItems = 0,
                isLoading = true
            ),
            isLoading = true,
            error = null,
            showDeleteDialog = null,
            deleteInProgress = null,
            onRefresh = {},
            onLoadMore = {},
            onAddUserClick = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {}
        )
    }
}

@Preview(name = "All Users - Empty", showBackground = true)
@Composable
fun AllUsersScreenEmptyPreview() {
    HorseInACoatTheme(darkTheme = false) {
        AllUsersContent(
            navController = rememberNavController(),
            users = emptyList(),
            paginationState = PaginationState(),
            isLoading = false,
            error = null,
            showDeleteDialog = null,
            deleteInProgress = null,
            onRefresh = {},
            onLoadMore = {},
            onAddUserClick = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {}
        )
    }
}

@Preview(name = "All Users - With Users", showBackground = true)
@Composable
fun AllUsersScreenWithUsersPreview() {
    HorseInACoatTheme(darkTheme = false) {
        AllUsersContent(
            navController = rememberNavController(),
            users = List(3) { index ->
                User(
                    id = "$index",
                    gender = if (index % 2 == 0) "male" else "female",
                    name = com.example.horseinacoat.domain.model.secondary.Name(
                        first = "User",
                        last = "$index",
                        title = if (index % 2 == 0) "Mr" else "Ms"
                    ),
                    location = com.example.horseinacoat.domain.model.secondary.Location(
                        street = com.example.horseinacoat.domain.model.secondary.Street(
                            number = index,
                            name = "Street"
                        ),
                        city = "City $index",
                        state = "State",
                        country = "Country",
                        postcode = "12345"
                    ),
                    email = "user$index@example.com",
                    phone = "+123456789$index",
                    cell = "+123456789$index",
                    picture = com.example.horseinacoat.domain.model.secondary.Picture(
                        large = "",
                        medium = "",
                        thumbnail = ""
                    ),
                    nat = "US"
                )
            },
            paginationState = PaginationState(
                currentPage = 0,
                pageSize = 20,
                isLastPage = false,
                totalItems = 3,
                isLoading = false
            ),
            isLoading = false,
            error = null,
            showDeleteDialog = null,
            deleteInProgress = null,
            onRefresh = {},
            onLoadMore = {},
            onAddUserClick = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {}
        )
    }
}

@Preview(name = "All Users - With Delete Dialog", showBackground = true)
@Composable
fun AllUsersScreenWithDeleteDialogPreview() {
    HorseInACoatTheme(darkTheme = false) {
        AllUsersContent(
            navController = rememberNavController(),
            users = List(2) { index ->
                User(
                    id = "$index",
                    gender = if (index % 2 == 0) "male" else "female",
                    name = com.example.horseinacoat.domain.model.secondary.Name(
                        first = "User",
                        last = "$index",
                        title = if (index % 2 == 0) "Mr" else "Ms"
                    ),
                    location = com.example.horseinacoat.domain.model.secondary.Location(
                        street = com.example.horseinacoat.domain.model.secondary.Street(
                            number = index,
                            name = "Street"
                        ),
                        city = "City $index",
                        state = "State",
                        country = "Country",
                        postcode = "12345"
                    ),
                    email = "user$index@example.com",
                    phone = "+123456789$index",
                    cell = "+123456789$index",
                    picture = com.example.horseinacoat.domain.model.secondary.Picture(
                        large = "",
                        medium = "",
                        thumbnail = ""
                    ),
                    nat = "US"
                )
            },
            paginationState = PaginationState(
                currentPage = 0,
                pageSize = 20,
                isLastPage = false,
                totalItems = 2,
                isLoading = false
            ),
            isLoading = false,
            error = null,
            showDeleteDialog = User(
                id = "1",
                gender = "male",
                name = com.example.horseinacoat.domain.model.secondary.Name(
                    first = "John",
                    last = "Doe",
                    title = "Mr"
                ),
                location = com.example.horseinacoat.domain.model.secondary.Location(
                    street = com.example.horseinacoat.domain.model.secondary.Street(
                        number = 123,
                        name = "Main St"
                    ),
                    city = "New York",
                    state = "NY",
                    country = "USA",
                    postcode = "10001"
                ),
                email = "john.doe@example.com",
                phone = "+1-555-0123",
                cell = "+1-555-0124",
                picture = com.example.horseinacoat.domain.model.secondary.Picture(
                    large = "",
                    medium = "",
                    thumbnail = ""
                ),
                nat = "US"
            ),
            deleteInProgress = null,
            onRefresh = {},
            onLoadMore = {},
            onAddUserClick = {},
            onDeleteClick = {},
            onConfirmDelete = {},
            onDismissDelete = {}
        )
    }
}

@Preview(name = "Delete Dialog", showBackground = true)
@Composable
fun DeleteDialogPreview() {
    HorseInACoatTheme(darkTheme = false) {
        DeleteConfirmationDialog(
            user = User(
                id = "1",
                gender = "male",
                name = com.example.horseinacoat.domain.model.secondary.Name(
                    first = "John",
                    last = "Doe",
                    title = "Mr"
                ),
                location = com.example.horseinacoat.domain.model.secondary.Location(
                    street = com.example.horseinacoat.domain.model.secondary.Street(
                        number = 123,
                        name = "Main St"
                    ),
                    city = "New York",
                    state = "NY",
                    country = "USA",
                    postcode = "10001"
                ),
                email = "john.doe@example.com",
                phone = "+1-555-0123",
                cell = "+1-555-0124",
                picture = com.example.horseinacoat.domain.model.secondary.Picture(
                    large = "",
                    medium = "",
                    thumbnail = ""
                ),
                nat = "US"
            ),
            onConfirm = {},
            onDismiss = {}
        )
    }
}