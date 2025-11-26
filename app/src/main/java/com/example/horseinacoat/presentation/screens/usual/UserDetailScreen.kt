package com.example.horseinacoat.presentation.screens.usual

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.room.util.TableInfo
import coil.compose.AsyncImage
import com.example.horseinacoat.R
import com.example.horseinacoat.domain.model.User
import com.example.horseinacoat.domain.model.secondary.Location
import com.example.horseinacoat.domain.model.secondary.Name
import com.example.horseinacoat.domain.model.secondary.Picture
import com.example.horseinacoat.presentation.viewModel.UserDetailViewModel
import com.example.horseinacoat.ui.theme.HorseInACoatTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    navController: NavController,
    userId: String,
    viewModel: UserDetailViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Детали пользователя",
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
                            contentDescription = "Назад",
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
            when {
                isLoading -> {
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
                            text = "Загрузка...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = error ?: "Ошибка загрузки",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                user != null -> {
                    UserDetailContent(user = user!!)
                }
                else -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Пользователь не найден",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserDetailContent(user: User) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
        }
        Column(
            modifier = Modifier
                .fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .offset(y = (-80).dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    AsyncImage(
                        model = user.picture.large,
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                clip = false
                            )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Привет, меня зовут",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${user.name.first} ${user.name.last}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset(y = (-80).dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
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
                            .height(60.dp)
                    ) {
                        TabButton(
                            icon = R.drawable.ic_user,
                            isSelected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            modifier = Modifier.weight(1f),
                            isFirst = true,
                            isLast = false
                        )

                        TabButton(
                            icon = R.drawable.ic_phone,
                            isSelected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            modifier = Modifier.weight(1f),
                            isFirst = false,
                            isLast = false
                        )

                        TabButton(
                            icon = R.drawable.ic_mail,
                            isSelected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            modifier = Modifier.weight(1f),
                            isFirst = false,
                            isLast = false
                        )

                        TabButton(
                            icon = R.drawable.ic_loc,
                            isSelected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            modifier = Modifier.weight(1f),
                            isFirst = false,
                            isLast = true
                        )
                    }

                    when (selectedTab) {
                        0 -> ProfileContent(user)
                        1 -> PhoneContent(user)
                        2 -> EmailContent(user)
                        3 -> LocationContent(user)
                    }
                }
            }
        }
    }
}


@Composable
fun TabButton(
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    val shape = when {
        isFirst -> RoundedCornerShape(topStart = 24.dp)
        isLast -> RoundedCornerShape(topEnd = 24.dp)
        else -> RoundedCornerShape(0.dp)
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(
                color = backgroundColor,
                shape = shape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = iconColor
        )
    }
}

@Composable
fun ProfileContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        UserInfoSection(
            title = "Основная информация",
            items = listOf(
                InfoItem(R.drawable.ic_user, "Имя", "${user.name.first} ${user.name.last}"),
                InfoItem(R.drawable.ic_user, "Пол", if (user.gender == "male") "Мужской" else "Женский"),
                InfoItem(R.drawable.ic_user, "Национальность", user.nat)
            )
        )
    }
}

@Composable
fun PhoneContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        UserInfoSection(
            title = "Телефонные номера",
            items = listOf(
                InfoItem(R.drawable.ic_back, "Основной телефон", user.phone),
                InfoItem(R.drawable.ic_back, "Мобильный телефон", user.cell)
            )
        )
    }
}

@Composable
fun EmailContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        UserInfoSection(
            title = "Электронная почта",
            items = listOf(
                InfoItem(R.drawable.ic_back, "Email адрес", user.email)
            )
        )
    }
}

@Composable
fun LocationContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        UserInfoSection(
            title = "Местоположение",
            items = listOf(
                InfoItem(R.drawable.ic_back, "Адрес", "${user.location.street.number} ${user.location.street.name}"),
                InfoItem(R.drawable.ic_back, "Город", user.location.city),
                InfoItem(R.drawable.ic_back, "Регион", user.location.state),
                InfoItem(R.drawable.ic_back, "Страна", user.location.country),
                InfoItem(R.drawable.ic_back, "Почтовый индекс", user.location.postcode.toString())
            )
        )
    }
}

@Composable
fun UserInfoSection(
    title: String,
    items: List<InfoItem>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items.forEach { item ->
                UserInfoRow(
                    icon = item.icon,
                    label = item.label,
                    value = item.value
                )
            }
        }
    }
}

@Composable
fun UserInfoRow(
    icon: Int,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .padding(top = 2.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

data class InfoItem(
    val icon: Int,
    val label: String,
    val value: String
)

enum class UserDetailTab(
    val title: String,
    val icon: Int
) {
    PROFILE("Профиль", R.drawable.ic_user),
    PHONE("Телефон", R.drawable.ic_phone),
    EMAIL("Почта", R.drawable.ic_mail),
    LOCATION("Адрес", R.drawable.ic_loc)
}

@Preview(name = "User Detail", showBackground = true)
@Composable
fun UserDetailPreview() {
    HorseInACoatTheme(darkTheme = false) {
        UserDetailContent(
            user = User(
                id = "1",
                gender = "male",
                name = Name(
                    first = "Александр",
                    last = "Иванов",
                    title = "Mr"
                ),
                location = Location(
                    street = com.example.horseinacoat.domain.model.secondary.Street(
                        number = 123,
                        name = "Ленина"
                    ),
                    city = "Москва",
                    state = "Московская область",
                    country = "Россия",
                    postcode = "101000"
                ),
                email = "alexander.ivanov@example.com",
                phone = "+7-495-123-45-67",
                cell = "+7-916-123-45-67",
                picture = Picture(
                    large = "https://randomuser.me/api/portraits/men/1.jpg",
                    medium = "https://randomuser.me/api/portraits/men/1.jpg",
                    thumbnail = "https://randomuser.me/api/portraits/men/1.jpg"
                ),
                nat = "RU"
            )
        )
    }
}