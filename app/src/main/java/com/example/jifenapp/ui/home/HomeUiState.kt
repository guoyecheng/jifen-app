package com.example.jifenapp.ui.home

import com.example.jifenapp.data.local.entity.ChildEntity
import com.example.jifenapp.data.model.ChildWithStats

/**
 * 首页 UI 状态：孩子列表 + 每个孩子的当前积分/本周加减。
 *
 * 一次性聚合加载；新增/删除孩子/新增流水时通过 Flow 自动刷新。
 */
data class HomeUiState(
    val children: List<ChildWithStats> = emptyList(),
    val loading: Boolean = true
) {
    val isEmpty: Boolean get() = !loading && children.isEmpty()
}
