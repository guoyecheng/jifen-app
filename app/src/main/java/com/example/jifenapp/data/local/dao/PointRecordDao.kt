package com.example.jifenapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jifenapp.data.local.entity.PointRecordEntity
import com.example.jifenapp.data.local.entity.RecordType
import com.example.jifenapp.data.model.CategoryShare
import com.example.jifenapp.data.model.DailyPointTotal
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

    // ===== 统计聚合（迭代 3） =====

    /**
     * 时间区间内按分类聚合的积分占比。
     *
     * 通过 point_records.ruleId 关联 point_rules 和 categories；
     * 自由输入或规则被删的记录归到"未分类"。
     */
    @Query("""
        SELECT
            p.categoryId AS category_id,
            COALESCE(c.name, '未分类') AS categoryName,
            COALESCE(c.colorHex, '#9E9E9E') AS colorHex,
            IFNULL(
                SUM(CASE WHEN r.type = 'ADD' THEN r.points ELSE -r.points END), 0
            ) AS total,
            COUNT(r.id) AS count
        FROM point_records r
        LEFT JOIN point_rules p ON r.ruleId = p.id
        LEFT JOIN categories c ON p.categoryId = c.id
        WHERE r.childId = :childId
          AND r.createdAt BETWEEN :fromMillis AND :toMillis
        GROUP BY p.categoryId, c.name, c.colorHex
        ORDER BY total DESC
    """)
    fun observeCategoryShare(
        childId: Long,
        fromMillis: Long,
        toMillis: Long
    ): Flow<List<CategoryShare>>

    /**
     * 按天聚合的积分（按本地日期分组，使用系统时区）。
     *
     * 实现要点：把 createdAt 毫秒除以一天的毫秒数，得到的"整数天"
     * 近似 UTC 日历日；Kotlin 侧再按本地时区校正到 LocalDate。
     * 为避免时区精度问题，DAO 直接返回原始 createdAt，Kotlin 端
     * 通过 observeByChildInRange 自行按 LocalDate 分组。
     */
    @Query("""
        SELECT * FROM point_records
        WHERE childId = :childId
          AND createdAt BETWEEN :fromMillis AND :toMillis
        ORDER BY createdAt ASC
    """)
    fun observeByChildInRange(
        childId: Long,
        fromMillis: Long,
        toMillis: Long
    ): Flow<List<PointRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: PointRecordEntity): Long

    @Delete
    suspend fun delete(record: PointRecordEntity)

    @Query("DELETE FROM point_records WHERE id = :id")
    suspend fun deleteById(id: Long)
}