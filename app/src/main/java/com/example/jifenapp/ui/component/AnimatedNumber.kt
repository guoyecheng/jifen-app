package com.example.jifenapp.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.jifenapp.ui.theme.PointNegative
import com.example.jifenapp.ui.theme.PointNegativeDark
import com.example.jifenapp.ui.theme.PointPositive
import com.example.jifenapp.ui.theme.PointPositiveDark
import androidx.compose.foundation.isSystemInDarkTheme

/**
 * 数字变化时带淡入动画。
 *
 * 当积分改变时，旧数字淡出，新数字从下方滑入，避免突兀跳变。
 *
 * @param showSign true 时正数前加 + 号
 */
@Composable
fun AnimatedNumber(
    value: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displaySmall,
    showSign: Boolean = false
) {
    val dark = isSystemInDarkTheme()
    val color = when {
        value > 0 -> if (dark) PointPositiveDark else PointPositive
        value < 0 -> if (dark) PointNegativeDark else PointNegative
        else -> MaterialTheme.colorScheme.onSurface
    }
    val formatted = remember(value, showSign) {
        if (showSign && value > 0) "+$value" else "$value"
    }

    AnimatedContent(
        targetState = value,
        transitionSpec = {
            (slideInVertically(animationSpec = tween(220)) { fullHeight -> fullHeight } + fadeIn(animationSpec = tween(220)))
                .togetherWith(slideOutVertically(animationSpec = tween(220)) { fullHeight -> -fullHeight } + fadeOut(animationSpec = tween(220)))
        },
        label = "AnimatedNumber"
    ) {
        Text(
            text = formatted,
            style = style,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = modifier
        )
    }
}
