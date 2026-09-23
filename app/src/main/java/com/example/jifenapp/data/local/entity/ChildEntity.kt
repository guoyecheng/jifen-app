package com.example.jifenapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 孩子档案。
 *
 * @property avatar  emoji 字符串（如 "🦁"），可选范围参考 [com.example.jifenapp.ui.theme.ChildAvatarChoices]
 * @property colorHex ARGB 十六进制字符串（如 "#FF9800"），用于卡片顶条 / 主题强调色
 * @property birthdayEpochDay 生日，距 epoch 的天数（kotlinx.datetime.LocalDate.toEpochDay()）；空表示未填
 */
@Entity(tableName = "children")
data class ChildEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val avatar: String,
    val colorHex: String,
    val birthdayEpochDay: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
