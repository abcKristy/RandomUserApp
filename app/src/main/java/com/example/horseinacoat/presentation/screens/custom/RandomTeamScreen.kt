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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.horseinacoat.R
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.presentation.viewModel.custom.RandomTeamViewModel
import com.example.horseinacoat.ui.theme.HorseInACoatTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomTeamScreen(
    navController: NavController,
    viewModel: RandomTeamViewModel = hiltViewModel()
) {
    val teamUsers by viewModel.teamUsers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val error by viewModel.error.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val selectedGender by viewModel.selectedGender.collectAsState()
    val selectedNationality by viewModel.selectedNationality.collectAsState()
    val teamSize by viewModel.teamSize.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = saveSuccess) {
        if (saveSuccess) {
            snackbarHostState.showSnackbar("Команда успешно сохранена в базу данных!")
            viewModel.resetSaveSuccess()
        }
    }

    LaunchedEffect(key1 = error) {
        if (error != null && !saveSuccess) {
            snackbarHostState.showSnackbar(error ?: "Произошла ошибка")
        }
    }

    RandomTeamContent(
        navController = navController,
        teamUsers = teamUsers,
        isLoading = isLoading,
        isSaving = isSaving,
        error = error,
        saveSuccess = saveSuccess,
        selectedGender = selectedGender,
        selectedNationality = selectedNationality,
        teamSize = teamSize,
        snackbarHostState = snackbarHostState,
        onGenerateTeam = { viewModel.generateRandomTeam() },
        onSaveTeam = { viewModel.saveTeamToDatabase() },
        onGenderSelect = { gender -> viewModel.setGender(gender) },
        onNationalitySelect = { nationality -> viewModel.setNationality(nationality) },
        onTeamSizeChange = { size -> viewModel.setTeamSize(size) },
        onClearTeam = { viewModel.clearTeam() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RandomTeamContent(
    navController: NavController,
    teamUsers: List<User>,
    isLoading: Boolean,
    isSaving: Boolean,
    error: String?,
    saveSuccess: Boolean,
    selectedGender: String?,
    selectedNationality: String?,
    teamSize: Int,
    snackbarHostState: SnackbarHostState,
    onGenerateTeam: () -> Unit,
    onSaveTeam: () -> Unit,
    onGenderSelect: (String?) -> Unit,
    onNationalitySelect: (String?) -> Unit,
    onTeamSizeChange: (Int) -> Unit,
    onClearTeam: () -> Unit
) {
    var genderExpanded by remember { mutableStateOf(false) }
    var nationalityExpanded by remember { mutableStateOf(false) }

    val genders = listOf("Man", "Woman")
    val nationalities = listOf("AU", "BR", "CA", "CH", "DE", "DK", "ES", "FI", "FR", "GB", "IE", "IN", "IR", "MX", "NL", "NO", "NZ", "RS", "TR", "UA", "US")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Random Team Generator",
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
                    containerColor = if (saveSuccess) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.errorContainer,
                    contentColor = if (saveSuccess) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onErrorContainer,
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
                // Team Configuration Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Team Configuration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Team Size Slider
                        Column {
                            Text(
                                text = "Team Size: $teamSize",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Slider(
                                value = teamSize.toFloat(),
                                onValueChange = { onTeamSizeChange(it.toInt()) },
                                valueRange = 1f..20f,
                                steps = 19,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Gender Selection
                        ExposedDropdownMenuBox(
                            expanded = genderExpanded,
                            onExpandedChange = { genderExpanded = !genderExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedGender?.let {
                                    if (it == "male") "Man" else "Woman"
                                } ?: "Any Gender",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                label = { Text("Gender") },
                                placeholder = { Text("Choose gender") }
                            )

                            ExposedDropdownMenu(
                                expanded = genderExpanded,
                                onDismissRequest = { genderExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Any Gender") },
                                    onClick = {
                                        onGenderSelect(null)
                                        genderExpanded = false
                                    }
                                )
                                genders.forEach { gender ->
                                    DropdownMenuItem(
                                        text = { Text(gender) },
                                        onClick = {
                                            onGenderSelect(if (gender == "Man") "male" else "female")
                                            genderExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Nationality Selection
                        ExposedDropdownMenuBox(
                            expanded = nationalityExpanded,
                            onExpandedChange = { nationalityExpanded = !nationalityExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedNationality ?: "Any Nationality",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = nationalityExpanded)
                                },
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
                                    text = { Text("Any Nationality") },
                                    onClick = {
                                        onNationalitySelect(null)
                                        nationalityExpanded = false
                                    }
                                )
                                nationalities.forEach { nationality ->
                                    DropdownMenuItem(
                                        text = { Text(nationality) },
                                        onClick = {
                                            onNationalitySelect(nationality)
                                            nationalityExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Buttons Row - Clear слева, Get по центру, Save справа
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Clear Button - слева (только когда есть команда)
                            if (teamUsers.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = onClearTeam,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Clear",
                                        fontSize = 14.sp
                                    )
                                }
                            } else {
                                // Заполнитель слева когда команды нет
                                Spacer(modifier = Modifier.weight(1f))
                            }

                            // Get Button - по центру (всегда видна)
                            Button(
                                onClick = onGenerateTeam,
                                modifier = Modifier.weight(1.5f),
                                enabled = !isLoading && !isSaving,
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
                                    Text(
                                        text = "Get",
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            // Save Button - справа (только когда есть команда)
                            if (teamUsers.isNotEmpty()) {
                                Button(
                                    onClick = onSaveTeam,
                                    modifier = Modifier.weight(1f),
                                    enabled = !isSaving && !isLoading,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary
                                    )
                                ) {
                                    if (isSaving) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                    } else {
                                        Text(
                                            text = "Save",
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            } else {
                                // Заполнитель справа когда команды нет
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Results Section
                if (error != null && !saveSuccess) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (teamUsers.isNotEmpty()) {
                    Text(
                        text = "Team Members (${teamUsers.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(teamUsers) { user ->
                            TeamMemberCard(user = user)
                        }
                    }
                } else if (!isLoading && error == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_group),
                                contentDescription = "No Team",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No Team Generated",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Configure parameters and generate your team",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Generating team...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamMemberCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.picture.thumbnail,
                contentDescription = "User Avatar",
                modifier = Modifier
                    .size(50.dp)
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
                    text = "📧 ${user.email}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row {
                    Text(
                        text = "🌍 ${user.nat}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = if (user.gender == "male") "♂" else "♀",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(name = "Random Team - Day", showBackground = true)
@Composable
fun RandomTeamScreenDayPreview() {
    HorseInACoatTheme(darkTheme = false) {
        RandomTeamContent(
            navController = rememberNavController(),
            teamUsers = emptyList(),
            isLoading = false,
            isSaving = false,
            error = null,
            saveSuccess = false,
            selectedGender = null,
            selectedNationality = null,
            teamSize = 5,
            snackbarHostState = remember { SnackbarHostState() },
            onGenerateTeam = {},
            onSaveTeam = {},
            onGenderSelect = {},
            onNationalitySelect = {},
            onTeamSizeChange = {},
            onClearTeam = {}
        )
    }
}

@Preview(name = "Random Team - With Team Generated", showBackground = true)
@Composable
fun RandomTeamScreenWithTeamPreview() {
    HorseInACoatTheme(darkTheme = false) {
        RandomTeamContent(
            navController = rememberNavController(),
            teamUsers = List(5) { index ->
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
                        large = "https://randomuser.me/api/portraits/men/$index.jpg",
                        medium = "https://randomuser.me/api/portraits/men/$index.jpg",
                        thumbnail = "https://randomuser.me/api/portraits/men/$index.jpg"
                    ),
                    nat = if (index % 2 == 0) "US" else "UK"
                )
            },
            isLoading = false,
            isSaving = false,
            error = null,
            saveSuccess = false,
            selectedGender = "male",
            selectedNationality = "US",
            teamSize = 5,
            snackbarHostState = remember { SnackbarHostState() },
            onGenerateTeam = {},
            onSaveTeam = {},
            onGenderSelect = {},
            onNationalitySelect = {},
            onTeamSizeChange = {},
            onClearTeam = {}
        )
    }
}

@Preview(name = "Random Team - Saving", showBackground = true)
@Composable
fun RandomTeamScreenSavingPreview() {
    HorseInACoatTheme(darkTheme = false) {
        RandomTeamContent(
            navController = rememberNavController(),
            teamUsers = List(3) { index ->
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
                        large = "https://randomuser.me/api/portraits/men/$index.jpg",
                        medium = "https://randomuser.me/api/portraits/men/$index.jpg",
                        thumbnail = "https://randomuser.me/api/portraits/men/$index.jpg"
                    ),
                    nat = if (index % 2 == 0) "US" else "UK"
                )
            },
            isLoading = false,
            isSaving = true,
            error = null,
            saveSuccess = false,
            selectedGender = "female",
            selectedNationality = "UK",
            teamSize = 8,
            snackbarHostState = remember { SnackbarHostState() },
            onGenerateTeam = {},
            onSaveTeam = {},
            onGenderSelect = {},
            onNationalitySelect = {},
            onTeamSizeChange = {},
            onClearTeam = {}
        )
    }
}

@Preview(name = "Random Team - Save Success", showBackground = true)
@Composable
fun RandomTeamScreenSaveSuccessPreview() {
    HorseInACoatTheme(darkTheme = false) {
        RandomTeamContent(
            navController = rememberNavController(),
            teamUsers = List(4) { index ->
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
                        large = "https://randomuser.me/api/portraits/men/$index.jpg",
                        medium = "https://randomuser.me/api/portraits/men/$index.jpg",
                        thumbnail = "https://randomuser.me/api/portraits/men/$index.jpg"
                    ),
                    nat = if (index % 3 == 0) "US" else if (index % 3 == 1) "UK" else "CA"
                )
            },
            isLoading = false,
            isSaving = false,
            error = null,
            saveSuccess = true,
            selectedGender = null,
            selectedNationality = null,
            teamSize = 10,
            snackbarHostState = remember { SnackbarHostState() },
            onGenerateTeam = {},
            onSaveTeam = {},
            onGenderSelect = {},
            onNationalitySelect = {},
            onTeamSizeChange = {},
            onClearTeam = {}
        )
    }
}

@Preview(name = "Random Team - Error State", showBackground = true)
@Composable
fun RandomTeamScreenErrorPreview() {
    HorseInACoatTheme(darkTheme = false) {
        RandomTeamContent(
            navController = rememberNavController(),
            teamUsers = emptyList(),
            isLoading = false,
            isSaving = false,
            error = "Не удалось найти пользователей с выбранными параметрами",
            saveSuccess = false,
            selectedGender = "male",
            selectedNationality = "US",
            teamSize = 5,
            snackbarHostState = remember { SnackbarHostState() },
            onGenerateTeam = {},
            onSaveTeam = {},
            onGenderSelect = {},
            onNationalitySelect = {},
            onTeamSizeChange = {},
            onClearTeam = {}
        )
    }
}

@Preview(name = "Random Team - Night Mode", showBackground = true)
@Composable
fun RandomTeamScreenNightPreview() {
    HorseInACoatTheme(darkTheme = true) {
        RandomTeamContent(
            navController = rememberNavController(),
            teamUsers = List(2) { index ->
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
                        large = "https://randomuser.me/api/portraits/men/$index.jpg",
                        medium = "https://randomuser.me/api/portraits/men/$index.jpg",
                        thumbnail = "https://randomuser.me/api/portraits/men/$index.jpg"
                    ),
                    nat = "US"
                )
            },
            isLoading = false,
            isSaving = false,
            error = null,
            saveSuccess = false,
            selectedGender = "female",
            selectedNationality = "CA",
            teamSize = 3,
            snackbarHostState = remember { SnackbarHostState() },
            onGenerateTeam = {},
            onSaveTeam = {},
            onGenderSelect = {},
            onNationalitySelect = {},
            onTeamSizeChange = {},
            onClearTeam = {}
        )
    }
}