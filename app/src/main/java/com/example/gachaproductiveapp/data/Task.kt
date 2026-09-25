package com.example.gachaproductiveapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskTier(val currencyReward: Int, val label: String) {
    EASY(10, "Easy"),
    MEDIUM(20, "Medium"),
    HARD(50, "Hard")
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val tier: TaskTier,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
