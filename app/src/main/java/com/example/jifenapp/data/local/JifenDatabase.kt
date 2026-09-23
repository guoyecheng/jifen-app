package com.example.jifenapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jifenapp.data.local.dao.ChildDao
import com.example.jifenapp.data.local.dao.PointRecordDao
import com.example.jifenapp.data.local.entity.ChildEntity
import com.example.jifenapp.data.local.entity.PointRecordEntity

/**
 * Room 数据库。迭代 1 包含孩子与流水两张表，迭代 2 接入模板/分类。
 *
 * schema 变更务必 bump version 并提供 Migration。当前 v1 不需要迁移。
 */
@Database(
    entities = [
        ChildEntity::class,
        PointRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class JifenDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao
    abstract fun pointRecordDao(): PointRecordDao

    companion object {
        private const val DB_NAME = "jifen.db"

        fun build(context: Context): JifenDatabase = Room
            .databaseBuilder(context.applicationContext, JifenDatabase::class.java, DB_NAME)
            // MVP 阶段直接销毁重建；后续正式版加迁移
            .fallbackToDestructiveMigration()
            .build()
    }
}
