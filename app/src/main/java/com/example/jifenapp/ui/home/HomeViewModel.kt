package com.example.jifenapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jifenapp.data.local.entity.ChildEntity
import com.example.jifenapp.data.model.ChildWithStats
import com.example.jifenapp.data.repository.ChildRepository
import com.example.jifenapp.data.repository.PointRecordRepository
import com.example.jifenapp.di.AppContainer
import com.example.jifenapp.util.startOfWeekMillis
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 首页 ViewModel：
 * - 监听孩子列表
 * - 每个孩子并行监听 totalPoints / 本周+ / 本周-，聚合为 [ChildWithStats]
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val childRepository: ChildRepository,
    private val recordRepository: PointRecordRepository
) : ViewModel() {

    /**
     * 关键实现：先 observe 孩子列表，每个 childId 通过 flatMapLatest 转成
     * "ChildWithStats" 的小 Flow，再用 combine 汇成整体列表。
     */
    val uiState: StateFlow<HomeUiState> = childRepository.observeAll()
        .flatMapLatest { children ->
            if (children.isEmpty()) {
                flowOf(HomeUiState(children = emptyList(), loading = false))
            } else {
                val weekStart = startOfWeekMillis()
                val now = System.currentTimeMillis()
                // 每个 child 一个 Flow，combine 合并
                val perChildFlows = children.map { child ->
                    combine(
                        recordRepository.totalPoints(child.id),
                        recordRepository.sumInRange(child.id, weekStart, now, com.example.jifenapp.data.local.entity.RecordType.ADD),
                        recordRepository.sumInRange(child.id, weekStart, now, com.example.jifenapp.data.local.entity.RecordType.SUBTRACT)
                    ) { total, add, sub ->
                        ChildWithStats(child, total, add, sub)
                    }
                }
                combine(perChildFlows) { array -> HomeUiState(children = array.toList(), loading = false) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(loading = true)
        )

    fun addChild(name: String, avatar: String, colorHex: String) {
        viewModelScope.launch {
            childRepository.add(
                ChildEntity(name = name, avatar = avatar, colorHex = colorHex)
            )
        }
    }

    companion object {
        fun create(container: AppContainer): HomeViewModel = HomeViewModel(
            childRepository = container.childRepository,
            recordRepository = container.pointRecordRepository
        )
    }
}
