package com.example.jifenapp.util

import kotlinx.serialization.json.Json

/** 全局 JSON 序列化器配置：忽略未知字段（用于版本兼容）。 */
val AppJson: Json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    prettyPrint = false
    explicitNulls = false
}
