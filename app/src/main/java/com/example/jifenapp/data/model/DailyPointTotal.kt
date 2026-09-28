package com.example.jifenapp.data.model

import androidx.room.ColumnInfo

/**
 * 一天内的积分统计（用于折线图）。
 *
 * @property dayEpochDay 本地日期的 epoch day（自 1970-01-01 起的天数）
 * @property netTotal 当日净增积分（正=净加，负=净减）
 * @property addTotal 当日奖励分
 * @property subTotal 当日扣分
 * @property count 当日流水条数
 */
data class DailyPointTotal(
    @ColumnInfo(name = "day_epoch_day") val dayEpochDay: Long,
    val netTotal: Int,
    val addTotal: Int,
    val subTotal: Int,
    val count: Int
)