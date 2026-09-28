package com.example.jifenapp.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * 时间工具。所有"今天"、"本周"、"本月"判定基于系统本地时区。
 */
private val zone: TimeZone get() = TimeZone.currentSystemDefault()

/** 当前时刻。 */
fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()

/** epochMillis → Instant */
fun Long.toInstant(): Instant = Instant.fromEpochMilliseconds(this)

/** epochMillis → 本地 LocalDate */
fun Long.toLocalDate(): LocalDate =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(zone).date

/** epochMillis → 本地 LocalDateTime */
fun Long.toLocalDateTime(): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(zone)

/** epochMillis → 今天 00:00:00.000 的 epochMillis */
fun startOfTodayMillis(): Long =
    Clock.System.now().toLocalDateTime(zone).date
        .atStartOfDayIn(zone).toEpochMilliseconds()

/** 本周一 00:00 的 epochMillis（周一作为一周第一天）。 */
fun startOfWeekMillis(today: LocalDate = Clock.System.now().toLocalDateTime(zone).date): Long {
    // 直接用 epoch days 算术，避免 kotlinx.datetime.DateTimeUnit.DAY 的访问路径歧义。
    // 注意：kotlinx.datetime.LocalDate.toEpochDays() 返回 Long，而
    // LocalDate.fromEpochDays(Int) 接 Int，所以差值要先 .toInt()。
    val monday = LocalDate.fromEpochDays(today.toEpochDays().toInt() - today.dayOfWeek.ordinal)
    return monday.atStartOfDayIn(zone).toEpochMilliseconds()
}

/** 本月第一天 00:00 的 epochMillis。 */
fun startOfMonthMillis(today: LocalDate = Clock.System.now().toLocalDateTime(zone).date): Long {
    val firstDay = LocalDate(today.year, today.monthNumber, 1)
    return firstDay.atStartOfDayIn(zone).toEpochMilliseconds()
}

/** N 天前 00:00 的 epochMillis（含今天）。 */
fun startOfDaysAgoMillis(days: Int): Long {
    val today = Clock.System.now().toLocalDateTime(zone).date
        .atStartOfDayIn(zone).toEpochMilliseconds()
    return today - days * 24L * 60 * 60 * 1000
}

/** 把 epochMillis 格式化为 "HH:mm" */
fun Long.toTimeString(): String {
    val ldt = toLocalDateTime()
    val time = ldt.toJavaLocalDate().atStartOfDay(java.time.ZoneId.systemDefault())
        .toLocalDate()
        .atTime(ldt.hour, ldt.minute)
    return time.format(DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()))
}

/** 把 epochMillis 格式化为 "MM-dd HH:mm" */
fun Long.toDateTimeString(): String {
    val ldt = toLocalDateTime()
    val javaLdt = java.time.LocalDateTime.of(ldt.year, ldt.monthNumber, ldt.dayOfMonth, ldt.hour, ldt.minute)
    return javaLdt.format(DateTimeFormatter.ofPattern("MM-dd HH:mm", Locale.getDefault()))
}

/** 把 epochMillis 格式化为 "yyyy-MM-dd"（用于按日分组头） */
fun Long.toDateString(): String {
    val ldt = toLocalDateTime()
    val date = ldt.toJavaLocalDate()
    return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()))
}

/** 友好的相对时间描述：刚刚 / N 分钟前 / N 小时前 / N 天前 / yyyy-MM-dd */
fun Long.toFriendlyTime(): String {
    val diffMs = nowMillis() - this
    val diffMin = diffMs / 60_000
    val diffHour = diffMs / 3_600_000
    val diffDay = diffMs / (24 * 3_600_000L)
    return when {
        diffMin < 1 -> "刚刚"
        diffMin < 60 -> "${diffMin} 分钟前"
        diffHour < 24 -> "${diffHour} 小时前"
        diffDay < 7 -> "${diffDay} 天前"
        else -> toDateString()
    }
}

/** LocalDate → epochDay */
fun LocalDate.toEpochDay(): Long = this.toEpochDays().toLong()

/** epochDay → LocalDate */
fun Long.toLocalDateFromEpochDay(): LocalDate = LocalDate.fromEpochDays(this.toInt())

/** 星期几的中文描述 */
val DayOfWeek.cnName: String
    get() = when (this) {
        DayOfWeek.MONDAY -> "周一"
        DayOfWeek.TUESDAY -> "周二"
        DayOfWeek.WEDNESDAY -> "周三"
        DayOfWeek.THURSDAY -> "周四"
        DayOfWeek.FRIDAY -> "周五"
        DayOfWeek.SATURDAY -> "周六"
        DayOfWeek.SUNDAY -> "周日"
    }
