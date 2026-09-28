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
    /**
     * 用于显示：(分类, 规则列表)。筛选生效时只返回该分类下的规则。
     *
     * 注意：data class body 的初始化块在所有构造参数赋值完之后才执行，
     * 所以这里访问 rules / categories / categoryFilter 是安全的。
     */
    val grouped: List<Pair<CategoryEntity?, List<RuleWithCategory>>>
        get() {
            // 1. 先按 categoryFilter 过滤
            val filtered: List<RuleWithCategory> =
                if (categoryFilter == null) {
                    rules
                } else {
                    rules.filter { it.rule.categoryId == categoryFilter }
                }
            // 2. 按 category 分组（key 是 CategoryEntity?，未分类的 rule 会归到 null key）
            val byCategory: Map<CategoryEntity?, List<RuleWithCategory>> =
                filtered.groupBy { it.category }

            // 3. 按分类 sortOrder 排序后展平
            val orderedCategories: List<CategoryEntity> =
                categories.sortedBy { it.sortOrder }
            val orderedPairs: MutableList<Pair<CategoryEntity?, List<RuleWithCategory>>> =
                mutableListOf()
            for (cat in orderedCategories) {
                val list = byCategory[cat]
                if (list != null && list.isNotEmpty()) {
                    orderedPairs.add(cat to list)
                }
            }
            // 4. 未分类（null key）放到末尾
            val uncat = byCategory[null].orEmpty()
            if (uncat.isNotEmpty()) {
                orderedPairs.add(null to uncat)
            }
            return orderedPairs
        }
}