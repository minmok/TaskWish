package com.example.gachaproductiveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gachaproductiveapp.ui.CurrencyScreen
import com.example.gachaproductiveapp.ui.GachaScreen
import com.example.gachaproductiveapp.ui.RewardSetupScreen
import com.example.gachaproductiveapp.ui.RewardsInventoryScreen
import com.example.gachaproductiveapp.ui.TaskScreen
import com.example.gachaproductiveapp.ui.WelcomeScreen
import com.example.gachaproductiveapp.ui.components.GradientButton
import com.example.gachaproductiveapp.ui.theme.DarkBackground
import com.example.gachaproductiveapp.ui.theme.DarkSurface
import com.example.gachaproductiveapp.ui.theme.DarkSurfaceVariant
import com.example.gachaproductiveapp.ui.theme.GachaProductiveAppTheme
import com.example.gachaproductiveapp.ui.theme.GlassBorderColor
import com.example.gachaproductiveapp.ui.theme.PrimaryGold
import com.example.gachaproductiveapp.ui.theme.SecondaryPurple
import com.example.gachaproductiveapp.ui.theme.TextPrimary
import com.example.gachaproductiveapp.ui.theme.TextSecondary
import com.example.gachaproductiveapp.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GachaProductiveAppTheme {
                AppRootView()
            }
        }
    }
}

@Composable
fun AppRootView(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.userState.collectAsState()

    if (state == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryGold)
        }
    } else if (state!!.username.isBlank()) {
        WelcomeScreen(viewModel = viewModel, onEntered = {})
    } else {
        MainAppView(viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppView(viewModel: MainViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val userState by viewModel.userState.collectAsState()
    val dailyBonus by viewModel.dailyLoginBonus.collectAsState()

    val currency = userState?.currency ?: 0
    val streak = userState?.loginStreak ?: 0
    val username = userState?.username.takeIf { !it.isNullOrBlank() } ?: "Traveler"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                ),
                title = {
                    Column {
                        Text(
                            text = "TaskWish",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGold
                        )
                        Text(
                            text = "Welcome, $username",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }
                },
                actions = {
                    // Daily Streak Pill Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFEA580C).copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, Color(0xFFF97316).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = "Streak",
                                tint = Color(0xFFFB923C),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${streak}d",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDBA74)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                    // Favor Currency Pill Badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PrimaryGold.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, PrimaryGold.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Favor",
                                tint = PrimaryGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$currency",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Tasks") },
                    label = { Text("Tasks") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGold,
                        selectedTextColor = PrimaryGold,
                        indicatorColor = PrimaryGold.copy(alpha = 0.15f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Insights, contentDescription = "Favor") },
                    label = { Text("Dashboard") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGold,
                        selectedTextColor = PrimaryGold,
                        indicatorColor = PrimaryGold.copy(alpha = 0.15f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Wish") },
                    label = { Text("Wish") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SecondaryPurple,
                        selectedTextColor = SecondaryPurple,
                        indicatorColor = SecondaryPurple.copy(alpha = 0.15f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Inventory") },
                    label = { Text("Inventory") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGold,
                        selectedTextColor = PrimaryGold,
                        indicatorColor = PrimaryGold.copy(alpha = 0.15f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Tune, contentDescription = "Setup") },
                    label = { Text("Setup") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryGold,
                        selectedTextColor = PrimaryGold,
                        indicatorColor = PrimaryGold.copy(alpha = 0.15f),
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    0 -> TaskScreen(viewModel)
                    1 -> CurrencyScreen(viewModel)
                    2 -> GachaScreen(viewModel)
                    3 -> RewardsInventoryScreen(viewModel)
                    4 -> RewardSetupScreen(viewModel)
                }
            }
        }
    }

    // Daily login popup handling
    when (dailyBonus) {
        -1 -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDailyLoginBanner() },
                containerColor = DarkSurfaceVariant,
                titleContentColor = TextPrimary,
                textContentColor = TextSecondary,
                icon = {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFFB923C),
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        "Daily Login Streak!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "Claim your daily login bonus and earn extra Favor to wish for rewards!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    GradientButton(
                        text = "Claim Bonus",
                        onClick = { viewModel.claimDailyLogin() },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDailyLoginBanner() }) {
                        Text("Later", color = TextSecondary)
                    }
                }
            )
        }
        is Int -> {
            val bonus = dailyBonus ?: 0
            if (bonus > 0) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissDailyLoginBanner() },
                    containerColor = DarkSurfaceVariant,
                    titleContentColor = TextPrimary,
                    textContentColor = TextSecondary,
                    icon = {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PrimaryGold,
                            modifier = Modifier.size(36.dp)
                        )
                    },
                    title = {
                        Text(
                            "Bonus Claimed!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGold
                        )
                    },
                    text = {
                        Text(
                            "You earned +$bonus Favor for keeping up your daily streak! Keep up the great productivity!",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        GradientButton(
                            text = "Awesome!",
                            onClick = { viewModel.dismissDailyLoginBanner() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }
        }
    }
}
