package com.example.gachaproductiveapp.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gachaproductiveapp.data.Rarity
import com.example.gachaproductiveapp.data.Reward
import com.example.gachaproductiveapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardSetupScreen(viewModel: MainViewModel) {
    val rewards by viewModel.rewards.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingReward by remember { mutableStateOf<Reward?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingReward = null
                showDialog = true
            }) { Text("+") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(Rarity.entries.toList()) { rarity ->
                Text(rarity.name, style = MaterialTheme.typography.titleMedium)
                rewards.filter { it.rarity == rarity }.forEach { reward ->
                    RewardRow(reward = reward, onClick = {
                        editingReward = reward
                        showDialog = true
                    })
                }
                Spacer(Modifier.height(8.dp))
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
            }
        )
    }
}

@Composable
private fun RewardRow(reward: Reward, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
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
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(reward.name + if (reward.isFeatured) " \u2b50 Featured" else "")
                if (reward.description.isNotBlank()) {
                    Text(reward.description, style = MaterialTheme.typography.bodySmall)
                }
                Text("Won ${reward.timesWon}x · Tap to edit", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditRewardDialog(
    rewardToEdit: Reward?,
    onDismiss: () -> Unit,
    onSave: (String, String, Rarity, Boolean, String) -> Unit
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
        title = { Text(if (rewardToEdit == null) "New reward" else "Edit reward") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Image URL or Gallery path") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Button(onClick = { galleryLauncher.launch("image/*") }) {
                        Text("Gallery")
                    }
                }

                if (imageUrl.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Rarity.entries.forEach { r ->
                        FilterChip(selected = rarity == r, onClick = { rarity = r }, label = { Text(r.name) })
                    }
                }
                if (rarity == Rarity.FIVE_STAR) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = featured, onCheckedChange = { featured = it })
                        Text("Set as current featured reward")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onSave(name, description, rarity, featured && rarity == Rarity.FIVE_STAR, imageUrl) }) {
                Text(if (rewardToEdit == null) "Add" else "Save")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
