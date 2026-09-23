package com.example.jifenapp.di

import android.content.Context
import com.example.jifenapp.data.local.JifenDatabase
import com.example.jifenapp.data.repository.ChildRepository
import com.example.jifenapp.data.repository.PointRecordRepository

/**
 * 简易依赖容器。Application.onCreate() 创建一次。
 *
 * 每个 ViewModel 通过 Application.container 拿到对应 Repository。
 */
class AppContainer(context: Context) {
    private val database: JifenDatabase = JifenDatabase.build(context)

    val childRepository: ChildRepository = ChildRepository(database.childDao())
    val pointRecordRepository: PointRecordRepository =
        PointRecordRepository(database.pointRecordDao())
}
