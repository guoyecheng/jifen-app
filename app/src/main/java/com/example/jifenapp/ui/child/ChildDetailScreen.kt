package com.example.jifenapp.ui.child

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jifenapp.data.local.entity.PointRecordEntity
import com.example.jifenapp.data.local.entity.RecordType
import com.example.jifenapp.ui.component.AnimatedNumber
import com.example.jifenapp.ui.component.EmptyState
import com.example.jifenapp.ui.theme.LocalChildColor
import com.example.jifenapp.ui.theme.PointNegative
import com.example.jifenapp.ui.theme.PointPositive
import com.example.jifenapp.ui.theme.childTheme
import com.example.jifenapp.util.toFriendlyTime
import com.example.jifenapp.util.toLocalDate

/**
 * 单孩子的详情页：顶部 summary + 流水列表。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildDetailScreen(
    childId: Long,
    viewModel: ChildDetailViewModel,
    onBack: () -> Unit,
    onAddRecord: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var pendingDelete by remember { mutableStateOf<PointRecordEntity?>(null) }

    val child = state.child
    if (child != null) {
        // 局部覆盖 child color 作为强调色
        childTheme(childColorHex = child.colorHex) {
            val childColor = LocalChildColor.current
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(child.name, fontWeight = FontWeight.SemiBold) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = childColor.copy(alpha = 0.1f)
                        )
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = onAddRecord,
                        containerColor = childColor
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "记一笔", tint = Color.White)
                    }
                }
            ) { padding ->
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                ) {
                    SummarySection(
                        total = state.total,
                        weekAdd = state.weekAdd,
                        weekSub = state.weekSub,
                        todayDelta = state.todayDelta,
                        accentColor = childColor
                    )
                    RecordsList(
                        records = state.records,
                        onLongPressRecord = { pendingDelete = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    pendingDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("删除这条记录？") },
            text = { Text("\"${record.title}\" 将被删除，积分也会回退。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRecord(record)
                    pendingDelete = null
                }) { Text("删除", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun SummarySection(
    total: Int,
    weekAdd: Int,
    weekSub: Int,
    todayDelta: Int,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("当前积分", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            AnimatedNumber(
                value = total,
                style = MaterialTheme.typography.displayLarge,
                showSign = false
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "本周 +", value = "+$weekAdd", color = PointPositive)
                StatItem(label = "本周 -", value = "-$weekSub", color = PointNegative)
                StatItem(
                    label = "今日",
                    value = (if (todayDelta > 0) "+$todayDelta" else "$todayDelta"),
                    color = if (todayDelta >= 0) PointPositive else PointNegative
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun RecordsList(
    records: List<PointRecordEntity>,
    onLongPressRecord: (PointRecordEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) {
        EmptyState(
            title = "还没有记录",
            subtitle = "点右下角 + 加一条吧",
            icon = androidx.compose.material.icons.Icons.Default.Star,
            modifier = modifier
        )
        return
    }

    // 按日期分组
    val grouped = records.groupBy { it.createdAt.toLocalDate().toString() }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        grouped.forEach { (date, items) ->
            item(key = "header_$date") {
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            items(items = items, key = { it.id }) { record ->
                RecordRow(record = record, onLongClick = { onLongPressRecord(record) })
            }
        }
    }
}

@Composable
private fun RecordRow(
    record: PointRecordEntity,
    onLongClick: () -> Unit
) {
    val isAdd = record.type == RecordType.ADD
    Card(
        onClick = onLongClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 类型圆点
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background((if (isAdd) PointPositive else PointNegative).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isAdd) "+" else "-",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isAdd) PointPositive else PointNegative
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                record.note?.takeIf { it.isNotBlank() }?.let { note ->
                    Text(
                        text = note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = record.createdAt.toFriendlyTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${if (isAdd) "+" else "-"}${record.points}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isAdd) PointPositive else PointNegative
            )
        }
    }
}

