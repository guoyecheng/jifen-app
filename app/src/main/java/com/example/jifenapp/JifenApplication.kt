package com.example.jifenapp

import android.app.Application
import com.example.jifenapp.di.AppContainer

/**
 * 应用入口：持有依赖容器 [AppContainer]，供各 ViewModel 通过 Application 取用。
 *
 * 不使用 Hilt/Koin——本项目规模小，手工注入更轻量、构建更快。
 */
class JifenApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
