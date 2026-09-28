package com.example.jifenapp.ui.rules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jifenapp.data.local.entity.CategoryEntity
import com.example.jifenapp.data.local.entity.PointRuleEntity
import com.example.jifenapp.data.repository.CategoryRepository
import com.example.jifenapp.data.repository.PointRuleRepository
import com.example.jifenapp.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RulesViewModel(
    private val ruleRepository: PointRuleRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val filter = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<RulesUiState> = combine(
        ruleRepository.observeAll(),
        categoryRepository.observeAll(),
        filter
    ) { rules, categories, cat ->
        RulesUiState(
            rules = rules,
            categories = categories,
            categoryFilter = cat,
            loading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = RulesUiState(loading = true)
    )

    fun setCategoryFilter(id: Long?) {
        filter.value = id
    }

    // ===== 规则 CRUD =====

    fun addRule(
        name: String,
        points: Int,
        categoryId: Long?,
        icon: String? = null,
        colorHex: String? = null
    ) = viewModelScope.launch {
        val sortOrder = ((uiState.value.rules.maxOfOrNull { it.rule.sortOrder } ?: -1) + 1)
        ruleRepository.add(
            PointRuleEntity(
                name = name,
                points = points,
                categoryId = categoryId,
                icon = icon.orEmpty(),
                colorHex = colorHex,
                sortOrder = sortOrder,
                enabled = true
            )
        )
    }

    fun updateRule(rule: PointRuleEntity) = viewModelScope.launch {
        ruleRepository.update(rule)
    }

    fun toggleRuleEnabled(rule: PointRuleEntity) = viewModelScope.launch {
        ruleRepository.setEnabled(rule.id, !rule.enabled)
    }

    fun deleteRule(rule: PointRuleEntity) = viewModelScope.launch {
        ruleRepository.delete(rule)
    }

    // ===== 分类 CRUD =====

    fun addCategory(name: String, icon: String, colorHex: String) = viewModelScope.launch {
        val sortOrder = ((uiState.value.categories.maxOfOrNull { it.sortOrder } ?: -1) + 1)
        categoryRepository.add(
            CategoryEntity(name = name, icon = icon, colorHex = colorHex, sortOrder = sortOrder)
        )
    }

    fun updateCategory(category: CategoryEntity) = viewModelScope.launch {
        categoryRepository.update(category)
    }

    fun deleteCategory(category: CategoryEntity) = viewModelScope.launch {
        categoryRepository.delete(category)
    }

    companion object {
        fun create(container: AppContainer): RulesViewModel = RulesViewModel(
            ruleRepository = container.pointRuleRepository,
            categoryRepository = container.categoryRepository
        )
    }
}