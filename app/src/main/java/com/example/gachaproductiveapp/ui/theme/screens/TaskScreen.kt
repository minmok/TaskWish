package com.example.gachaproductiveapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import com.example.gachaproductiveapp.data.Task
import com.example.gachaproductiveapp.data.TaskTier
import com.example.gachaproductiveapp.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) { Text("+") }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header actions for clearing tasks
            if (tasks.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.deleteCompletedTasks() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear Completed")
                    }
                    OutlinedButton(
                        onClick = { viewModel.deleteAllTasks() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Delete All Tasks")
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskRow(
                        task = task,
                        onComplete = { viewModel.completeTask(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
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
private fun TaskRow(task: Task, onComplete: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${task.tier.name} \u00b7 +${task.tier.currencyReward} Favor",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!task.isCompleted) {
                    Button(onClick = onComplete) { Text("Done") }
                } else {
                    Text("\u2713 Completed", style = MaterialTheme.typography.bodySmall)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, TaskTier) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tier by remember { mutableStateOf(TaskTier.EASY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskTier.entries.forEach { t ->
                        FilterChip(selected = tier == t, onClick = { tier = t }, label = { Text(t.name) })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (title.isNotBlank()) onConfirm(title, description, tier) }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
