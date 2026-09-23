package com.example.jifenapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * 主题入口。支持：
 * 1. 系统深色模式自动跟随
 * 2. Android 12+ dynamicColor（取系统壁纸色）
 * 3. 孩子详情页可局部覆盖 [LocalChildColor] 强调色
 */

// 子页面（孩子详情等）可通过此 CompositionLocal 局部覆盖主题强调色
val LocalChildColor = staticCompositionLocalOf<Color> {
    error("LocalChildColor not provided. Wrap your screen with childTheme().")
}

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    secondary = Secondary,
    onSecondary = Color.White,
    background = NeutralLight,
    surface = Color.White,
    error = PointNegative,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.Black,
    secondary = SecondaryDark,
    onSecondary = Color.Black,
    background = NeutralDark,
    surface = Color(0xFF22242A),
    error = PointNegativeDark,
    onError = Color.Black
)

@Composable
fun JifenTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JifenTypography,
        shapes = JifenShapes
    ) {
        // 默认不提供 child color，依赖 childTheme() 显式注入
        CompositionLocalProvider(LocalChildColor provides colorScheme.primary) {
            content()
        }
    }
}

/**
 * 在 Composable 作用域内覆盖 [LocalChildColor]，用于孩子详情页等需要以孩子色为主题的场景。
 *
 * 用法：
 * ```
 * childTheme(childColorHex) {
 *     // 这里 MaterialTheme.colorScheme.primary 仍是全局色，
 *     // 但可以用 LocalChildColor.current 取孩子色
 * }
 * ```
 */
@Composable
fun childTheme(
    childColorHex: String,
    content: @Composable () -> Unit
) {
    val childColor = runCatching {
        Color(android.graphics.Color.parseColor(childColorHex))
    }.getOrDefault(MaterialTheme.colorScheme.primary)

    CompositionLocalProvider(LocalChildColor provides childColor) {
        content()
    }
}
