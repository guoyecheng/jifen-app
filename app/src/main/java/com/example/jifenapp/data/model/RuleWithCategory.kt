package com.example.jifenapp.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.example.jifenapp.data.local.entity.CategoryEntity
import com.example.jifenapp.data.local.entity.PointRuleEntity

/**
 * 规则 + 所属分类。模板展示时同时拿到分类名/颜色/图标。
 */
data class RuleWithCategory(
    @Embedded val rule: PointRuleEntity,
    @Relation(parentColumn = "categoryId", entityColumn = "id")
    val category: CategoryEntity?
)