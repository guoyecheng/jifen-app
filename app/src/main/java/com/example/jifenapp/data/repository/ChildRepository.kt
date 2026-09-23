package com.example.jifenapp.data.repository

import com.example.jifenapp.data.local.dao.ChildDao
import com.example.jifenapp.data.local.entity.ChildEntity
import kotlinx.coroutines.flow.Flow

/**
 * 孩子的数据仓库：薄封装 DAO，方便未来注入缓存/同步等横切关注点。
 */
class ChildRepository(private val dao: ChildDao) {

    fun observeAll(): Flow<List<ChildEntity>> = dao.observeAll()

    fun observeById(id: Long): Flow<ChildEntity?> = dao.observeById(id)

    suspend fun getById(id: Long): ChildEntity? = dao.getById(id)

    suspend fun add(child: ChildEntity): Long = dao.insert(child)

    suspend fun update(child: ChildEntity) = dao.update(child)

    suspend fun delete(child: ChildEntity) = dao.delete(child)

    suspend fun count(): Int = dao.count()
}
