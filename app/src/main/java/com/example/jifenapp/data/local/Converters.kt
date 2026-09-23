package com.example.jifenapp.data.local

import androidx.room.TypeConverter
import com.example.jifenapp.data.local.entity.RecordType

/**
 * Room TypeConverter。仅枚举需要处理；时间全部用 epochMillis(Long)。
 */
class Converters {
    @TypeConverter
    fun recordTypeToString(value: RecordType): String = value.name

    @TypeConverter
    fun stringToRecordType(value: String): RecordType = RecordType.valueOf(value)
}
