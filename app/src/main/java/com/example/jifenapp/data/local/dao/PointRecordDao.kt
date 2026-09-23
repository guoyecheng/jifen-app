package com.example.jifenapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jifenapp.data.local.entity.PointRecordEntity
import com.example.jifenapp.data.local.entity.RecordType
import kotlinx.coroutines.flow.Flow

@Dao
interface PointRecordDao {

    /** 单孩子的全部流水（按时间倒序，最新在前） */
    @Query("""
        SELECT * FROM point_records
        WHERE childId = :childId
        ORDER BY createdAt DESC
    """)
    fun observeByChild(childId: Long): Flow<List<PointRecordEntity>>

    /** 当前积分：奖励之和 - 扣减之和。空表返回 0。 */
    @Query("""
        SELECT IFNULL(
            SUM(CASE WHEN type = 'ADD' THEN points ELSE -points END), 0
        ) FROM point_records WHERE childId = :childId
    """)
    fun totalPoints(childId: Long): Flow<Int>

    /** 时间区间内某类型的积分合计（用于"本周 +"、"本周 -" 等） */
    @Query("""
        SELECT IFNULL(SUM(points), 0)
        FROM point_records
        WHERE childId = :childId
          AND type = :type
          AND createdAt BETWEEN :fromMillis AND :toMillis
    """)
    fun sumInRange(childId: Long, fromMillis: Long, toMillis: Long, type: RecordType): Flow<Int>

    /** 时间区间内净增积分（正数表示净加，负数表示净减） */
    @Query("""
        SELECT IFNULL(
            SUM(CASE WHEN type = 'ADD' THEN points ELSE -points END), 0
        )
        FROM point_records
        WHERE childId = :childId
          AND createdAt BETWEEN :fromMillis AND :toMillis
    """)
    fun netInRange(childId: Long, fromMillis: Long, toMillis: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: PointRecordEntity): Long

    @Delete
    suspend fun delete(record: PointRecordEntity)

    @Query("DELETE FROM point_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
