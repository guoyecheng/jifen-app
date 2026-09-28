package com.example.jifenapp.ui.statistics

import com.example.jifenapp.data.local.entity.ChildEntity
import com.example.jifenapp.data.model.CategoryShare
import com.example.jifenapp.data.model.DailyPointTotal
import com.example.jifenapp.util.TimeRange

/** 统计页 UI 状态。 */
data class StatisticsUiState(
    val child: ChildEntity? = null,
    val timeRange: TimeRange = TimeRange.THIS_WEEK,
    val totalPoints: Int = 0,
    val rangeAdd: Int = 0,
    val rangeSub: Int = 0,
    val todayDelta: Int = 0,
    val categoryShare: List<CategoryShare> = emptyList(),
    val dailyTrend: List<DailyPointTotal> = emptyList(),
    val loading: Boolean = true
)