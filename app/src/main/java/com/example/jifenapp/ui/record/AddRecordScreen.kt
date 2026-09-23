package com.example.jifenapp.ui.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.jifenapp.data.local.entity.RecordType
import com.example.jifenapp.ui.theme.PointNegative
import com.example.jifenapp.ui.theme.PointPositive

/**
 * 记一笔页面（自由输入版）。迭代 2 接入规则模板后扩展 RulePickerGrid。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    childId: Long,
    viewModel: AddRecordViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("记一笔") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChildHeader(
                name = state.child?.name ?: "",
                avatar = state.child?.avatar ?: "👶",
                colorHex = state.child?.colorHex ?: "#5B6EE1",
                total = state.childTotalPoints
            )

            // 类型 Tab
            TypeTabs(
                type = state.type,
                onChange = viewModel::setType
            )

            // 分数输入
            PointsInput(
                type = state.type,
                value = state.pointsInput,
                onChange = viewModel::setPoints,
                onAdjust = viewModel::adjustPoints
            )

            // 标题
            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::setTitle,
                label = { Text("标题（必填）") },
                placeholder = { Text("例如：刷牙、整理房间") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // 备注（可选）
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::setNote,
                label = { Text("备注（可选）") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::save,
                enabled = state.canSave,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.type == RecordType.ADD) PointPositive else PointNegative
                )
            ) {
                Text(
                    text = if (state.type == RecordType.ADD) "保存奖励" else "保存扣减",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ChildHeader(name: String, avatar: String, colorHex: String, total: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            com.example.jifenapp.ui.component.ChildAvatar(
                avatar = avatar, colorHex = colorHex, size = 48.dp, emojiSizeSp = 24
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text = "当前积分 $total",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TypeTabs(type: RecordType, onChange: (RecordType) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterChip(
            selected = type == RecordType.ADD,
            onClick = { onChange(RecordType.ADD) },
            label = { Text("奖励 +") },
            leadingIcon = if (type == RecordType.ADD) {
                { Icon(androidx.compose.material.icons.Icons.Default.Add, null) }
            } else null,
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = type == RecordType.SUBTRACT,
            onClick = { onChange(RecordType.SUBTRACT) },
            label = { Text("扣减 -") },
            leadingIcon = if (type == RecordType.SUBTRACT) {
                { Icon(androidx.compose.material.icons.Icons.Default.Remove, null) }
            } else null,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PointsInput(
    type: RecordType,
    value: String,
    onChange: (String) -> Unit,
    onAdjust: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            label = { Text("分数") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (type == RecordType.ADD) PointPositive else PointNegative
            )
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(-5, -1, 1, 5, 10).forEach { delta ->
                OutlinedButton(
                    onClick = { onAdjust(delta) },
                    modifier = Modifier.weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Text(
                        text = (if (delta > 0) "+$delta" else "$delta"),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
