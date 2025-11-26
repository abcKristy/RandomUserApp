package com.example.horseinacoat.presentation.screens.usual

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.horseinacoat.presentation.viewModel.AddNewRandomUserViewModel
import com.example.horseinacoat.ui.theme.HorseInACoatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewRandomUserScreen(
    navController: NavController,
    viewModel: AddNewRandomUserViewModel = hiltViewModel()
) {
    val userState by viewModel.userState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    AddNewRandomUserContent(
        navController = navController,
        userState = userState,
        isLoading = isLoading,
        error = error,
        onFindUser = { gender, nationality ->
            viewModel.findRandomUser(gender, nationality)
        },
        onSaveUser = { user ->
            viewModel.saveUser(user)
        },
        onChangeUser = { gender, nationality ->
            viewModel.findRandomUser(gender, nationality)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewRandomUserContent(
    navController: NavController,
    userState: AddNewRandomUserState,
    isLoading: Boolean,
    error: String?,
    onFindUser: (String?, String?) -> Unit,
    onSaveUser: (User) -> Unit,
    onChangeUser: (String?, String?) -> Unit
) {
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var selectedNationality by remember { mutableStateOf<String?>(null) }
    var genderExpanded by remember { mutableStateOf(false) }
    var nationalityExpanded by remember { mutableStateOf(false) }

    val genders = listOf("Man", "Woman")
    val nationalities = listOf("AU", "BR", "CA", "CH", "DE", "DK", "ES", "FI", "FR", "GB", "IE", "IN", "IR", "MX", "NL", "NO", "NZ", "RS", "TR", "UA", "US")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Generate user",
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
                ExposedDropdownMenuBox(
                    expanded = genderExpanded,
                    onExpandedChange = { genderExpanded = !genderExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedGender ?: "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Male") },
                        placeholder = { Text("Choose male") }
                    )

                    ExposedDropdownMenu(
                        expanded = genderExpanded,
                        onDismissRequest = { genderExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Any") },
                            onClick = {
                                selectedGender = null
                                genderExpanded = false
                            }
                        )
                        genders.forEach { gender ->
                            DropdownMenuItem(
                                text = { Text(gender) },
                                onClick = {
                                    selectedGender = gender
                                    genderExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = nationalityExpanded,
                    onExpandedChange = { nationalityExpanded = !nationalityExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedNationality ?: "",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = nationalityExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text("Nationality") },
                        placeholder = { Text("Choose nationality") }
                    )

                    ExposedDropdownMenu(
                        expanded = nationalityExpanded,
                        onDismissRequest = { nationalityExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Any") },
                            onClick = {
                                selectedNationality = null
                                nationalityExpanded = false
                            }
                        )
                        nationalities.forEach { nationality ->
                            DropdownMenuItem(
                                text = { Text(nationality) },
                                onClick = {
                                    selectedNationality = nationality
                                    nationalityExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (userState) {
                    is AddNewRandomUserState.UserFound -> {
                        Button(
                            onClick = {
                                onChangeUser(
                                    selectedGender?.let { if (it == "Man") "male" else "female" },
                                    selectedNationality
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Text("Choose another user")
                        }
                    }
                    else -> {
                        Button(
                            onClick = {
                                onFindUser(
                                    selectedGender?.let { if (it == "Man") "male" else "female" },
                                    selectedNationality
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
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
                                Text("Find user")
                            }
                        }
                    }
                }

                if (error != null) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (userState) {
                    is AddNewRandomUserState.UserFound -> {
                        UserCard(
                            user = userState.user,
                            showSaveButton = true,
                            onSaveClick = { onSaveUser(userState.user) }
                        )
                    }
                    is AddNewRandomUserState.UserSaved -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "User saved!",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            UserCard(
                                user = userState.user,
                                showSaveButton = false,
                                onSaveClick = {}
                            )
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    showSaveButton: Boolean,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
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
                text = "Random user",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.picture.large,
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(80.dp)
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

                    Text(
                        text = "📞 ${user.phone}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "📧 ${user.email}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "🌍 ${user.nat}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "🏠 ${user.location.city}, ${user.location.country}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (showSaveButton) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Save user")
                }
            }
        }
    }
}

sealed class AddNewRandomUserState {
    object Initial : AddNewRandomUserState()
    object Loading : AddNewRandomUserState()
    data class UserFound(val user: User) : AddNewRandomUserState()
    data class UserSaved(val user: User) : AddNewRandomUserState()
    data class Error(val message: String) : AddNewRandomUserState()
}

private fun createMockUser(): User {
    return User(
        id = "1",
        gender = "male",
        name = Name(
            first = "Иван",
            last = "Петров",
            title = "Mr"
        ),
        location = Location(
            street = com.example.horseinacoat.domain.model.secondary.Street(
                number = 123,
                name = "Main St"
            ),
            city = "Москва",
            state = "Московская область",
            country = "Россия",
            postcode = "101000"
        ),
        email = "ivan.petrov@example.com",
        phone = "+7-495-123-45-67",
        cell = "+7-916-123-45-67",
        picture = Picture(
            large = "https://randomuser.me/api/portraits/men/1.jpg",
            medium = "https://randomuser.me/api/portraits/men/1.jpg",
            thumbnail = "https://randomuser.me/api/portraits/men/1.jpg"
        ),
        nat = "RU"
    )
}

@Preview(name = "Add New User - Initial", showBackground = true)
@Composable
fun AddNewRandomUserScreenInitialPreview() {
    HorseInACoatTheme(darkTheme = false) {
        AddNewRandomUserContent(
            navController = rememberNavController(),
            userState = AddNewRandomUserState.Initial,
            isLoading = false,
            error = null,
            onFindUser = { _, _ -> },
            onSaveUser = {},
            onChangeUser = { _, _ -> }
        )
    }
}

@Preview(name = "Add New User - Loading", showBackground = true)
@Composable
fun AddNewRandomUserScreenLoadingPreview() {
    HorseInACoatTheme(darkTheme = false) {
        AddNewRandomUserContent(
            navController = rememberNavController(),
            userState = AddNewRandomUserState.Initial,
            isLoading = true,
            error = null,
            onFindUser = { _, _ -> },
            onSaveUser = {},
            onChangeUser = { _, _ -> }
        )
    }
}

@Preview(name = "Add New User - User Found", showBackground = true)
@Composable
fun AddNewRandomUserScreenUserFoundPreview() {
    HorseInACoatTheme(darkTheme = false) {
        AddNewRandomUserContent(
            navController = rememberNavController(),
            userState = AddNewRandomUserState.UserFound(createMockUser()),
            isLoading = false,
            error = null,
            onFindUser = { _, _ -> },
            onSaveUser = {},
            onChangeUser = { _, _ -> }
        )
    }
}

@Preview(name = "Add New User - User Saved", showBackground = true)
@Composable
fun AddNewRandomUserScreenUserSavedPreview() {
    HorseInACoatTheme(darkTheme = false) {
        AddNewRandomUserContent(
            navController = rememberNavController(),
            userState = AddNewRandomUserState.UserSaved(createMockUser()),
            isLoading = false,
            error = null,
            onFindUser = { _, _ -> },
            onSaveUser = {},
            onChangeUser = { _, _ -> }
        )
    }
}

@Preview(name = "Add New User - Error", showBackground = true)
@Composable
fun AddNewRandomUserScreenErrorPreview() {
    HorseInACoatTheme(darkTheme = false) {
        AddNewRandomUserContent(
            navController = rememberNavController(),
            userState = AddNewRandomUserState.Initial,
            isLoading = false,
            error = "Не удалось найти пользователя",
            onFindUser = { _, _ -> },
            onSaveUser = {},
            onChangeUser = { _, _ -> }
        )
    }
}