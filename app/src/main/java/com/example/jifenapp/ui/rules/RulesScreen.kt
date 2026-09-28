package com.example.jifenapp.ui.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jifenapp.data.local.entity.CategoryEntity
import com.example.jifenapp.data.local.entity.PointRuleEntity
import com.example.jifenapp.data.model.RuleWithCategory
import com.example.jifenapp.ui.component.EmptyState
import com.example.jifenapp.ui.theme.PointNegative
import com.example.jifenapp.ui.theme.PointPositive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    viewModel: RulesViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var menuExpanded by remember { mutableStateOf(false) }
    var showRuleEditor by remember { mutableStateOf(false) }
    var editingRule by remember { mutableStateOf<PointRuleEntity?>(null) }
    var showCategoryEditor by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var pendingDeleteRule by remember { mutableStateOf<PointRuleEntity?>(null) }
    var pendingDeleteCategory by remember { mutableStateOf<CategoryEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("行为模板", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showRuleEditor = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "新增模板")
                    }
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "更多")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("管理分类") },
                                onClick = {
                                    menuExpanded = false
                                    editingCategory = null
                                    showCategoryEditor = true
                                },
                                leadingIcon = { Icon(Icons.Filled.Category, null) }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
        ) {
            // 分类筛选 Chip 行
            if (state.categories.isNotEmpty()) {
                CategoryFilterRow(
                    categories = state.categories,
                    selectedId = state.categoryFilter,
                    onSelect = viewModel::setCategoryFilter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            if (state.rules.isEmpty() && !state.loading) {
                EmptyState(
                    title = "还没有模板",
                    subtitle = "点右上角 + 添加你的第一个模板，例如\"刷牙 +5\"",
                    icon = Icons.Filled.Star,
                    actionText = "新建模板",
                    onAction = { showRuleEditor = true }
                )
            } else {
                RuleGroupedList(
                    groups = state.grouped,
                    onEdit = { rule ->
                        editingRule = rule
                        showRuleEditor = true
                    },
                    onToggleEnabled = viewModel::toggleRuleEnabled,
                    onDelete = { pendingDeleteRule = it }
                )
            }
        }
    }

    // 规则编辑弹窗
    if (showRuleEditor) {
        RuleEditorDialog(
            initial = editingRule,
            categories = state.categories,
            onDismiss = {
                showRuleEditor = false
                editingRule = null
            },
            onConfirm = { name, points, catId, ic ->
                if (editingRule == null) {
                    viewModel.addRule(name, points, catId, ic)
                } else {
                    viewModel.updateRule(
                        editingRule!!.copy(
                            name = name, points = points,
                            categoryId = catId, icon = ic
                        )
                    )
                }
                showRuleEditor = false
                editingRule = null
            }
        )
    }

    // 分类编辑弹窗
    if (showCategoryEditor) {
        CategoryEditorDialog(
            initial = editingCategory,
            onDismiss = {
                showCategoryEditor = false
                editingCategory = null
            },
            onConfirm = { name, icon, hex ->
                if (editingCategory == null) {
                    viewModel.addCategory(name, icon, hex)
                } else {
                    viewModel.updateCategory(
                        editingCategory!!.copy(name = name, icon = icon, colorHex = hex)
                    )
                }
                showCategoryEditor = false
                editingCategory = null
            }
        )
    }

    // 删除确认
    pendingDeleteRule?.let { rule ->
        AlertDialog(
            onDismissRequest = { pendingDeleteRule = null },
            title = { Text("删除模板？") },
            text = { Text("\"${rule.name}\" 将被删除，相关流水的模板引用会置空，但历史记录保留。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteRule(rule)
                    pendingDeleteRule = null
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteRule = null }) { Text("取消") }
            }
        )
    }

    pendingDeleteCategory?.let { cat ->
        AlertDialog(
            onDismissRequest = { pendingDeleteCategory = null },
            title = { Text("删除分类？") },
            text = { Text("\"${cat.name}\" 下的规则不会被删除，但会变为\"未分类\"。") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCategory(cat)
                    pendingDeleteCategory = null
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteCategory = null }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun CategoryFilterRow(
    categories: List<CategoryEntity>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedId == null,
            onClick = { onSelect(null) },
            label = { Text("全部") }
        )
        categories.forEach { cat ->
            FilterChip(
                selected = selectedId == cat.id,
                onClick = { onSelect(cat.id) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(cat.icon, fontSize = 14.sp)
                        Spacer(Modifier.size(4.dp))
                        Text(cat.name)
                    }
                }
            )
        }
    }
}

@Composable
private fun RuleGroupedList(
    groups: List<Pair<CategoryEntity?, List<RuleWithCategory>>>,
    onEdit: (PointRuleEntity) -> Unit,
    onToggleEnabled: (PointRuleEntity) -> Unit,
    onDelete: (PointRuleEntity) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        groups.forEach { (category, rules) ->
            item(key = "h_${category?.id ?: "uncat"}") {
                CategoryHeader(category = category, count = rules.size)
            }
            items(items = rules, key = { it.rule.id }) { ruleWithCat ->
                RuleRow(
                    item = ruleWithCat,
                    onEdit = { onEdit(ruleWithCat.rule) },
                    onToggle = { onToggleEnabled(ruleWithCat.rule) },
                    onDelete = { onDelete(ruleWithCat.rule) }
                )
            }
        }
    }
}

@Composable
private fun CategoryHeader(category: CategoryEntity?, count: Int) {
    val color = category?.colorHex?.let {
        runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull()
    } ?: MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "${category?.icon.orEmpty()} ${category?.name ?: "未分类"}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = "$count",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RuleRow(
    item: RuleWithCategory,
    onEdit: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val rule = item.rule
    val categoryColor = item.category?.colorHex?.let {
        runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull()
    } ?: MaterialTheme.colorScheme.primary
    val isPenalty = rule.points < 0

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (rule.enabled)
                MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧圆点（用分类色或规则色）
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rule.icon.ifBlank { item.category?.icon ?: "⭐" },
                    fontSize = 20.sp
                )
            }
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (rule.enabled)
                        MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (isPenalty) "惩罚" else "奖励",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = (if (isPenalty) "${rule.points}" else "+${rule.points}"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPenalty) PointNegative else PointPositive
            )
            Spacer(Modifier.size(4.dp))
            Switch(checked = rule.enabled, onCheckedChange = { onToggle() })
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "编辑", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}