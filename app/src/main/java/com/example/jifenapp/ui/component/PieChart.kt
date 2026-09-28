package com.example.jifenapp.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jifenapp.data.model.CategoryShare

/**
 * 自绘饼图：用 Compose Canvas 画圆弧 + 图例。
 *
 * 每块扇区的弧长正比于 |share.total|（绝对值），负数（净扣分）也按份额画。
 * 没有数据时显示空状态文案。
 */
@Composable
fun PieChart(
    slices: List<CategoryShare>,
    modifier: Modifier = Modifier,
    strokeDp: Float = 28f
) {
    // data: 计算总和与每块占比
    val totalAbs = remember(slices) { slices.sumOf { kotlin.math.abs(it.total) }.coerceAtLeast(1) }

    if (slices.isEmpty() || slices.all { it.total == 0 }) {
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

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 饼图本体
            Box(modifier = Modifier.size(180.dp), contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(180.dp)) {
                    val strokeWidth = strokeDp.dp.toPx()
                    val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                    val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

                    var startAngle = -90f  // 从 12 点钟方向开始
                    slices.forEach { slice ->
                        val fraction = kotlin.math.abs(slice.total).toFloat() / totalAbs
                        if (fraction <= 0f) return@forEach
                        val sweep = fraction * 360f
                        val color = runCatching {
                            Color(android.graphics.Color.parseColor(slice.colorHex))
                        }.getOrDefault(MaterialTheme.colorScheme.primary.toArgb().toLong().let { Color(it.toInt()) })

                        drawArc(
                            color = color,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                        startAngle += sweep
                    }
                }
                // 中心显示总数
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalAbs",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "总积分",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.size(16.dp))

            // 图例
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                slices.forEach { slice ->
                    LegendRow(slice = slice, totalAbs = totalAbs)
                }
            }
        }
    }
}

@Composable
private fun LegendRow(slice: CategoryShare, totalAbs: Int) {
    val color = runCatching {
        Color(android.graphics.Color.parseColor(slice.colorHex))
    }.getOrDefault(MaterialTheme.colorScheme.primary)

    val fraction = if (totalAbs > 0)
        (kotlin.math.abs(slice.total).toFloat() / totalAbs * 100f).toInt()
    else 0

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "${slice.categoryName}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = "${if (slice.total > 0) "+" else ""}${slice.total}",
            style = MaterialTheme.typography.labelSmall,
            color = if (slice.total >= 0) com.example.jifenapp.ui.theme.PointPositive
            else com.example.jifenapp.ui.theme.PointNegative,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.size(4.dp))
        Text(
            text = "$fraction%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Color 转 Int 辅助
private fun Color.toArgb(): Int = android.graphics.Color.argb(
    (alpha * 255).toInt(),
    (red * 255).toInt(),
    (green * 255).toInt(),
    (blue * 255).toInt()
)