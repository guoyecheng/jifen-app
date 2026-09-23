# 宝贝积分 (jifen-app)

一个用于管理小孩积分的 Android 单机 App。

- **平台**：原生 Android (Kotlin + Jetpack Compose + Room)
- **模式**：纯单机离线
- **功能**：积分添加/扣减、行为模板、统计、备份导出

## 文档

- [可交互原型](docs/prototype.html) — 浏览器打开，高保真可点击的 5 个页面 + 2 个弹窗
- [原型设计文档](docs/prototype-design.md) — 视觉规范、组件库、关键页面 ASCII 线框图
- [实现计划](docs/implementation-plan.md) — 完整的技术方案与分迭代计划

## 当前进度

✅ **迭代 1（MVP）完成**
- 项目骨架（Gradle / AGP 8.7 / Kotlin 2.0）
- 数据层（Room + Repository）
- 主题与设计系统
- 三个核心页面：首页 / 孩子详情 / 记一笔
- 通用组件（骨架屏、动画数字、空状态等）

⏳ 待开始
- 迭代 2：多孩完善 + 行为模板
- 迭代 3：统计图表
- 迭代 4：设置 + 备份导出

## 构建运行

```bash
# 在 Android Studio 中导入项目，等待 Gradle 同步完成
# 或命令行：
./gradlew assembleDebug
./gradlew installDebug   # 安装到连接的设备/模拟器
```

## 目录结构

```
app/src/main/java/com/example/jifenapp/
├── JifenApplication.kt       # Application + DI 容器
├── MainActivity.kt           # 单 Activity
├── data/
│   ├── local/                # Room (entity/dao/database)
│   ├── repository/
│   ├── model/
│   └── backup/
├── ui/
│   ├── theme/                # 色彩 / 排版 / 主题
│   ├── component/            # 通用组件
│   ├── home/                 # 首页
│   ├── child/                # 孩子详情
│   └── record/               # 记一笔
├── navigation/               # 路由
├── di/                       # 依赖容器
└── util/                     # 工具（时间、JSON）
```
