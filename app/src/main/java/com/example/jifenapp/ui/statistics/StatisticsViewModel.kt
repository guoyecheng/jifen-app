package com.example.jifenapp.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jifenapp.data.local.entity.RecordType
import com.example.jifenapp.data.model.DailyPointTotal
import com.example.jifenapp.data.repository.ChildRepository
import com.example.jifenapp.data.repository.PointRecordRepository
import com.example.jifenapp.di.AppContainer
import com.example.jifenapp.util.TimeRange
import com.example.jifenapp.util.nowMillis
import com.example.jifenapp.util.startOfTodayMillis
import com.example.jifenapp.util.toEpochDay
import com.example.jifenapp.util.toLocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

/**
 * 统计页 ViewModel。
 *
 * - 监听孩子档案
 * - 监听 totalPoints（实时刷新）
 * - 根据 timeRange 切换区间，查询分类份额 / 按日趋势
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModel(
    private val childId: Long,
    private val childRepository: ChildRepository,
    private val recordRepository: PointRecordRepository
) : ViewModel() {

    private val timeRange = MutableStateFlow(TimeRange.THIS_WEEK)

    val uiState: StateFlow<StatisticsUiState> = combine(
        childRepository.observeById(childId),
        recordRepository.totalPoints(childId),
        timeRange
    ) { child, total, range ->
        Triple(child, total, range)
    }.flatMapLatest { (child, total, range) ->
        val (from, to) = range.resolve(nowMillis())
        combine(
            recordRepository.sumInRange(childId, from, to, RecordType.ADD),
            recordRepository.sumInRange(childId, from, to, RecordType.SUBTRACT),
            recordRepository.netInRange(childId, startOfTodayMillis(), nowMillis()),
            recordRepository.observeCategoryShare(childId, from, to),
            recordRepository.observeByChildInRange(childId, from, to)
        ) { add, sub, today, share, records ->
            val daily = records.groupBy { it.createdAt.toLocalDate().toEpochDay() }
                .toSortedMap()
                .map { (epochDay, list) ->
                    val (n, a, s) = list.fold(Triple(0, 0, 0)) { acc, r ->
                        when (r.type) {
                            RecordType.ADD -> Triple(acc.first + r.points, acc.second + r.points, acc.third)
                            RecordType.SUBTRACT -> Triple(acc.first - r.points, acc.second, acc.third + r.points)
                        }
                    }
                    DailyPointTotal(
                        dayEpochDay = epochDay,
                        netTotal = n,
                        addTotal = a,
                        subTotal = s,
                        count = list.size
                    )
                }
            StatisticsUiState(
                child = child,
                timeRange = range,
                totalPoints = total,
                rangeAdd = add,
                rangeSub = sub,
                todayDelta = today,
                categoryShare = share,
                dailyTrend = daily,
                loading = false
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatisticsUiState(loading = true)
    )

    fun setTimeRange(range: TimeRange) {
        timeRange.value = range
    }

    companion object {
        fun create(container: AppContainer, childId: Long): StatisticsViewModel =
            StatisticsViewModel(
                childId = childId,
                childRepository = container.childRepository,
                recordRepository = container.pointRecordRepository
            )
    }
}