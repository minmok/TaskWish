package com.example.gachaproductiveapp.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.gachaproductiveapp.ui.components.EmptyStateView
import com.example.gachaproductiveapp.ui.components.GlassCard
import com.example.gachaproductiveapp.ui.components.GradientButton
import com.example.gachaproductiveapp.ui.components.RarityBadge
import com.example.gachaproductiveapp.ui.components.SectionHeader
import com.example.gachaproductiveapp.ui.theme.AccentEmerald
import com.example.gachaproductiveapp.ui.theme.DarkSurface
import com.example.gachaproductiveapp.ui.theme.DarkSurfaceVariant
import com.example.gachaproductiveapp.ui.theme.PrimaryGold
import com.example.gachaproductiveapp.ui.theme.Rarity3Star
import com.example.gachaproductiveapp.ui.theme.Rarity4Star
import com.example.gachaproductiveapp.ui.theme.Rarity5Star
import com.example.gachaproductiveapp.ui.theme.TextMuted
import com.example.gachaproductiveapp.ui.theme.TextPrimary
import com.example.gachaproductiveapp.ui.theme.TextSecondary
import com.example.gachaproductiveapp.viewmodel.MainViewModel

@Composable
fun RewardsInventoryScreen(viewModel: MainViewModel) {
    val rewards by viewModel.rewards.collectAsState()
    val wonRewards = rewards.filter { it.timesWon > 0 }
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: All, 1: 5★, 2: 4★, 3: 3★
    var rewardToDelete by remember { mutableStateOf<Reward?>(null) }

    val filteredRewards = when (selectedFilterIndex) {
        1 -> wonRewards.filter { it.rarity == Rarity.FIVE_STAR }
        2 -> wonRewards.filter { it.rarity == Rarity.FOUR_STAR }
        3 -> wonRewards.filter { it.rarity == Rarity.THREE_STAR }
        else -> wonRewards
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionHeader(
            title = "Rewards Inventory",
            subtitle = "Tap any available reward to use one copy"
        )

        // Rarity Filter Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilterIndex == 0,
                onClick = { selectedFilterIndex = 0 },
                label = { Text("All (${wonRewards.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryGold.copy(alpha = 0.2f),
                    selectedLabelColor = PrimaryGold,
                    containerColor = DarkSurfaceVariant,
                    labelColor = TextSecondary
                )
            )
            FilterChip(
                selected = selectedFilterIndex == 1,
                onClick = { selectedFilterIndex = 1 },
                label = { Text("5★ SSR") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Rarity5Star.copy(alpha = 0.2f),
                    selectedLabelColor = Rarity5Star,
                    containerColor = DarkSurfaceVariant,
                    labelColor = TextSecondary
                )
            )
            FilterChip(
                selected = selectedFilterIndex == 2,
                onClick = { selectedFilterIndex = 2 },
                label = { Text("4★ SR") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Rarity4Star.copy(alpha = 0.2f),
                    selectedLabelColor = Rarity4Star,
                    containerColor = DarkSurfaceVariant,
                    labelColor = TextSecondary
                )
            )
            FilterChip(
                selected = selectedFilterIndex == 3,
                onClick = { selectedFilterIndex = 3 },
                label = { Text("3★ R") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Rarity3Star.copy(alpha = 0.2f),
                    selectedLabelColor = Rarity3Star,
                    containerColor = DarkSurfaceVariant,
                    labelColor = TextSecondary
                )
            )
        }

        if (filteredRewards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateView(
                    icon = Icons.Default.Inventory2,
                    title = "No Inventory Items",
                    description = "Complete tasks to earn Favor and pull wish rewards to fill your inventory."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredRewards, key = { it.id }) { reward ->
                    InventoryRewardCard(
                        reward = reward,
                        onUse = { viewModel.useReward(reward) },
                        onDelete = { rewardToDelete = reward }
                    )
                }
            }
        }
    }

    if (rewardToDelete != null) {
        AlertDialog(
            onDismissRequest = { rewardToDelete = null },
            containerColor = DarkSurfaceVariant,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Delete Reward?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete \"${rewardToDelete?.name}\" from your inventory?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        rewardToDelete?.let { viewModel.deleteReward(it) }
                        rewardToDelete = null
                    }
                ) { Text("Yes, Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { rewardToDelete = null }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun InventoryRewardCard(
    reward: Reward,
    onUse: () -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val rarityColor = when (reward.rarity) {
        Rarity.FIVE_STAR -> Rarity5Star
        Rarity.FOUR_STAR -> Rarity4Star
        Rarity.THREE_STAR -> Rarity3Star
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        backgroundColor = DarkSurface,
        borderColor = rarityColor.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (reward.imageUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = reward.imageUrl,
                        contentDescription = reward.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = rarityColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                RarityBadge(rarity = reward.rarity)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = reward.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                if (reward.description.isNotBlank()) {
                    Text(
                        text = reward.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Spacer(Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (reward.remainingCount > 0) AccentEmerald.copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (reward.remainingCount > 0) "In Inventory: ${reward.remainingCount}x" else "Out of Stock",
                            color = if (reward.remainingCount > 0) AccentEmerald else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = "Used: ${reward.timesUsed}x",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Reward",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }
        }
    }

    if (showDialog) {
        if (reward.remainingCount > 0) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                containerColor = DarkSurfaceVariant,
                titleContentColor = TextPrimary,
                textContentColor = TextSecondary,
                title = { Text("Use Reward?", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Are you ready to claim and use 1x copy of \"${reward.name}\"?")
                        Text(
                            "Remaining quantity: ${reward.remainingCount} → ${reward.remainingCount - 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                confirmButton = {
                    GradientButton(
                        text = "Use 1x Item",
                        onClick = {
                            onUse()
                            showDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                containerColor = DarkSurfaceVariant,
                titleContentColor = TextPrimary,
                textContentColor = TextSecondary,
                title = { Text("Out of Stock") },
                text = {
                    Text("You currently have 0 copies of \"${reward.name}\" remaining in your inventory.\n\nEarn Favor and wish on the Wish tab to win more copies!")
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Got it", color = PrimaryGold, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
