# 儿童积分管理 Android App 实现计划

## Context

需求：从零搭建一个 Android 单机版积分管理 App，用于家长管理孩子的积分添加/扣减与统计。

确认的关键需求（通过 AskUserQuestion 收集）：

1. **技术栈**：原生 Kotlin + Jetpack Compose + Room（推荐方案）
2. **多孩支持**：支持多个孩子，可自由切换查看
3. **行为模板**：家长可自定义积分规则（如"刷牙 +5"），点模板即可快速记录
4. **统计深度**：基本统计——总数、本周/月加减、分类饼图、近 N 天趋势图
5. **离线**：纯单机，不需要云端、登录、网络

仓库当前状态：D:\workspace\jifen-app 为空仓库（仅有 `.git`、`.idea/`、`.gitignore`），需从零搭建。

预期产出：可在 Android Studio 编译运行的 APK，支持完整"添加孩子 → 定义规则 → 记流水 → 看统计"闭环。

---

## 技术选型

| 组件 | 选择 | 备注 |
|------|------|------|
| Kotlin | 2.0.21 | K2 编译器稳定 |
| AGP / Gradle | 8.7.2 / 8.10.2 | 与 Kotlin 2.0 匹配 |
| compileSdk / targetSdk | 35 | Material3 1.3+ 兼容 |
| minSdk | 26 | 覆盖 >97% 设备 |
| Compose BOM | 2025.01.00 | 统一管理 |
| Room | 2.6.1 | 含 room-ktx |
| Navigation Compose | 2.8.4 | 单 Activity 多 Composable |
| Lifecycle | 2.8.7 | collectAsStateWithLifecycle |
| DataStore Preferences | 1.1.1 | 主题、最后选中孩子 |
| Coroutines | 1.9.0 | Flow / viewModelScope |
| kotlinx-serialization-json | 1.7.3 | 数据备份（无反射） |
| kotlinx-datetime | 0.6.1 | 时间处理，避免 desugar |
| **图表** | **Vico（折线）+ 自绘 Compose Canvas（饼图）** | Vico 饼图弱，自绘更可控 |
| **DI** | **手动 AppContainer + ViewModelProvider.Factory** | 项目规模小，避免 Hilt 编译开销 |

不引入：Hilt、Retrofit、Coil（emoji 当头像，无需图片加载）、Glide。

---

## 包结构

按 feature-first 分层：

```
com.example.jifenapp/
├── JifenApplication.kt        # 创建 Database/Repository 单例
├── MainActivity.kt            # 单 Activity 宿主
├── data/
│   ├── local/                 # Room: JifenDatabase, Converters, entity/*, dao/*
│   ├── repository/            # ChildRepository, PointRecordRepository, ...
│   ├── model/                 # ChildWithStats, DailyPointTotal, CategoryShare
│   └── backup/                # Backup.kt (@Serializable)
├── ui/
│   ├── theme/                 # Color, Theme, Type
│   ├── component/             # ChildAvatar, PointBadge, EmptyState, PieChart
│   ├── home/                  # HomeScreen + ViewModel + UiState
│   ├── child/                 # ChildDetailScreen, ChildEditorDialog
│   ├── record/                # AddRecordScreen, RulePickerGrid
│   ├── statistics/            # StatisticsScreen, TrendChart
│   ├── rules/                 # RulesScreen, RuleEditorDialog
│   └── settings/              # SettingsScreen, BackupExporter
├── navigation/                # JifenNavHost, Routes (sealed class)
├── di/                        # AppContainer + ViewModelFactories
└── util/                      # DateExt, JsonFormat
```

---

## 数据模型（Room）

### ChildEntity（孩子）
- id (PK), name, avatar (emoji), colorHex, birthdayEpochDay?, createdAt

### CategoryEntity（分类）
- id (PK), name, colorHex, icon, sortOrder

### PointRuleEntity（行为模板）
- id (PK), name, **points (正=奖励 / 负=惩罚)**, categoryId (FK→Category, SET_NULL), icon, colorHex?, sortOrder, enabled, createdAt
- 索引：categoryId, enabled

### PointRecordEntity（流水）
- id (PK), **childId (FK→Child, CASCADE)**, ruleId (FK→Rule, SET_NULL), type (ADD/SUBTRACT enum), **points (绝对值)**, **title (快照字符串)**, note?, createdAt
- 索引：childId, ruleId, createdAt, **(childId, createdAt) 复合索引**

**关键设计**：
- `title` 存"快照"——规则后续改名/删除不影响历史流水显示
- `type` 冗余存——规则分数从 +5 改为 -5 时，历史记录的 type 不翻转
- 当前积分：`SUM(CASE WHEN type='ADD' THEN points ELSE -points END)`

### 关系视图（@Relation）
- `RecordWithRule` — 流水 + 规则
- `RuleWithCategory` — 规则 + 分类

---

## 关键功能流程

### 添加流水（核心路径）
1. AddRecordScreen 顶部显示孩子头像 + 当前积分
2. 类型 Tab：ADD / SUBTRACT（可手动切换，默认按规则带过来）
3. 规则网格：横向 LazyVerticalGrid，按 category 分组
4. 点击规则 → 自动填入 points/type/title；高亮选中
5. 折叠自定义区：可调整分数、加备注
6. 保存 → ViewModel → Repository → Room insert → 返回 ChildDetail

### 统计聚合（DAO 关键查询）
| 指标 | SQL |
|------|-----|
| 总积分 | `SUM(CASE WHEN type='ADD' THEN points ELSE -points END)` |
| 本周 + / - | `WHERE childId=? AND type=? AND createdAt BETWEEN ? AND ?` |
| 类别饼图 | `LEFT JOIN categories` + `GROUP BY categoryId` |
| 近 N 天趋势 | `GROUP BY createdAt/86400000` → List<DailyPointTotal> |

时间工具（util/DateExt.kt）：`Instant.startOfDay() / startOfWeek() / startOfMonth()`。

---

## 屏幕/页面设计

| Screen | 职责 | 关键组件 |
|--------|------|---------|
| **HomeScreen** | 孩子卡片网格 + 顶部菜单（规则、设置） | LazyVerticalGrid, ChildCard, ExtendedFAB("记一笔") |
| **ChildDetailScreen** | 单孩子流水列表（按日分组）+ Summary 头 | LargeTopAppBar, LazyColumn + stickyHeader, FAB |
| **AddRecordScreen** | 选规则快速记录 / 自由输入 | SecondaryTabRow, RulePickerGrid, Button |
| **StatisticsScreen** | 4 个指标卡 + 饼图 + 折线图 + 时间范围 Chip | StatCards, PieChart (自绘), TrendChart (Vico) |
| **RulesScreen** | 模板 CRUD，分组按类别 | LazyColumn + stickyHeader, Switch, DragHandle(v2) |
| **SettingsScreen** | 孩子管理、主题、备份导出 | DataStore, FileProvider, ACTION_SEND/OPEN_DOCUMENT |

每个孩子可设独立 `colorHex`，ChildDetailScreen 内用 `CompositionLocalProvider(LocalChildColor provides ...)` 局部覆盖主题强调色。

---

## 架构与模式

- **架构**：UI (Composable + ViewModel) → StateFlow → Repository → Room DAO → SQLite
- **模式**：MVVM + Repository；UI 状态统一 `data class XxxUiState`，事件用一次性 `SharedFlow`
- **路由**：`sealed class Routes` + `NavType.LongType` 传参
- **导航**：单 Activity + Navigation Compose
- **注入**：Application 持有 `AppContainer`，ViewModel 通过 `ViewModelProvider.Factory` 手动从容器取依赖
- **主题**：Material3，dynamicColor 默认开启（Android 12+），深色模式支持
- **字体**：Material3 默认 Roboto（中文尚可），思源黑体集成作为可选 v2

---

## 实现步骤（4 个迭代）

### 迭代 1：MVP — 项目骨架 + 单孩 + 加减流水 + 流水列表
- 搭建 Gradle（`build.gradle.kts`、version catalog `gradle/libs.versions.toml`）
- `JifenApplication`、`MainActivity`、`JifenTheme`
- `ChildEntity` + `PointRecordEntity` + 对应 DAO/Repository
- `AppContainer`、`JifenNavHost`、`Routes`
- `HomeScreen`、`ChildDetailScreen`、`AddRecordScreen`（自由输入版，无规则）

**验收**：能添加孩子、加减流水、查看列表，积分正确。

### 迭代 2：多孩 + 行为模板
- 新增 `CategoryEntity`、`PointRuleEntity` 及 DAO/Repository
- `RulesScreen` + `RuleEditorDialog`
- `AddRecordScreen` 接入 `RulePickerGrid`
- `SettingsScreen` 入口 + 孩子管理 `ChildEditorDialog`

**验收**：能管理多孩，CRUD 模板，点模板即可记流水。

### 迭代 3：统计图表
- 扩展 `PointRecordRepository`：observeTotal / observeRange / observeByCategory / observeDailyTrend
- `StatisticsScreen` + ViewModel
- 自绘 `PieChart`（Compose Canvas）
- Vico `TrendChart`
- `util/DateExt.kt`

**验收**：4 卡片 + 饼图 + 折线图正确；时间范围切换正常。

### 迭代 4：设置 + 备份导出
- `SettingsScreen` + ViewModel
- `BackupExporter`（导出 JSON → Downloads；导入 ACTION_OPEN_DOCUMENT）
- AndroidManifest：`FileProvider` 声明 + `res/xml/file_paths.xml`
- 主题切换（DataStore）

**验收**：能导出/导入 JSON 备份；深色模式切换正常。

---

## 关键文件清单（按模块分组）

### 根与构建
- `build.gradle.kts` (root + app), `settings.gradle.kts`, `gradle/libs.versions.toml`, `gradle.properties`
- `AndroidManifest.xml`

### Application & Navigation
- `app/src/main/java/com/example/jifenapp/JifenApplication.kt`
- `app/src/main/java/com/example/jifenapp/MainActivity.kt`
- `app/src/main/java/com/example/jifenapp/navigation/JifenNavHost.kt`
- `app/src/main/java/com/example/jifenapp/navigation/Routes.kt`

### Theme
- `app/src/main/java/com/example/jifenapp/ui/theme/{Color,Theme,Type}.kt`

### Data — Entities & DAOs
- `app/src/main/java/com/example/jifenapp/data/local/JifenDatabase.kt`
- `app/src/main/java/com/example/jifenapp/data/local/Converters.kt`
- `app/src/main/java/com/example/jifenapp/data/local/entity/{Child,Category,PointRule,PointRecord}Entity.kt`
- `app/src/main/java/com/example/jifenapp/data/local/dao/{Child,Category,PointRule,PointRecord}Dao.kt`

### Data — Repository / Model / Backup
- `app/src/main/java/com/example/jifenapp/data/repository/{Child,Category,PointRule,PointRecord}Repository.kt`
- `app/src/main/java/com/example/jifenapp/data/model/{ChildWithStats,DailyPointTotal,CategoryShare}.kt`
- `app/src/main/java/com/example/jifenapp/data/backup/Backup.kt`

### DI
- `app/src/main/java/com/example/jifenapp/di/AppContainer.kt`
- `app/src/main/java/com/example/jifenapp/di/ViewModelFactories.kt`

### UI Screens（每个 feature 一个目录）
- `app/src/main/java/com/example/jifenapp/ui/home/{HomeScreen,HomeViewModel,HomeUiState}.kt`
- `app/src/main/java/com/example/jifenapp/ui/child/{ChildDetailScreen,ChildDetailViewModel,ChildEditorDialog}.kt`
- `app/src/main/java/com/example/jifenapp/ui/record/{AddRecordScreen,AddRecordViewModel,RulePickerGrid}.kt`
- `app/src/main/java/com/example/jifenapp/ui/statistics/{StatisticsScreen,StatisticsViewModel,TrendChart}.kt`
- `app/src/main/java/com/example/jifenapp/ui/rules/{RulesScreen,RulesViewModel,RuleEditorDialog}.kt`
- `app/src/main/java/com/example/jifenapp/ui/settings/{SettingsScreen,SettingsViewModel,BackupExporter}.kt`

### Common Components & Util
- `app/src/main/java/com/example/jifenapp/ui/component/{ChildAvatar,PointBadge,EmptyState,PieChart}.kt`
- `app/src/main/java/com/example/jifenapp/util/{DateExt,JsonFormat}.kt`

### Resources
- `app/src/main/res/xml/file_paths.xml`
- `app/src/main/res/values/{strings,colors,themes}.xml`

---

## 验证方法

### 单元测试（`test/`，用 in-memory Room）
- `PointRecordDaoTest`：`insertRecord_thenTotalIsComputed`（插入 +10/+5/-3，断言 total=12）
- `RuleSnapshot_preservesHistoricalTitle`：规则改名后历史 title 不变
- `RecordsByCategory_groupsCorrectly`：按 categoryId 聚合正确
- ViewModel 测试用 `MainDispatcherRule` + fake repository

### UI 测试（`androidTest/`，Compose Test）
- `HomeScreenTest`：空状态 → 添加孩子 → 卡片显示
- `AddRecordFlowTest`：选规则 → 保存 → 流水列表出现
- `StatisticsScreenTest`：插若干数据 → 断言饼图扇区/折线点数

### 手动验证清单（核心闭环）
1. 添加孩子 A、B、C，各选不同头像/颜色
2. 添加分类：学习/生活/运动；添加 6 条模板（3 正 3 负，跨 3 分类）
3. 对 A 加流水 → 积分计算正确（实时响应）
4. 修改模板分数 +5 → -3，验证 A 历史流水分数不变
5. 删除模板，验证历史流水 title 仍可读、ruleId=null
6. 删除孩子 A，验证其流水全部消失（CASCADE）
7. 统计页：今日/本周/本月切换，4 卡片数值正确
8. 饼图扇区比例目测正确，折线图近 7 天曲线符合预期
9. 切换深色模式，所有页面无对比度问题
10. 导出 JSON → 文件存在 Downloads 且可解析；导入 JSON → 数据库还原
11. 杀进程冷启动 → 还原到上次状态

---

## 关键决策摘要

| 决策点 | 选择 | 关键理由 |
|--------|------|---------|
| 图表库 | Vico（折线）+ 自绘饼图 | Compose 原生、主题适配；Vico 饼图弱，自绘更可控 |
| DI | 手动 AppContainer | 项目规模小、零额外编译开销 |
| 时间库 | kotlinx-datetime | 跨平台、API 现代、避免 desugar |
| 序列化 | kotlinx-serialization | 无反射、速度快 |
| 单/多 Activity | 单 Activity + Navigation Compose | 现代标准做法 |
| 数据迁移 | Room v1 schema 固定 | v2 再做迁移 |
| 备份 | JSON + FileProvider | 简单、可读、跨 App 互导 |
| minSdk | 26 | 覆盖广、API 现代 |

---

## 后续可扩展（v2+，不在本期范围）

- 奖励兑换系统（孩子用积分换奖品）
- 提醒/通知（每日打卡提醒）
- 多设备同步（若未来加云端）
- 拖拽排序、长按删除手势
- 图表交互（点击扇区、缩放折线）
- 思源黑体字体集成
- Compose Performance Baseline（Baseline Profile 启动优化）