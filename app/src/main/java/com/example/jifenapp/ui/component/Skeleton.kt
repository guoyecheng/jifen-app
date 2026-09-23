package com.example.jifenapp.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 骨架屏占位：流动的渐变条。
 *
 * 用法：
 * ```
 * if (loading) SkeletonBlock(width = 160.dp, height = 24.dp)
 * ```
 */
@Composable
fun SkeletonBlock(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp = 16.dp,
    cornerRadius: Dp = 8.dp
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeleton-translate"
    )

    val baseColor = MaterialTheme.colorScheme.surfaceVariant
    val highlightColor = MaterialTheme.colorScheme.surface
    val gradient = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translate - 200f, 0f),
        end = Offset(translate, 0f)
    )

    Box(
        modifier = modifier
            .let { if (width != null) it.width(width) else it }
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(gradient)
    )
}

/**
 * 圆形骨架点（用于头像占位）
 */
@Composable
fun SkeletonCircle(size: Dp, modifier: Modifier = Modifier) {
    SkeletonBlock(
        modifier = modifier.size(size),
        height = size,
        cornerRadius = size / 2
    )
}

/**
 * 首页卡片骨架
 */
@Composable
fun HomeCardSkeleton(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        SkeletonBlock(
            modifier = Modifier.fillMaxSize().height(8.dp),
            width = null,
            cornerRadius = 0.dp
        )
        SkeletonCircle(size = 64.dp, modifier = Modifier.padding(top = 12.dp))
        SkeletonBlock(modifier = Modifier.padding(top = 12.dp), width = 80.dp, height = 16.dp)
        SkeletonBlock(modifier = Modifier.padding(top = 12.dp), width = 60.dp, height = 28.dp)
        SkeletonBlock(modifier = Modifier.padding(top = 4.dp), width = 30.dp, height = 12.dp)
        SkeletonBlock(modifier = Modifier.padding(top = 12.dp), width = 100.dp, height = 12.dp)
    }
}
