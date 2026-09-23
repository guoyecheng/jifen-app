package com.example.jifenapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.jifenapp.data.local.entity.PointRuleEntity
import com.example.jifenapp.data.model.RuleWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface PointRuleDao {

    /** 全部启用的规则，按分类与排序字段 */
    @Transaction
    @Query("""
        SELECT * FROM point_rules
        WHERE enabled = 1
        ORDER BY categoryId ASC, sortOrder ASC, createdAt ASC
    """)
    fun observeEnabledWithCategory(): Flow<List<RuleWithCategory>>

    /** 全部规则（含已停用），管理页用 */
    @Transaction
    @Query("""
        SELECT * FROM point_rules
        ORDER BY categoryId ASC, sortOrder ASC, createdAt ASC
    """)
    fun observeAllWithCategory(): Flow<List<RuleWithCategory>>

    @Query("SELECT * FROM point_rules WHERE id = :id")
    suspend fun getById(id: Long): PointRuleEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(rule: PointRuleEntity): Long

    @Update
    suspend fun update(rule: PointRuleEntity)

    @Delete
    suspend fun delete(rule: PointRuleEntity)

    @Query("DELETE FROM point_rules WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE point_rules SET enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)
}