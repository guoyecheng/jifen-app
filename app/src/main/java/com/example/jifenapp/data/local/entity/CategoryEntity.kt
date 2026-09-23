package com.example.jifenapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 行为分类：把规则按"学习/生活/运动"等分组。
 *
 * @property colorHex 主题色，未设置时规则使用自身颜色
 * @property sortOrder 列表显示顺序
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val colorHex: String,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)