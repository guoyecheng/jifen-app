package com.example.jifenapp.ui.rules

import com.example.jifenapp.data.local.entity.CategoryEntity
import com.example.jifenapp.data.model.RuleWithCategory

/**
 * 模板管理页 UI 状态。
 *
 * - [categoryFilter] 当前筛选的分类 id；null 表示"全部"
 * - [grouped] 已按 categoryId 聚合的 (category, rules) 列表，未分类放在末尾
 */
data class RulesUiState(
    val rules: List<RuleWithCategory> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val categoryFilter: Long? = null,
    val loading: Boolean = true
) {
    /** 用于显示：(分类, 规则列表)。筛选生效时只返回该分类下的规则。 */
    val grouped: List<Pair<CategoryEntity?, List<RuleWithCategory>>> = run {
        val filtered = if (categoryFilter == null) rules
        else rules.filter { it.rule.categoryId == categoryFilter }
        val byCategory = filtered.groupBy { it.category }
        // 排序：先按分类 sortOrder，再按规则 sortOrder
        val ordered = categories.sortedBy { it.sortOrder }.mapNotNull { c -> byCategory[c]?.let { c to it } }
        val uncat = byCategory[null].orEmpty()
        val result = ordered.toMutableList()
        if (uncat.isNotEmpty()) result.add(null to uncat)
        result
    }
}