package com.example.jifenapp.ui.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jifenapp.data.model.DailyPointTotal
import com.example.jifenapp.ui.theme.PointNegative
import com.example.jifenapp.ui.theme.PointPositive

/**
 * 自绘折线图：0 轴虚线 + 正负分段着色 + 端点圆点 + 极值标签。
 *
 * 自绘原因：Vico 2.x API 较复杂；本应用统计场景简单，自绘足够。
 */
@Composable
fun TrendChart(
    daily: List<DailyPointTotal>,
    modifier: Modifier = Modifier
) {
    if (daily.isEmpty() || daily.all { it.netTotal == 0 }) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "该时间范围内还没有数据",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val isDark = isSystemInDarkTheme()
    val labelColor = if (isDark) Color(0xFFB0B0B0) else Color(0xFF666666)

    Column(modifier = modifier) {
        Text(
            text = "积分趋势",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val w = size.width
            val h = size.height
            val padLeft = 40f
            val padRight = 8f
            val padTop = 8f
            val padBottom = 8f

            val maxAbs = daily.maxOf { kotlin.math.abs(it.netTotal) }.coerceAtLeast(1)
            val zeroY = padTop + (h - padTop - padBottom) / 2f
            val chartW = w - padLeft - padRight
            val chartH = h - padTop - padBottom

            // 0 轴虚线
            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(padLeft, zeroY),
                end = Offset(w - padRight, zeroY),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )

            if (daily.size < 2) {
                // 仅 1 个数据点：画端点 + 标签
                val d = daily.first()
                val color = if (d.netTotal >= 0) PointPositive else PointNegative
                val cx = padLeft + chartW / 2f
                drawCircle(color, radius = 6f, center = Offset(cx, zeroY))
            } else {
                // 把数据映射到像素点
                val pts: List<Offset> = daily.mapIndexed { i, d ->
                    val x = padLeft + chartW * (i.toFloat() / (daily.size - 1).toFloat())
                    val y = zeroY - chartH / 2f * (d.netTotal.toFloat() / maxAbs.toFloat())
                    Offset(x, y)
                }

                // 折线段：根据相邻两点的 net 正负选颜色
                for (i in 0 until pts.size - 1) {
                    val mid = daily[i].netTotal + daily[i + 1].netTotal
                    val color = if (mid >= 0) PointPositive else PointNegative
                    drawLine(
                        color = color,
                        start = pts[i],
                        end = pts[i + 1],
                        strokeWidth = 3f
                    )
                }

                // 端点圆 + 白色描边
                pts.forEachIndexed { i, p ->
                    val d = daily[i]
                    val color = if (d.netTotal >= 0) PointPositive else PointNegative
                    drawCircle(color, radius = 5f, center = p)
                    drawCircle(Color.White, radius = 2f, center = p)
                }
            }

            // 极值标签（用原生 Paint 直接画文字）
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    color = labelColor.toArgb()
                    textSize = 22f
                    isAntiAlias = true
                }
                drawText("+$maxAbs", 0f, padTop + 16f, paint)
                drawText("0", 4f, zeroY + 8f, paint)
                drawText("-$maxAbs", 4f, h - 4f, paint)
            }
        }

        // 日期范围标注
        Text(
            text = "${daily.first().dayEpochDay.formatShortDate()} ~ ${daily.last().dayEpochDay.formatShortDate()}",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textAlign = TextAlign.End
        )
    }
}

private fun Long.formatShortDate(): String {
    // epoch day (1970-01-01 = 0) → yyyy-MM-dd 粗略换算（不考虑闰年，足够显示）
    val year = 1970 + (this / 365).toInt()
    val dayOfYear = (this % 365).toInt() + 1
    val month = ((dayOfYear - 1) / 30 + 1).coerceIn(1, 12)
    val day = ((dayOfYear - 1) % 30 + 1).coerceIn(1, 31)
    return String.format("%04d-%02d-%02d", year, month, day)
}

private fun Color.toArgb(): Int = android.graphics.Color.argb(
    (alpha * 255).toInt(),
    (red * 255).toInt(),
    (green * 255).toInt(),
    (blue * 255).toInt()
)