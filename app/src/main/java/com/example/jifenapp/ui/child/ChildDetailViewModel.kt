package com.example.jifenapp.ui.child

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jifenapp.data.local.entity.PointRecordEntity
import com.example.jifenapp.data.local.entity.RecordType
import com.example.jifenapp.data.repository.ChildRepository
import com.example.jifenapp.data.repository.PointRecordRepository
import com.example.jifenapp.di.AppContainer
import com.example.jifenapp.util.startOfTodayMillis
import com.example.jifenapp.util.startOfWeekMillis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 单孩子详情 ViewModel：聚合档案、流水、本周+/本周-/今日净增。
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChildDetailViewModel(
    private val childId: Long,
    private val childRepository: ChildRepository,
    private val recordRepository: PointRecordRepository
) : ViewModel() {

    val uiState: StateFlow<ChildDetailUiState> = combine(
        childRepository.observeById(childId),
        recordRepository.observeByChild(childId),
        recordRepository.totalPoints(childId)
    ) { child, records, total ->
        val weekStart = startOfWeekMillis()
        val todayStart = startOfTodayMillis()
        val now = System.currentTimeMillis()

        val weekAdd = records.filter {
            it.createdAt in weekStart..now && it.type == RecordType.ADD
        }.sumOf { it.points }
        val weekSub = records.filter {
            it.createdAt in weekStart..now && it.type == RecordType.SUBTRACT
        }.sumOf { it.points }
        val todayDelta = records.filter {
            it.createdAt in todayStart..now
        }.sumOf {
            if (it.type == RecordType.ADD) it.points else -it.points
        }

        ChildDetailUiState(
            child = child,
            records = records,
            total = total,
            weekAdd = weekAdd,
            weekSub = weekSub,
            todayDelta = todayDelta,
            loading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChildDetailUiState(loading = true)
    )

    fun deleteRecord(record: PointRecordEntity) {
        viewModelScope.launch { recordRepository.delete(record) }
    }

    companion object {
        fun create(childId: Long): (AppContainer) -> ChildDetailViewModel = { container ->
            ChildDetailViewModel(
                childId = childId,
                childRepository = container.childRepository,
                recordRepository = container.pointRecordRepository
            )
        }
    }
}
