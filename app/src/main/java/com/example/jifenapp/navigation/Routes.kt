package com.example.jifenapp.navigation

/**
 * 单 Activity + Compose Navigation 的路由表。
 *
 * 每个路由用 sealed class 表示，参数在路由路径中显式声明，
 * 通过 NavController 的 backStackEntry.arguments 取值。
 */
sealed class Routes(val route: String) {
    /** 首页：孩子卡片网格 */
    data object Home : Routes("home")

    /** 孩子详情：单个孩子的流水列表 */
    data object ChildDetail : Routes("child/{childId}") {
        const val ARG_CHILD_ID = "childId"
        fun build(childId: Long): String = "child/$childId"
    }

    /** 记一笔：添加流水 */
    data object AddRecord : Routes("record/{childId}") {
        const val ARG_CHILD_ID = "childId"
        fun build(childId: Long): String = "record/$childId"
    }
}
