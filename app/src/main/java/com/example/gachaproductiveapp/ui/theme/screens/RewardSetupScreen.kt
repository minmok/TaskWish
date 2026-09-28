package com.example.gachaproductiveapp.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gachaproductiveapp.data.Rarity
import com.example.gachaproductiveapp.data.Reward
import com.example.gachaproductiveapp.ui.components.GlassCard
import com.example.gachaproductiveapp.ui.components.GradientButton
import com.example.gachaproductiveapp.ui.components.RarityBadge
import com.example.gachaproductiveapp.ui.components.SectionHeader
import com.example.gachaproductiveapp.ui.theme.DarkBackground
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardSetupScreen(viewModel: MainViewModel) {
    val rewards by viewModel.rewards.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingReward by remember { mutableStateOf<Reward?>(null) }
    var rewardToDelete by remember { mutableStateOf<Reward?>(null) }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingReward = null
                    showDialog = true
                },
                containerColor = PrimaryGold,
                contentColor = Color.Black,
                shape = RoundedCornerShape(18.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Reward") },
                text = { Text("New Reward", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader(
                    title = "Reward Pool Setup",
                    subtitle = "Customize rewards for 5★, 4★, and 3★ wish pulls"
                )
            }

            items(Rarity.entries.toList()) { rarity ->
                val rarityRewards = rewards.filter { it.rarity == rarity }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RarityBadge(rarity = rarity)
                        Text(
                            text = "${rarityRewards.size} configured",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    if (rarityRewards.isEmpty()) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = DarkSurfaceVariant
                        ) {
                            Text(
                                text = "No ${rarity.name} rewards configured yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        rarityRewards.forEach { reward ->
                            RewardRow(
                                reward = reward,
                                onClick = {
                                    editingReward = reward
                                    showDialog = true
                                },
                                onDelete = {
                                    rewardToDelete = reward
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddEditRewardDialog(
            rewardToEdit = editingReward,
            onDismiss = {
                showDialog = false
                editingReward = null
            },
            onSave = { name, desc, rarity, featured, imageUrl ->
                if (editingReward != null) {
                    viewModel.updateReward(
                        editingReward!!.copy(
                            name = name,
                            description = desc,
                            rarity = rarity,
                            isFeatured = featured,
                            imageUrl = imageUrl
                        )
                    )
                } else {
                    viewModel.addReward(name, desc, rarity, featured, imageUrl)
                }
                showDialog = false
                editingReward = null
            },
            onDelete = {
                if (editingReward != null) {
                    rewardToDelete = editingReward
                    showDialog = false
                    editingReward = null
                }
            }
        )
    }

    if (rewardToDelete != null) {
        AlertDialog(
            onDismissRequest = { rewardToDelete = null },
            containerColor = DarkSurfaceVariant,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Delete Reward?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete \"${rewardToDelete?.name}\"?") },
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
private fun RewardRow(
    reward: Reward,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val rarityColor = when (reward.rarity) {
        Rarity.FIVE_STAR -> Rarity5Star
        Rarity.FOUR_STAR -> Rarity4Star
        Rarity.THREE_STAR -> Rarity3Star
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        backgroundColor = DarkSurface,
        borderColor = rarityColor.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (reward.imageUrl.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
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
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = rarityColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = reward.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    if (reward.isFeatured) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PrimaryGold.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = PrimaryGold,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "Featured",
                                    color = PrimaryGold,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                if (reward.description.isNotBlank()) {
                    Text(
                        text = reward.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Text(
                    text = "Won ${reward.timesWon}x · Tap to edit",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                )
            }

            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditRewardDialog(
    rewardToEdit: Reward?,
    onDismiss: () -> Unit,
    onSave: (String, String, Rarity, Boolean, String) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(rewardToEdit?.name ?: "") }
    var description by remember { mutableStateOf(rewardToEdit?.description ?: "") }
    var imageUrl by remember { mutableStateOf(rewardToEdit?.imageUrl ?: "") }
    var rarity by remember { mutableStateOf(rewardToEdit?.rarity ?: Rarity.THREE_STAR) }
    var featured by remember { mutableStateOf(rewardToEdit?.isFeatured ?: false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUrl = it.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceVariant,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = if (rewardToEdit == null) "New Reward" else "Edit Reward",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Reward Name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGold,
                        unfocusedBorderColor = TextMuted,
                        focusedLabelColor = PrimaryGold,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGold,
                        unfocusedBorderColor = TextMuted,
                        focusedLabelColor = PrimaryGold,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Image URL or Path") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGold,
                            unfocusedBorderColor = TextMuted,
                            focusedLabelColor = PrimaryGold,
                            unfocusedLabelColor = TextSecondary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Gallery",
                            tint = PrimaryGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (imageUrl.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Text("Select Rarity Level", style = MaterialTheme.typography.labelMedium, color = TextSecondary)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Rarity.entries.forEach { r ->
                        val color = when (r) {
                            Rarity.FIVE_STAR -> Rarity5Star
                            Rarity.FOUR_STAR -> Rarity4Star
                            Rarity.THREE_STAR -> Rarity3Star
                        }

                        FilterChip(
                            selected = rarity == r,
                            onClick = { rarity = r },
                            label = { Text(r.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.25f),
                                selectedLabelColor = color,
                                containerColor = DarkBackground,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                if (rarity == Rarity.FIVE_STAR) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Checkbox(
                            checked = featured,
                            onCheckedChange = { featured = it },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryGold)
                        )
                        Text(
                            text = "Set as Featured Banner Reward (5★)",
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        },
        confirmButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (rewardToEdit != null) {
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete Reward", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
                GradientButton(
                    text = if (rewardToEdit == null) "Add Reward" else "Save Changes",
                    onClick = {
                        if (name.isNotBlank()) {
                            onSave(name.trim(), description.trim(), rarity, featured && rarity == Rarity.FIVE_STAR, imageUrl.trim())
                        }
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
