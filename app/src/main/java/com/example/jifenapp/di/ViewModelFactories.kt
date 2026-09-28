package com.example.jifenapp.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jifenapp.JifenApplication

/**
 * ViewModel 工厂：从 Application.container 注入依赖。
 *
 * 用法：
 * ```
 * val vm: HomeViewModel = viewModel(factory = viewModelFactory { HomeViewModel.create(it) })
 * ```
 *
 * ViewModel 的 `create(container, ...)` 静态方法内部从 AppContainer 取依赖。
 *
 * 实现要点：`LocalContext.current` 必须在 `@Composable` 作用域里访问，
 * 所以这里先在 `@Composable` 里拿到 Application，再交给非 Composable 的 Factory.create。
 */
@Composable
inline fun <reified VM : ViewModel> viewModelFactory(
    crossinline create: (AppContainer) -> VM
): ViewModelProvider.Factory {
    val app = LocalContext.current.applicationContext as JifenApplication
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(VM::class.java)) {
                "Unknown ViewModel class: $modelClass"
            }
            @Suppress("UNCHECKED_CAST")
            return create(app.container) as T
        }
    }
}