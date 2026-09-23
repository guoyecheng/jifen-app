package com.example.jifenapp.data.model

import com.example.jifenapp.data.local.entity.ChildEntity

/**
 * 孩子 + 关键统计：用于首页卡片展示。
 *
 * @property total 当前积分
 * @property weekAdd 本周获得
 * @property weekSub 本周扣除
 */
data class ChildWithStats(
    val child: ChildEntity,
    val total: Int,
    val weekAdd: Int,
    val weekSub: Int
)
