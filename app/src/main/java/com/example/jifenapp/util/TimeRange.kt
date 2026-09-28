package com.example.jifenapp.util

/**
 * 统计页用的时间范围。
 *
 * `fromMillis` / `toMillis` 是 epoch millis；`label` 显示中文。
 */
enum class TimeRange(
    val label: String,
    val fromMillis: Long,
    val toMillis: Long
) {
    TODAY("今日", 0L, 0L),               // 由 ViewModel 在观察时动态算
    THIS_WEEK("本周", 0L, 0L),
    THIS_MONTH("本月", 0L, 0L),
    LAST_7_DAYS("近 7 天", 0L, 0L),
    LAST_30_DAYS("近 30 天", 0L, 0L),
    ALL("全部", 0L, Long.MAX_VALUE);

    /** 解析为 (fromMillis, toMillis)。0L 占位代表"调用方按需计算"。 */
    fun resolve(nowMillis: Long): Pair<Long, Long> = when (this) {
        TODAY -> startOfTodayMillis() to nowMillis
        THIS_WEEK -> startOfWeekMillis() to nowMillis
        THIS_MONTH -> startOfMonthMillis() to nowMillis
        LAST_7_DAYS -> startOfDaysAgoMillis(7) to nowMillis
        LAST_30_DAYS -> startOfDaysAgoMillis(30) to nowMillis
        ALL -> 0L to Long.MAX_VALUE
    }
}