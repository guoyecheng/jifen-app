package com.example.jifenapp.ui.statistics

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jifenapp.ui.component.AnimatedNumber
import com.example.jifenapp.ui.component.PieChart
import com.example.jifenapp.ui.theme.LocalChildColor
import com.example.jifenapp.ui.theme.PointNegative
import com.example.jifenapp.ui.theme.PointPositive
import com.example.jifenapp.ui.theme.childTheme
import com.example.jifenapp.util.TimeRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val child = state.child

    if (child == null) {
        // 加载中或孩子不存在：仅显示空 Scaffold（避免 childTheme 没值时崩）
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("统计") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding))
        }
        return
    }

    childTheme(childColorHex = child.colorHex) {
        val childColor = LocalChildColor.current
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("统计", fontWeight = FontWeight.SemiBold)
                            Text(
                                text = child.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                        containerColor = childColor.copy(alpha = 0.1f)
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // 时间范围 Chip
                TimeRangeRow(
                    selected = state.timeRange,
                    onSelect = viewModel::setTimeRange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )

                // 4 个指标卡
                StatsGrid(
                    totalPoints = state.totalPoints,
                    rangeAdd = state.rangeAdd,
                    rangeSub = state.rangeSub,
                    todayDelta = state.todayDelta,
                    timeRangeLabel = state.timeRange.label,
                    accent = childColor,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(Modifier.height(8.dp))

                // 饼图 + 折线图
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PieChartCard(slices = state.categoryShare)
                    TrendChart(daily = state.dailyTrend)
                }
            }
        }
    }
}

@Composable
private fun TimeRangeRow(
    selected: TimeRange,
    onSelect: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TimeRange.entries.forEach { range ->
            FilterChip(
                selected = selected == range,
                onClick = { onSelect(range) },
                label = { Text(range.label) }
            )
        }
    }
}

@Composable
private fun StatsGrid(
    totalPoints: Int,
    rangeAdd: Int,
    rangeSub: Int,
    todayDelta: Int,
    timeRangeLabel: String,
    accent: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            label = "当前积分",
            valueContent = { AnimatedNumber(value = totalPoints, showSign = false) },
            modifier = Modifier.weight(1f),
            accent = accent
        )
    }
    Spacer(Modifier.height(8.dp))
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            label = "$timeRangeLabel +",
            valueContent = { Text("+${rangeAdd}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PointPositive) },
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "$timeRangeLabel -",
            valueContent = { Text("-${rangeSub}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PointNegative) },
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "今日",
            valueContent = {
                Text(
                    text = (if (todayDelta > 0) "+$todayDelta" else "$todayDelta"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (todayDelta >= 0) PointPositive else PointNegative
                )
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    valueContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    accent: androidx.compose.ui.graphics.Color? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (accent != null) accent.copy(alpha = 0.08f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            valueContent()
        }
    }
}

@Composable
private fun PieChartCard(slices: List<com.example.jifenapp.data.model.CategoryShare>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "分类占比",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            PieChart(slices = slices)
        }
    }
}