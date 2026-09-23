package com.example.jifenapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jifenapp.data.model.ChildWithStats
import com.example.jifenapp.ui.component.AnimatedNumber
import com.example.jifenapp.ui.component.ChildAvatar
import com.example.jifenapp.ui.component.EmptyState
import com.example.jifenapp.ui.component.HomeCardSkeleton
import com.example.jifenapp.ui.theme.ChildAvatarChoices
import com.example.jifenapp.ui.theme.ChildColorChoices
import com.example.jifenapp.ui.child.ChildEditorDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onChildClick: (Long) -> Unit,
    onAddRecord: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddChildDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的孩子们", fontWeight = FontWeight.SemiBold) }
            )
        },
        floatingActionButton = {
            // 仅当存在孩子时显示"记一笔" FAB；空状态由 EmptyState 引导添加孩子
            if (state.children.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = {
                        // 默认选中第一个孩子
                        state.children.firstOrNull()?.let { onAddRecord(it.child.id) }
                    },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("记一笔") }
                )
            }
        }
    ) { padding ->
        if (state.isEmpty) {
            EmptyState(
                title = "还没有添加孩子",
                subtitle = "添加你的第一个孩子，开始记录TA的每一次成长",
                icon = Icons.Default.ChildCare,
                actionText = "添加孩子",
                onAction = { showAddChildDialog = true },
                modifier = Modifier.padding(padding)
            )
        } else if (state.children.isEmpty()) {
            // 加载中：骨架屏
            LoadingGrid(modifier = Modifier.fillMaxSize().padding(padding))
        } else {
            ChildGrid(
                children = state.children,
                onChildClick = onChildClick,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        }
    }

    if (showAddChildDialog) {
        ChildEditorDialog(
            initial = null,
            onDismiss = { showAddChildDialog = false },
            onConfirm = { name, avatar, colorHex ->
                viewModel.addChild(name, avatar, colorHex)
                showAddChildDialog = false
            }
        )
    }
}

@Composable
private fun ChildGrid(
    children: List<ChildWithStats>,
    onChildClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(items = children, key = { it.child.id }) { item ->
            ChildCard(item = item, onClick = { onChildClick(item.child.id) })
        }
    }
}

@Composable
private fun LoadingGrid(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(count = 4) {
            HomeCardSkeleton()
        }
    }
}

@Composable
private fun ChildCard(item: ChildWithStats, onClick: () -> Unit) {
    val childColor = remember(item.child.colorHex) {
        runCatching { Color(android.graphics.Color.parseColor(item.child.colorHex)) }
            .getOrDefault(MaterialTheme.colorScheme.primary)
    }
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部色条
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(childColor)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ChildAvatar(
                    avatar = item.child.avatar,
                    colorHex = item.child.colorHex,
                    size = 64.dp,
                    emojiSizeSp = 32
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.child.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                AnimatedNumber(
                    value = item.total,
                    style = MaterialTheme.typography.displaySmall,
                    showSign = false
                )
                Text(
                    text = "积分",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "本周 +${item.weekAdd}",
                        style = MaterialTheme.typography.labelMedium,
                        color = com.example.jifenapp.ui.theme.PointPositive
                    )
                    Text(
                        text = "-${item.weekSub}",
                        style = MaterialTheme.typography.labelMedium,
                        color = com.example.jifenapp.ui.theme.PointNegative
                    )
                }
            }
        }
    }
}
