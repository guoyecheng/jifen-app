package com.example.jifenapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size as boxSize

/**
 * 孩子头像：圆圈 + emoji。可选 size 决定尺寸。
 */
@Composable
fun ChildAvatar(
    avatar: String,
    colorHex: String,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    emojiSizeSp: Int = 36
) {
    val backgroundColor = remember(colorHex) {
        runCatching { Color(android.graphics.Color.parseColor(colorHex)) }
            .getOrDefault(MaterialTheme.colorScheme.primary)
    }
    Box(
        modifier = modifier
            .boxSize(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = avatar,
            fontSize = emojiSizeSp.sp,
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}
