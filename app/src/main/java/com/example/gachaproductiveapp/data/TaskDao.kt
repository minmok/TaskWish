package com.example.gachaproductiveapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, createdAt DESC")
    fun observeTasks(): Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompleted()
}