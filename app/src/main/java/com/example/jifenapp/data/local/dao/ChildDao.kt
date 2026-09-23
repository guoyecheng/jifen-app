package com.example.jifenapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jifenapp.data.local.entity.ChildEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildDao {

    @Query("SELECT * FROM children ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<ChildEntity>>

    @Query("SELECT * FROM children WHERE id = :id")
    fun observeById(id: Long): Flow<ChildEntity?>

    @Query("SELECT * FROM children WHERE id = :id")
    suspend fun getById(id: Long): ChildEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(child: ChildEntity): Long

    @Update
    suspend fun update(child: ChildEntity)

    @Delete
    suspend fun delete(child: ChildEntity)

    @Query("SELECT COUNT(*) FROM children")
    suspend fun count(): Int
}
