package com.example.jifenapp.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jifenapp.data.local.entity.PointRecordEntity
import com.example.jifenapp.data.local.entity.PointRuleEntity
import com.example.jifenapp.data.local.entity.RecordType
import com.example.jifenapp.data.repository.ChildRepository
import com.example.jifenapp.data.repository.PointRecordRepository
import com.example.jifenapp.data.repository.PointRuleRepository
import com.example.jifenapp.di.AppContainer
import com.example.jifenapp.util.nowMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 添加流水 ViewModel（迭代 2：含规则选择）。
 */
class AddRecordViewModel(
    private val childId: Long,
    private val childRepository: ChildRepository,
    private val recordRepository: PointRecordRepository,
    private val ruleRepository: PointRuleRepository
) : ViewModel() {

    private val _form = MutableStateFlow(AddRecordUiState())

    val uiState: StateFlow<AddRecordUiState> = combine(
        _form,
        childRepository.observeById(childId),
        recordRepository.totalPoints(childId),
        ruleRepository.observeEnabled()
    ) { form, child, total, rules ->
        form.copy(child = child, childTotalPoints = total, rules = rules)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AddRecordUiState()
    )

    fun setType(type: RecordType) = _form.update { it.copy(type = type, error = null) }

    fun setTitle(title: String) = _form.update {
        it.copy(title = title.take(40), selectedRuleId = null, error = null)
    }

    fun setPoints(input: String) = _form.update {
        val cleaned = input.filter { c -> c.isDigit() }.take(5)
        it.copy(pointsInput = cleaned, selectedRuleId = null, error = null)
    }

    fun setNote(note: String) = _form.update { it.copy(note = note.take(100)) }

    fun adjustPoints(delta: Int) {
        val current = _form.value.points ?: 0
        val newValue = (current + delta).coerceAtLeast(0).coerceAtMost(99999)
        _form.update { it.copy(pointsInput = newValue.toString(), selectedRuleId = null) }
    }

    /**
     * 选中规则：自动填入 title / points / type，清空 note。
     */
    fun selectRule(rule: PointRuleEntity) = _form.update {
        val type = if (rule.points < 0) RecordType.SUBTRACT else RecordType.ADD
        it.copy(
            selectedRuleId = rule.id,
            title = rule.name,
            pointsInput = kotlin.math.abs(rule.points).toString(),
            type = type,
            note = ""
        )
    }

    fun clearRuleSelection() = _form.update { it.copy(selectedRuleId = null) }

    fun save() {
        val current = _form.value
        if (!current.canSave) return

        viewModelScope.launch {
            _form.update { it.copy(saving = true) }
            try {
                val p = current.points ?: 0
                recordRepository.add(
                    PointRecordEntity(
                        childId = childId,
                        type = current.type,
                        points = p,
                        title = current.title.trim(),
                        note = current.note.trim().takeIf { it.isNotEmpty() },
                        ruleId = current.selectedRuleId,
                        createdAt = nowMillis()
                    )
                )
                _form.update { it.copy(saving = false, saved = true) }
            } catch (e: Exception) {
                _form.update { it.copy(saving = false, error = e.message ?: "保存失败") }
            }
        }
    }

    companion object {
        fun create(container: AppContainer, childId: Long): AddRecordViewModel = AddRecordViewModel(
            childId = childId,
            childRepository = container.childRepository,
            recordRepository = container.pointRecordRepository,
            ruleRepository = container.pointRuleRepository
        )
    }
}