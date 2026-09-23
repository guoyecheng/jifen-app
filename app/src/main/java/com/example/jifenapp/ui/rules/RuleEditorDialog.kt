package com.example.jifenapp.ui.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jifenapp.data.local.entity.CategoryEntity
import com.example.jifenapp.data.local.entity.PointRuleEntity
import com.example.jifenapp.ui.theme.CategoryIconOptions
import com.example.jifenapp.ui.theme.PointNegative
import com.example.jifenapp.ui.theme.PointPositive

/**
 * 规则编辑弹窗：增/改模板。
 *
 * 复用 [RulesViewModel.addRule]/[RulesViewModel.updateRule] 时传入 4 元组
 * (name, points, categoryId, icon)。
 */
@Composable
fun RuleEditorDialog(
    initial: PointRuleEntity?,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, points: Int, categoryId: Long?, icon: String) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var pointsInput by remember { mutableStateOf((initial?.points?.absoluteValue ?: 5).toString()) }
    var isPenalty by remember { mutableStateOf((initial?.points ?: 5) < 0) }
    var categoryId by remember { mutableStateOf(initial?.categoryId ?: categories.firstOrNull()?.id) }
    var icon by remember {
        mutableStateOf(
            initial?.icon?.takeIf { it.isNotBlank() }
                ?: categories.firstOrNull()?.icon
                ?: CategoryIconOptions.first()
        )
    }
    var enabled by remember { mutableStateOf(initial?.enabled ?: true) }

    val points = pointsInput.toIntOrNull()?.coerceAtLeast(0) ?: 0
    val signedPoints = if (isPenalty) -points else points

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "新建模板" else "编辑模板") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // 名称
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(20) },
                    label = { Text("名称") },
                    placeholder = { Text("如：刷牙、整理房间") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                // 分数（带正负切换）
                Text("分数", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = pointsInput,
                        onValueChange = { input -> pointsInput = input.filter { it.isDigit() }.take(5) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isPenalty) PointNegative else PointPositive
                        )
                    )
                    Spacer(Modifier.size(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isPenalty) "扣分" else "奖励", fontSize = 13.sp)
                        Spacer(Modifier.size(4.dp))
                        Switch(
                            checked = isPenalty,
                            onCheckedChange = { isPenalty = it }
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 分类选择
                Text("分类", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                if (categories.isEmpty()) {
                    Text(
                        "尚未创建分类，请先在右上角\"管理分类\"中添加",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(categories) { cat ->
                            val selected = cat.id == categoryId
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (selected) Color(android.graphics.Color.parseColor(cat.colorHex))
                                            .copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .border(
                                        width = if (selected) 2.dp else 0.dp,
                                        color = Color(android.graphics.Color.parseColor(cat.colorHex)),
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clickable { categoryId = cat.id }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cat.icon, fontSize = 16.sp)
                                Spacer(Modifier.size(4.dp))
                                Text(
                                    cat.name,
                                    fontSize = 13.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 图标选择（可选）
                Text("图标", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CategoryIconOptions.take(12)) { emoji ->
                        val selected = emoji == icon
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = if (selected) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .clickable { icon = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 20.sp)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 启用开关
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("启用", modifier = Modifier.weight(1f))
                    Switch(checked = enabled, onCheckedChange = { enabled = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && points > 0) {
                        onConfirm(name.trim(), signedPoints, categoryId, icon)
                    }
                },
                enabled = name.isNotBlank() && points > 0
            ) {
                Text(if (initial == null) "添加" else "保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

/** Kotlin Int 扩展：取绝对值（顶层已有 util.abs，但这里 inline 减少 import） */
private val Int.absoluteValue: Int get() = if (this < 0) -this else this