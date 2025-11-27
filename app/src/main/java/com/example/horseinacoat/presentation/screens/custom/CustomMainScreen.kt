package com.example.horseinacoat.presentation.screens.custom

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.horseinacoat.R
import com.example.horseinacoat.presentation.navigation.NavigationRoutes
import com.example.horseinacoat.ui.theme.HorseInACoatTheme
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomMainScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Creative Mode",
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
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.horse),
                            contentDescription = "Horse in a Coat Logo",
                            modifier = Modifier
                                .padding(4.dp)
                                .size(150.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 3.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(12.dp)
                                )
                        )

                        Text(
                            text = "    Welcome to the Creative Corral! Where horses code and users gallop through data fields. \n" +
                                    "   Our algorithm has a peculiar sense of humor - it thinks everyone looks better in a coat. " +
                                    "Saddle up for some seriously stable connections!",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontStyle = FontStyle.Italic,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Text(
                        text = "    Don't worry, we won't put the cart before the horse... unless it's a feature! " +
                                "Ready to trot through some user data? Neigh problem! " +
                                "Our digital stable is full of fascinating characters waiting to be discovered. " +
                                "From coding cowboys to database dressage experts, we've got them all!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.5f)
                ) {
                    CircularButtonSpinner(
                        modifier = Modifier
                            .fillMaxSize(),
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun CircularButtonSpinner(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    var rotation by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(targetValue = rotation, label = "rotation")

    val buttons = listOf(
        ButtonData("All Users", "Browse complete user database collection", R.drawable.ic_db) {
            navController.navigate(NavigationRoutes.ALL_USERS_SCREEN)
        },
        ButtonData("Location Match", "Discover nearby users in area", R.drawable.ic_location) {
            navController.navigate(NavigationRoutes.LOCATION_MATCH_SCREEN)
        },
        ButtonData("Random Team", "Generate perfectly random teams", R.drawable.ic_group) {
            navController.navigate(NavigationRoutes.RANDOM_TEAM_SCREEN)
        },
        ButtonData("Statistics", "Database analytics and user insights", R.drawable.ic_analytics) {
            navController.navigate(NavigationRoutes.STATISTICS_SCREEN)
        },
        ButtonData("Resume", "View professional profile summary", R.drawable.ic_priz) {
            navController.navigate(NavigationRoutes.RESUME_SCREEN)
        }
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        val activeIndex = findTopButtonIndex(animatedRotation, buttons.size)

        Text(
            text = buttons[activeIndex].description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .offset(y = 100.dp)
                .align(Alignment.TopCenter)
        )

        Box(
            modifier = Modifier
                .size(450.dp)
                .offset(y = 200.dp)
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            rotation += dragAmount.x * 0.5f
                        },
                        onDragEnd = {
                            val targetAngle = findNearestTopAngle(rotation, buttons.size)
                            rotation = targetAngle
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            buttons.forEachIndexed { index, buttonData ->
                val angleDegrees = (index * (360f / buttons.size) + animatedRotation) % 360f
                val angleRadians = angleDegrees * (PI.toFloat() / 180f)

                val radius = 150.dp
                val x = (sin(angleRadians) * radius.value).dp
                val y = (-cos(angleRadians) * radius.value).dp

                val isActive = angleDegrees in 330f..360f || angleDegrees in 0f..30f
                val scale by animateFloatAsState(
                    targetValue = if (isActive) 1.4f else 0.8f,
                    label = "scale"
                )

                CircularSpinnerButton(
                    buttonData = buttonData,
                    modifier = Modifier
                        .offset(x = x, y = y)
                        .scale(scale),
                    isActive = isActive,
                    onClick = {
                        val targetAngle = -index * (360f / buttons.size)
                        rotation = targetAngle
                    }
                )
            }
        }

        Text(
            text = "Drag to explore the full circle • Top position selects",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
fun CircularSpinnerButton(
    buttonData: ButtonData,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        IconButton(
            onClick = buttonData.onClick ?: onClick,
            modifier = Modifier
                .size(if (isActive) 90.dp else 75.dp)
                .background(
                    color = if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = buttonData.iconRes),
                contentDescription = buttonData.title,
                tint = if (isActive) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(if (isActive) 36.dp else 30.dp)
            )
        }

        if (isActive) {
            Text(
                text = buttonData.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun findTopButtonIndex(rotation: Float, buttonCount: Int): Int {
    val normalizedRotation = (rotation % 360f).let { if (it < 0) it + 360f else it }

    var minDiff = 360f
    var closestIndex = 0

    for (i in 0 until buttonCount) {
        val buttonAngle = (i * (360f / buttonCount) + normalizedRotation) % 360f
        val normalizedButtonAngle = if (buttonAngle < 0) buttonAngle + 360f else buttonAngle
        val diff = minOf(
            abs(normalizedButtonAngle - 0f),
            abs(normalizedButtonAngle - 360f)
        )

        if (diff < minDiff) {
            minDiff = diff
            closestIndex = i
        }
    }

    return closestIndex
}

private fun findNearestTopAngle(currentRotation: Float, buttonCount: Int): Float {
    val anglePerButton = 360f / buttonCount

    val closestIndex = findTopButtonIndex(currentRotation, buttonCount)

    val targetAngle = -closestIndex * anglePerButton

    return targetAngle
}

data class ButtonData(
    val title: String,
    val description: String,
    val iconRes: Int,
    val onClick: (() -> Unit)? = null
)


@Preview(name = "Custom Main - Day", showBackground = true)
@Composable
fun CustomMainScreenDayPreview() {
    HorseInACoatTheme(darkTheme = false) {
        CustomMainScreen(navController = rememberNavController())
    }
}

@Preview(name = "Custom Main - Night", showBackground = true)
@Composable
fun CustomMainScreenNightPreview() {
    HorseInACoatTheme(darkTheme = true) {
        CustomMainScreen(navController = rememberNavController())
    }
}