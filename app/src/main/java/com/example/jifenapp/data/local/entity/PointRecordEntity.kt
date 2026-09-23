package com.example.jifenapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** 流水类型：奖励加分 / 扣减减分。 */
enum class RecordType { ADD, SUBTRACT }

/**
 * 一条积分流水（不可变历史记录）。
 *
 * 关键字段：
 * - [type] + [points]：最终积分 = type * points（+points / -points）
 * - [title]：模板的"快照"，规则改名/删除不影响历史显示
 * - [ruleId]：来源模板 id，可空（自由输入时为空）；删除模板后置 null（SET_NULL）
 *
 * 外键：
 * - 删除孩子 → 流水一起删（CASCADE）
 * - 删除模板 → 流水保留，ruleId 置 null
 *
 * 索引：`(childId, createdAt)` 复合索引，覆盖"单个孩子按时间范围"的最常见查询。
 */
@Entity(
    tableName = "point_records",
    foreignKeys = [
        ForeignKey(
            entity = ChildEntity::class,
            parentColumns = ["id"],
            childColumns = ["childId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("childId"),
        Index("createdAt"),
        Index(value = ["childId", "createdAt"])
    ]
)
data class PointRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val childId: Long,
    val type: RecordType,
    /** 绝对值（>= 1）。最终积分 = type == ADD ? points : -points */
    val points: Int,
    /** 模板名快照；自由输入时即用户填的标题 */
    val title: String,
    val note: String? = null,
    /** 预留字段，迭代 2 接入模板时启用。当前先不作为外键，避免引入未完成表 */
    val ruleId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
