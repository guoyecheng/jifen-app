package com.example.jifenapp.data.repository

import com.example.jifenapp.data.local.dao.PointRecordDao
import com.example.jifenapp.data.local.entity.PointRecordEntity
import com.example.jifenapp.data.local.entity.RecordType
import com.example.jifenapp.data.model.CategoryShare
import kotlinx.coroutines.flow.Flow

/**
 * 流水的数据仓库。
 */
class PointRecordRepository(private val dao: PointRecordDao) {

    fun observeByChild(childId: Long): Flow<List<PointRecordEntity>> =
        dao.observeByChild(childId)

    fun totalPoints(childId: Long): Flow<Int> = dao.totalPoints(childId)

    fun sumInRange(
        childId: Long,
        fromMillis: Long,
        toMillis: Long,
        type: RecordType
    ): Flow<Int> = dao.sumInRange(childId, fromMillis, toMillis, type)

    fun netInRange(childId: Long, fromMillis: Long, toMillis: Long): Flow<Int> =
        dao.netInRange(childId, fromMillis, toMillis)

    // ===== 迭代 3 统计聚合 =====

    /** 时间区间内按分类聚合（饼图） */
    fun observeCategoryShare(
        childId: Long,
        fromMillis: Long,
        toMillis: Long
    ): Flow<List<CategoryShare>> = dao.observeCategoryShare(childId, fromMillis, toMillis)

    /** 时间区间内的全部流水（折线图 / 手动按 LocalDate 分组用） */
    fun observeByChildInRange(
        childId: Long,
        fromMillis: Long,
        toMillis: Long
    ): Flow<List<PointRecordEntity>> = dao.observeByChildInRange(childId, fromMillis, toMillis)

    suspend fun add(record: PointRecordEntity): Long = dao.insert(record)

    suspend fun delete(record: PointRecordEntity) = dao.delete(record)

    suspend fun deleteById(id: Long) = dao.deleteById(id)
}