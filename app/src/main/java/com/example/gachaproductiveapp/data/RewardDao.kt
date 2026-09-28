package com.example.gachaproductiveapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards ORDER BY rarity DESC, id ASC")
    fun observeAll(): Flow<List<Reward>>

    @Insert
    suspend fun insert(reward: Reward)

    @Update
    suspend fun update(reward: Reward)

    @Delete
    suspend fun delete(reward: Reward)

    @Query("SELECT * FROM rewards WHERE rarity = :rarity")
    suspend fun getByRarity(rarity: Rarity): List<Reward>

    @Query("SELECT * FROM rewards WHERE rarity = 'FIVE_STAR' AND isFeatured = 1 LIMIT 1")
    suspend fun getFeaturedFiveStar(): Reward?

    @Query("SELECT * FROM rewards WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Reward?
}
