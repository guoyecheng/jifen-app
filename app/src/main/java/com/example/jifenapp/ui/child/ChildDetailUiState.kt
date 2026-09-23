package com.example.jifenapp.ui.child

import com.example.jifenapp.data.local.entity.ChildEntity
import com.example.jifenapp.data.local.entity.PointRecordEntity

/** 单孩子详情页 UI 状态。 */
data class ChildDetailUiState(
    val child: ChildEntity? = null,
    val records: List<PointRecordEntity> = emptyList(),
    val total: Int = 0,
    val weekAdd: Int = 0,
    val weekSub: Int = 0,
    val todayDelta: Int = 0,
    val loading: Boolean = true
)
