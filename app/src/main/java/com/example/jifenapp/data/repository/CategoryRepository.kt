package com.example.jifenapp.data.repository

import com.example.jifenapp.data.local.dao.CategoryDao
import com.example.jifenapp.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val dao: CategoryDao) {
    fun observeAll(): Flow<List<CategoryEntity>> = dao.observeAll()
    suspend fun getById(id: Long): CategoryEntity? = dao.getById(id)
    suspend fun count(): Int = dao.count()
    suspend fun add(category: CategoryEntity): Long = dao.insert(category)
    suspend fun update(category: CategoryEntity) = dao.update(category)
    suspend fun delete(category: CategoryEntity) = dao.delete(category)
    suspend fun deleteById(id: Long) = dao.deleteById(id)
}