package com.example.jifenapp.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jifenapp.data.local.entity.ChildEntity
import com.example.jifenapp.ui.theme.ChildAvatarChoices
import com.example.jifenapp.ui.theme.ChildColorChoices

/**
 * 孩子编辑/新建对话框。MVP 阶段仅 name/avatar/colorHex 三个字段。
 */
@Composable
fun ChildEditorDialog(
    initial: ChildEntity?,
    onDismiss: () -> Unit,
    onConfirm: (name: String, avatar: String, colorHex: String) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var avatar by remember { mutableStateOf(initial?.avatar ?: ChildAvatarChoices.first()) }
    var colorHex by remember { mutableStateOf(initial?.colorHex ?: colorToHex(ChildColorChoices.first())) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "添加孩子" else "编辑孩子") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it.take(20) },
                    label = { Text("姓名") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.size(16.dp))

                Text("选个头像", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.size(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(48.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    items(ChildAvatarChoices) { emoji ->
                        val selected = emoji == avatar
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(
                                    width = if (selected) 2.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .clickable { avatar = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                        }
                    }
                }

                Spacer(Modifier.size(16.dp))

                Text("选个主题色", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.size(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(ChildColorChoices) { color ->
                        val hex = colorToHex(color)
                        val selected = hex == colorHex
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selected) 3.dp else 0.dp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = CircleShape
                                )
                                .clickable { colorHex = hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selected) {
                                Text("✓", color = Color.White, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name.trim(), avatar, colorHex) },
                enabled = name.isNotBlank()
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

internal fun colorToHex(color: Color): String {
    val argb = (color.value shr 32).toLong().toInt()
    return String.format("#%06X", 0xFFFFFF and argb)
}
