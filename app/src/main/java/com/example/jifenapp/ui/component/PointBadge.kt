package com.example.jifenapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.jifenapp.ui.theme.PointNegative
import com.example.jifenapp.ui.theme.PointPositive
import com.example.jifenapp.ui.theme.PointNegativeDark
import com.example.jifenapp.ui.theme.PointPositiveDark
import androidx.compose.foundation.isSystemInDarkTheme

/**
 * 积分徽章：根据正负自动染色。
 *
 * @param points 正数绿、负数红、0 中性
 */
@Composable
fun PointBadge(
    points: Int,
    modifier: Modifier = Modifier,
    withSign: Boolean = true,
    backgroundAlpha: Float = 0.15f
) {
    val dark = isSystemInDarkTheme()
    val color = when {
        points > 0 -> if (dark) PointPositiveDark else PointPositive
        points < 0 -> if (dark) PointNegativeDark else PointNegative
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val text = when {
        points > 0 && withSign -> "+$points"
        points < 0 && withSign -> "$points"
        else -> "0"
    }
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = backgroundAlpha))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

/**
 * 仅文字积分（无背景），用于卡片大字显示。
 */
@Composable
fun PointText(
    points: Int,
    modifier: Modifier = Modifier,
    withSign: Boolean = true,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.displaySmall
) {
    val dark = isSystemInDarkTheme()
    val color = when {
        points > 0 -> if (dark) PointPositiveDark else PointPositive
        points < 0 -> if (dark) PointNegativeDark else PointNegative
        else -> MaterialTheme.colorScheme.onSurface
    }
    val text = when {
        points > 0 && withSign -> "+$points"
        else -> "$points"
    }
    Text(text = text, style = style, color = color, modifier = modifier)
}
