package com.example.jifenapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 行为模板：一键记流水的快捷入口。
 *
 * @property points 正=奖励加分，负=惩罚扣分（绝对值用于显示，最终积分 = type * points）
 * @property categoryId 所属分类，删除分类时置 null（SET_NULL）
 * @property icon 图标 key，为空时回退到分类图标
 * @property colorHex 主题色，覆盖分类颜色
 * @property enabled 软删除/停用开关
 */
@Entity(
    tableName = "point_rules",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId"), Index("enabled")]
)
data class PointRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val points: Int,
    val categoryId: Long?,
    val icon: String = "",
    val colorHex: String? = null,
    val sortOrder: Int = 0,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)