package com.example.jifenapp.data.repository

import com.example.jifenapp.data.local.dao.PointRuleDao
import com.example.jifenapp.data.local.entity.PointRuleEntity
import com.example.jifenapp.data.model.RuleWithCategory
import kotlinx.coroutines.flow.Flow

class PointRuleRepository(private val dao: PointRuleDao) {
    fun observeEnabled(): Flow<List<RuleWithCategory>> = dao.observeEnabledWithCategory()
    fun observeAll(): Flow<List<RuleWithCategory>> = dao.observeAllWithCategory()
    suspend fun getById(id: Long): PointRuleEntity? = dao.getById(id)
    suspend fun add(rule: PointRuleEntity): Long = dao.insert(rule)
    suspend fun update(rule: PointRuleEntity) = dao.update(rule)
    suspend fun delete(rule: PointRuleEntity) = dao.delete(rule)
    suspend fun deleteById(id: Long) = dao.deleteById(id)
    suspend fun setEnabled(id: Long, enabled: Boolean) = dao.setEnabled(id, enabled)
}