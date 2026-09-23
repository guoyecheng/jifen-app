package com.example.jifenapp.ui.record

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jifenapp.data.local.entity.PointRuleEntity
import com.example.jifenapp.data.model.RuleWithCategory
import com.example.jifenapp.ui.theme.PointNegative
import com.example.jifenapp.ui.theme.PointPositive

/**
 * 规则选择器：按分类横向分组展示模板。点击规则回调 onSelect。
 */
@Composable
fun RulePickerGrid(
    rules: List<RuleWithCategory>,
    selectedRuleId: Long?,
    onSelect: (PointRuleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (rules.isEmpty()) {
        EmptyRulesHint(modifier = modifier)
        return
    }

    // 按 category 分组
    val groups = rules.groupBy { it.category }
    val orderedCategories = rules.mapNotNull { it.category }.distinctBy { it.id }

    Column(modifier = modifier) {
        orderedCategories.forEach { cat ->
            val groupRules = groups[cat].orEmpty()
            if (groupRules.isEmpty()) return@forEach
            val catColor = runCatching {
                Color(android.graphics.Color.parseColor(cat.colorHex))
            }.getOrDefault(MaterialTheme.colorScheme.primary)

            // 分类小头
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            ) {
                Text(cat.icon, fontSize = 14.sp)
                Spacer(Modifier.size(4.dp))
                Text(
                    text = cat.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = catColor
                )
            }

            // 规则横向 chip
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                items(groupRules) { item ->
                    val rule = item.rule
                    val isPenalty = rule.points < 0
                    val selected = rule.id == selectedRuleId
                    RuleChip(
                        name = rule.name,
                        points = rule.points,
                        isPenalty = isPenalty,
                        color = catColor,
                        icon = rule.icon.ifBlank { cat.icon },
                        selected = selected,
                        onClick = { onSelect(rule) }
                    )
                }
            }
        }

        // 未分类规则
        groups[null]?.takeIf { it.isNotEmpty() }?.let { uncat ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            ) {
                Text("未分类", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uncat) { item ->
                    val isPenalty = item.rule.points < 0
                    RuleChip(
                        name = item.rule.name,
                        points = item.rule.points,
                        isPenalty = isPenalty,
                        color = MaterialTheme.colorScheme.primary,
                        icon = item.rule.icon.ifBlank { "⭐" },
                        selected = item.rule.id == selectedRuleId,
                        onClick = { onSelect(item.rule) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RuleChip(
    name: String,
    points: Int,
    isPenalty: Boolean,
    color: Color,
    icon: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val sign = if (isPenalty) "${points}" else "+${points}"
    val pointsColor = if (isPenalty) PointNegative else PointPositive

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) color.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = color,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 16.sp)
        }
        Spacer(Modifier.size(6.dp))
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = sign,
                style = MaterialTheme.typography.labelSmall,
                color = pointsColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyRulesHint(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "暂无模板，可到行为模板页添加快捷模板",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}