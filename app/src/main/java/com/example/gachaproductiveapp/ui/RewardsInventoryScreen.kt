package com.example.gachaproductiveapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gachaproductiveapp.data.Rarity
import com.example.gachaproductiveapp.data.Reward
import com.example.gachaproductiveapp.viewmodel.MainViewModel

@Composable
fun RewardsInventoryScreen(viewModel: MainViewModel) {
    val rewards by viewModel.rewards.collectAsState()
    val wonRewards = rewards.filter { it.timesWon > 0 }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("🎒 Rewards Inventory", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Tap any available reward to use 1x from your inventory:", style = MaterialTheme.typography.bodyMedium)

        if (wonRewards.isEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No rewards won yet!", style = MaterialTheme.typography.titleMedium)
                    Text("Complete tasks, earn Favor, and make a wish!", style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(wonRewards, key = { it.id }) { reward ->
                    InventoryRewardCard(
                        reward = reward,
                        onUse = { viewModel.useReward(reward) }
                    )
                }
            }
        }
    }
}

@Composable
private fun InventoryRewardCard(reward: Reward, onUse: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    val color = when (reward.rarity) {
        Rarity.FIVE_STAR -> androidx.compose.ui.graphics.Color(0xFFD69E2E)
        Rarity.FOUR_STAR -> androidx.compose.ui.graphics.Color(0xFF805AD5)
        Rarity.THREE_STAR -> androidx.compose.ui.graphics.Color(0xFF3182CE)
    }
    val stars = when (reward.rarity) {
        Rarity.FIVE_STAR -> "★★★★★"
        Rarity.FOUR_STAR -> "★★★★☆"
        Rarity.THREE_STAR -> "★★★☆☆"
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { showDialog = true }
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (reward.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = reward.imageUrl,
                    contentDescription = reward.name,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(stars, color = color, fontWeight = FontWeight.Bold)
                Text(reward.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (reward.description.isNotBlank()) {
                    Text(reward.description, style = MaterialTheme.typography.bodySmall)
                }
                
                val remainingText = if (reward.remainingCount > 0) {
                    "In Inventory: ${reward.remainingCount}x · Total Used: ${reward.timesUsed}x"
                } else {
                    "Out of Stock (Used all ${reward.timesUsed}x)"
                }
                
                Text(
                    remainingText,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (reward.remainingCount > 0) color else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showDialog) {
        if (reward.remainingCount > 0) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Use Reward?") },
                text = {
                    Text(
                        "Do you want to use 1x \"${reward.name}\"?\n\n" +
                        "Remaining in inventory: ${reward.remainingCount} -> ${reward.remainingCount - 1}"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onUse()
                            showDialog = false
                        }
                    ) {
                        Text("Yes, Use 1x")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("No Items Remaining") },
                text = {
                    Text(
                        "You have 0 \"${reward.name}\" left in your inventory.\n\n" +
                        "Complete tasks, earn Favor, and pull in the Wish Banner to win more!"
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Got it")
                    }
                }
            )
        }
    }
}
