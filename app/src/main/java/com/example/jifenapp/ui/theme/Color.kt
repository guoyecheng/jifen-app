package com.example.jifenapp.ui.theme

import androidx.compose.ui.graphics.Color

// ===== 主品牌色（蓝紫，亲子场景通用） =====
val Primary = Color(0xFF5B6EE1)
val PrimaryDark = Color(0xFF8B97F3)
val Secondary = Color(0xFFFF9F1C)
val SecondaryDark = Color(0xFFFFB951)

// ===== 积分正负色（统一规则：奖励绿、扣减红） =====
val PointPositive = Color(0xFF2E7D32)
val PointPositiveDark = Color(0xFF81C784)
val PointNegative = Color(0xFFC62828)
val PointNegativeDark = Color(0xFFEF5350)

// ===== 中性色 =====
val NeutralLight = Color(0xFFF7F7FB)
val NeutralDark = Color(0xFF1A1B1F)
val SurfaceVariantLight = Color(0xFFE7E8F0)
val SurfaceVariantDark = Color(0xFF2A2C32)

// ===== 孩子可选主题色（用于"我的孩子们"卡片顶条） =====
val ChildColorChoices: List<Color> = listOf(
    Color(0xFFFF9800), // 橙
    Color(0xFFE91E63), // 粉
    Color(0xFF4CAF50), // 绿
    Color(0xFF2196F3), // 蓝
    Color(0xFF9C27B0), // 紫
    Color(0xFFFFEB3B), // 黄
    Color(0xFF795548), // 棕
    Color(0xFF00BCD4), // 青
)

// ===== 孩子可选头像（emoji，简单） =====
val ChildAvatarChoices: List<String> = listOf(
    "🦁", "🐯", "🐻", "🐼", "🐨", "🦊",
    "🐰", "🐱", "🐶", "🐹", "🐸", "🦄",
    "🐧", "🐤", "🦉", "🐢", "🐳", "🦋",
    "🌟", "🌈", "🍎", "🍓", "⚽", "🎨"
)
