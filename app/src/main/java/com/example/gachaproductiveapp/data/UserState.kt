package com.example.gachaproductiveapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_state")
data class UserState(
    @PrimaryKey val id: Int = 0, // singleton row, always id = 0
    val currency: Int = 0,
    val pity5Counter: Int = 0,
    val pity4Counter: Int = 0,
    val guaranteed5: Boolean = false,
    val lastLoginEpochDay: Long = -1,
    val loginStreak: Int = 0,
    val username: String = ""
)
