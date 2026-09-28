package com.example.jifenapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * 分类维度的积分汇总（用于饼图）。
 *
 * 注解了 [@Entity] 是为了让 Room 在 KSP 阶段能识别这个 POJO，
 * 把 cursor 列映射到字段。**不** 加入 [@Database] 的 entities 列表，
 * 所以 Room 不会尝试创建这张表，它实际是个 view-model。
 *
 * @property categoryId 分类 id，未分类为 null
 * @property categoryName 分类名，未分类显示为"未分类"
 * @property colorHex 主题色，未分类用灰色
 * @property total 净积分（正=净奖励，负=净惩罚）
 * @property count 该分类下的流水条数
 */
@Entity(tableName = "category_share_view")
data class CategoryShare(
    @ColumnInfo(name = "category_id") val categoryId: Long?,
    val categoryName: String,
    /** 与 categories 表同名字段（colorHex, 无下划线）；DAO SELECT 列表名就是 colorHex */
    val colorHex: String,
    val total: Int,
    val count: Int
)