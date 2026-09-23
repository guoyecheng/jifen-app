package com.example.jifenapp.ui.record

import com.example.jifenapp.data.local.entity.ChildEntity
import com.example.jifenapp.data.local.entity.RecordType
import com.example.jifenapp.data.model.RuleWithCategory

/**
 * 添加流水页面状态。迭代 2 接入规则模板。
 */
data class AddRecordUiState(
    val child: ChildEntity? = null,
    val childTotalPoints: Int = 0,
    val type: RecordType = RecordType.ADD,
    val pointsInput: String = "",
    val title: String = "",
    val note: String = "",
    /** 当前选中的规则 id；null = 自由输入 */
    val selectedRuleId: Long? = null,
    /** 启用的规则列表（按分类分组展示） */
    val rules: List<RuleWithCategory> = emptyList(),
    val saving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
) {
    /** 输入框内的分数（绝对值），解析失败返回 null */
    val points: Int? get() = pointsInput.toIntOrNull()?.takeIf { it > 0 }

    /** 是否可以保存：标题 + 有效分数 */
    val canSave: Boolean get() = title.isNotBlank() && (points ?: 0) > 0 && !saving
}