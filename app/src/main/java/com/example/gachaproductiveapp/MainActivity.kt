package com.example.gachaproductiveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gachaproductiveapp.ui.CurrencyScreen
import com.example.gachaproductiveapp.ui.GachaScreen
import com.example.gachaproductiveapp.ui.RewardSetupScreen
import com.example.gachaproductiveapp.ui.RewardsInventoryScreen
import com.example.gachaproductiveapp.ui.TaskScreen
import com.example.gachaproductiveapp.ui.WelcomeScreen
import com.example.gachaproductiveapp.ui.theme.GachaProductiveAppTheme
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
                .background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFFD69E2E))
        }
    } else if (state!!.username.isBlank()) {
        WelcomeScreen(viewModel = viewModel, onEntered = {})
    } else {
        MainAppView(viewModel = viewModel)
    }
}

@Composable
fun MainAppView(viewModel: MainViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val dailyBonus by viewModel.dailyLoginBonus.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.List, contentDescription = "Tasks") },
                    label = { Text("Tasks") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Favor") },
                    label = { Text("Favor") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.ThumbUp, contentDescription = "Wish") },
                    label = { Text("Wish") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Inventory") },
                    label = { Text("Inventory") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Rewards") },
                    label = { Text("Setup") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> TaskScreen(viewModel)
                1 -> CurrencyScreen(viewModel)
                2 -> GachaScreen(viewModel)
                3 -> RewardsInventoryScreen(viewModel)
                4 -> RewardSetupScreen(viewModel)
            }
        }
    }

    // Daily login popup handling
    when (dailyBonus) {
        -1 -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissDailyLoginBanner() },
                title = { Text("Daily Login Bonus!") },
                text = { Text("Claim your daily login streak bonus and earn Favor!") },
                confirmButton = {
                    TextButton(onClick = { viewModel.claimDailyLogin() }) {
                        Text("Claim")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissDailyLoginBanner() }) {
                        Text("Later")
                    }
                }
            )
        }
        is Int -> {
            val bonus = dailyBonus ?: 0
            if (bonus > 0) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissDailyLoginBanner() },
                    title = { Text("Bonus Claimed!") },
                    text = { Text("You received +$bonus Favor from your daily login streak!") },
                    confirmButton = {
                        TextButton(onClick = { viewModel.dismissDailyLoginBanner() }) {
                            Text("Awesome")
                        }
                    }
                )
            }
        }
    }
}
