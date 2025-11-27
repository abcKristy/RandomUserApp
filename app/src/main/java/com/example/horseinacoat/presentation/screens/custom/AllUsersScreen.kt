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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.horseinacoat.R
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.presentation.navigation.NavigationRoutes
import com.example.horseinacoat.presentation.viewModel.custom.CustomListViewModel
import com.example.horseinacoat.ui.theme.HorseInACoatTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllUsersScreen(
    navController: NavController,
    viewModel: CustomListViewModel = hiltViewModel()
) {
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    AllUsersContent(
        navController = navController,
        users = users,
        isLoading = isLoading,
        error = error,
        onRefresh = { viewModel.refreshUsers() },
        onAddUserClick = {
            navController.navigate(NavigationRoutes.CUSTOM_ADD_NEW_RANDOM_USER_SCREEN)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllUsersContent(
    navController: NavController,
    users: List<User>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit,
    onAddUserClick: () -> Unit
) {
    var expandedUserId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "All Users",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (isLoading) {
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
                } else if (error != null) {
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(users) { user ->
                            CustomUserCard(
                                user = user,
                                isExpanded = expandedUserId == user.id,
                                onCardClick = {
                                    expandedUserId = if (expandedUserId == user.id) {
                                        null
                                    } else {
                                        user.id
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomUserCard(
    user: User,
    isExpanded: Boolean,
    onCardClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 8.dp else 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                        .clip(CircleShape)
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
            isLoading = true,
            error = null,
            onRefresh = {},
            onAddUserClick = {}
        )
    }
}

@Preview(name = "All Users - Error", showBackground = true)
@Composable
fun AllUsersScreenErrorPreview() {
    HorseInACoatTheme(darkTheme = false) {
        AllUsersContent(
            navController = rememberNavController(),
            users = emptyList(),
            isLoading = false,
            error = "Failed to load users",
            onRefresh = {},
            onAddUserClick = {}
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
            isLoading = false,
            error = null,
            onRefresh = {},
            onAddUserClick = {}
        )
    }
}