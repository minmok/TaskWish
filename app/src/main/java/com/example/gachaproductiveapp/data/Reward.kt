package com.example.gachaproductiveapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Rarity { THREE_STAR, FOUR_STAR, FIVE_STAR }

@Entity(tableName = "rewards")
data class Reward(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val rarity: Rarity,
    val isFeatured: Boolean = false, // only meaningful for FIVE_STAR
    val timesWon: Int = 0,
    val imageUrl: String = ""
)