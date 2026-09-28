package com.example.gachaproductiveapp.ui

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.gachaproductiveapp.data.Task
import com.example.gachaproductiveapp.data.TaskTier
import com.example.gachaproductiveapp.ui.components.EmptyStateView
import com.example.gachaproductiveapp.ui.components.GlassCard
import com.example.gachaproductiveapp.ui.components.GradientButton
import com.example.gachaproductiveapp.ui.components.SectionHeader
import com.example.gachaproductiveapp.ui.components.TierBadge
import com.example.gachaproductiveapp.ui.theme.DarkBackground
import com.example.gachaproductiveapp.ui.theme.DarkSurface
import com.example.gachaproductiveapp.ui.theme.DarkSurfaceVariant
import com.example.gachaproductiveapp.ui.theme.PrimaryGold
import com.example.gachaproductiveapp.ui.theme.TextMuted
import com.example.gachaproductiveapp.ui.theme.TextPrimary
import com.example.gachaproductiveapp.ui.theme.TextSecondary
import com.example.gachaproductiveapp.ui.theme.TierEasyColor
import com.example.gachaproductiveapp.ui.theme.TierHardColor
import com.example.gachaproductiveapp.ui.theme.TierMediumColor
import com.example.gachaproductiveapp.viewmodel.MainViewModel

@Composable
fun TaskScreen(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    var selectedFilterIndex by remember { mutableIntStateOf(0) } // 0: All, 1: Pending, 2: Completed
    var showAddDialog by remember { mutableStateOf(false) }
    var showClearCompletedDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    val filteredTasks = when (selectedFilterIndex) {
        1 -> tasks.filter { !it.isCompleted }
        2 -> tasks.filter { it.isCompleted }
        else -> tasks
    }

    val pendingCount = tasks.count { !it.isCompleted }
    val completedCount = tasks.count { it.isCompleted }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PrimaryGold,
                contentColor = Color.Black,
                shape = RoundedCornerShape(18.dp),
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Task") },
                text = { Text("New Task", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header & Clear Actions
            SectionHeader(
                title = "Tasks",
                subtitle = "$pendingCount pending · $completedCount completed",
                action = {
                    if (tasks.isNotEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (completedCount > 0) {
                                TextButton(onClick = { showClearCompletedDialog = true }) {
                                    Text("Clear Done", color = TextSecondary, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                            TextButton(onClick = { showDeleteAllDialog = true }) {
                                Text("Clear All", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            )

            // Filter Chips Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilterIndex == 0,
                    onClick = { selectedFilterIndex = 0 },
                    label = { Text("All (${tasks.size})") },
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
                    label = { Text("Pending ($pendingCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGold.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryGold,
                        containerColor = DarkSurfaceVariant,
                        labelColor = TextSecondary
                    )
                )
                FilterChip(
                    selected = selectedFilterIndex == 2,
                    onClick = { selectedFilterIndex = 2 },
                    label = { Text("Completed ($completedCount)") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGold.copy(alpha = 0.2f),
                        selectedLabelColor = PrimaryGold,
                        containerColor = DarkSurfaceVariant,
                        labelColor = TextSecondary
                    )
                )
            }

            // Task List or Empty State
            if (filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        icon = Icons.Default.CheckCircle,
                        title = when (selectedFilterIndex) {
                            1 -> "No Pending Tasks!"
                            2 -> "No Completed Tasks Yet"
                            else -> "No Tasks Created"
                        },
                        description = when (selectedFilterIndex) {
                            1 -> "Great job! All your planned tasks are complete."
                            2 -> "Complete tasks to earn Favor and pull wish rewards!"
                            else -> "Add tasks with difficulty tiers to earn Favor and unlock rewards."
                        },
                        actionButtonText = if (selectedFilterIndex == 0) "Add First Task" else null,
                        onActionClick = { showAddDialog = true }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredTasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onComplete = { viewModel.completeTask(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }

    if (showClearCompletedDialog) {
        AlertDialog(
            onDismissRequest = { showClearCompletedDialog = false },
            containerColor = DarkSurfaceVariant,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Clear Completed Tasks?") },
            text = { Text("Are you sure you want to remove all completed tasks from your list?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCompletedTasks()
                        showClearCompletedDialog = false
                    }
                ) { Text("Clear Completed", color = PrimaryGold, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showClearCompletedDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            containerColor = DarkSurfaceVariant,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Delete All Tasks?") },
            text = { Text("Are you sure you want to delete ALL tasks from your list? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllTasks()
                        showDeleteAllDialog = false
                    }
                ) { Text("Delete All", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, tier ->
                viewModel.addTask(title, desc, tier)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val tierColor = when (task.tier) {
        TaskTier.EASY -> TierEasyColor
        TaskTier.MEDIUM -> TierMediumColor
        TaskTier.HARD -> TierHardColor
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (task.isCompleted) DarkSurface.copy(alpha = 0.6f) else DarkSurface,
        borderColor = if (task.isCompleted) Color.Transparent else tierColor.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Difficulty Tier Accent Strip
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(84.dp)
                    .background(if (task.isCompleted) TextMuted else tierColor)
            )

            Row(
                modifier = Modifier
                    .padding(14.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Completion Toggle Icon
                IconButton(
                    onClick = { if (!task.isCompleted) onComplete() },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Toggle Complete",
                        tint = if (task.isCompleted) PrimaryGold else TextSecondary
                    )
                }

                // Title & Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (task.isCompleted) TextMuted else TextPrimary,
                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )

                    if (task.description.isNotBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    Spacer(Modifier.height(6.dp))
                    TierBadge(tier = task.tier)
                }

                // Right Action (Delete)
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Task",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, TaskTier) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedTier by remember { mutableStateOf(TaskTier.EASY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceVariant,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = { Text("New Task", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
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
                    label = { Text("Description (Optional)") },
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

                Text("Select Difficulty Tier", style = MaterialTheme.typography.labelMedium, color = TextSecondary)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TaskTier.entries.forEach { tier ->
                        val isSelected = selectedTier == tier
                        val color = when (tier) {
                            TaskTier.EASY -> TierEasyColor
                            TaskTier.MEDIUM -> TierMediumColor
                            TaskTier.HARD -> TierHardColor
                        }

                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedTier = tier },
                            label = { Text("${tier.label}\n+${tier.currencyReward}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color.copy(alpha = 0.25f),
                                selectedLabelColor = color,
                                containerColor = DarkBackground,
                                labelColor = TextSecondary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            GradientButton(
                text = "Add Task",
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), description.trim(), selectedTier)
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
