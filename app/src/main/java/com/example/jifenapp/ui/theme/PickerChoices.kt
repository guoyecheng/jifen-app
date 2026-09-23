package com.example.jifenapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 分类可选图标（emoji）。
 */
val CategoryIconOptions: List<String> = listOf(
    "📚", "✏️", "🎨", "🎵", "🏃", "⚽", "🍎", "🛏️",
    "🧹", "🪥", "🌱", "🚿", "📺", "🎮", "👕", "🐶",
    "🌟", "💪", "🧠", "🤝", "❤️", "⏰", "🛒", "✨"
)

/**
 * 分类可选主题色（与孩子色不同，饱和度更高，更"分类感"）。
 */
val CategoryColorOptions: List<Color> = listOf(
    Color(0xFF5B6EE1), // 蓝紫
    Color(0xFFE91E63), // 粉
    Color(0xFF4CAF50), // 绿
    Color(0xFFFF9800), // 橙
    Color(0xFF9C27B0), // 紫
    Color(0xFF00BCD4), // 青
    Color(0xFF795548), // 棕
    Color(0xFFF44336), // 红
    Color(0xFF3F51B5), // 靛
    Color(0xFF8BC34A), // 嫩绿
    Color(0xFFFFEB3B), // 黄
    Color(0xFF607D8B)  // 蓝灰
)

fun colorToHexString(color: Color): String {
    val argb = (color.value shr 32).toLong().toInt()
    return String.format("#%06X", 0xFFFFFF and argb)
}