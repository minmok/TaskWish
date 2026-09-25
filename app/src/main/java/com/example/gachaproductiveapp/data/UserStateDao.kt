package com.example.gachaproductiveapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStateDao {
    @Query("SELECT * FROM user_state WHERE id = 0")
    fun observeState(): Flow<UserState?>

    @Query("SELECT * FROM user_state WHERE id = 0")
    suspend fun getState(): UserState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(state: UserState)
}