package com.example.gachaproductiveapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gachaproductiveapp.logic.GachaEngine
import com.example.gachaproductiveapp.viewmodel.MainViewModel

@Composable
fun CurrencyScreen(viewModel: MainViewModel) {
    val state by viewModel.userState.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    val totalFavor = state?.currency ?: 0
    val availablePulls = totalFavor / GachaEngine.PULL_COST

    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val completionRate = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks) * 100f else 0f

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Favor & Progress", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("User: ${state?.username.takeIf { !it.isNullOrBlank() } ?: "Traveler"}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Total Favor: $totalFavor", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Available Pulls: $availablePulls (Cost: ${GachaEngine.PULL_COST} Favor/pull)", style = MaterialTheme.typography.bodyLarge)
            }
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Productivity Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Total Tasks: $totalTasks")
                Text("Completed Tasks: $completedTasks")
                Text("Completion Rate: %.1f%%".format(completionRate), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text("Daily Streak: ${state?.loginStreak ?: 0} days")
            }
        }
    }
}
