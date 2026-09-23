package com.example.jifenapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jifenapp.data.local.dao.CategoryDao
import com.example.jifenapp.data.local.dao.ChildDao
import com.example.jifenapp.data.local.dao.PointRecordDao
import com.example.jifenapp.data.local.dao.PointRuleDao
import com.example.jifenapp.data.local.entity.CategoryEntity
import com.example.jifenapp.data.local.entity.ChildEntity
import com.example.jifenapp.data.local.entity.PointRecordEntity
import com.example.jifenapp.data.local.entity.PointRuleEntity

/**
 * Room 数据库。
 *
 * Schema 版本：
 * - v1：Child + PointRecord
 * - v2：新增 Category + PointRule；PointRecord 加 ruleId 外键
 *
 * 迭代 2 接入规则/分类时 bump 到 v2。开发期使用 fallbackToDestructiveMigration 直接重建。
 */
@Database(
    entities = [
        ChildEntity::class,
        PointRecordEntity::class,
        PointRuleEntity::class,
        CategoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class JifenDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao
    abstract fun pointRecordDao(): PointRecordDao
    abstract fun pointRuleDao(): PointRuleDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        private const val DB_NAME = "jifen.db"

        fun build(context: Context): JifenDatabase = Room
            .databaseBuilder(context.applicationContext, JifenDatabase::class.java, DB_NAME)
            // 开发期直接重建，正式版需要写 Migration
            .fallbackToDestructiveMigration()
            .build()
    }
}