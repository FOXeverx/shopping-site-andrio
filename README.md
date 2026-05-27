# Shopping Site Andrio

Android 电商应用，基于 **Jetpack Compose + Material Design 3 + MVVM Clean Architecture**。

## 技术栈

| 类别 | 技术 |
|------|------|
| UI | Jetpack Compose + Material Design 3 |
| 架构 | MVVM + Clean Architecture (data/domain/ui) |
| 依赖注入 | Hilt |
| 网络 | Retrofit + OkHttp |
| 本地存储 | DataStore Preferences |
| 状态管理 | StateFlow + UiState 模式 |
| 分页 | Paging3 |
| 图片加载 | Coil |
| 导航 | Navigation Compose |

## 项目结构

```
app/src/main/java/com/example/shopping_site_andrio/
├── data/
│   ├── api/            # Retrofit 接口 + 拦截器 + safeApiCall
│   ├── datastore/      # Token 本地存储
│   ├── model/          # DTO 数据传输对象
│   ├── paging/         # PagingSource
│   └── repository/     # 数据仓库层
├── domain/
│   ├── model/          # 领域模型 + UiState
│   └── usecase/        # 用例
├── viewmodel/          # ViewModels
├── navigation/         # 导航图 + 路由定义
├── di/                 # Hilt 依赖注入模块
├── ui/
│   ├── component/      # 可复用组件
│   ├── screen/         # 页面 (login/home/detail/cart/order/profile)
│   └── theme/          # Material3 主题
├── MainActivity.kt
└── ShoppingApplication.kt
```

## 功能模块

### 认证模块
- 登录 `/api/auth/login`
- 注册 `/api/auth/register`
- 找回密码 `/api/auth/forgot-password`
- 登出 `/api/auth/logout`
- 获取用户信息 `/api/auth/me`
- 修改密码 `/api/auth/change-password`
- Token 自动附加（AuthInterceptor）
- 401 自动清除 Token 并强制登录

### 商品模块
- 商品列表（Paging3 分页）
- 搜索（SearchBar）
- 筛选（FilterChip：价格区间、排序）
- 商品详情（图片、标题、价格、描述、库存）

### 推荐系统
- 相关推荐 `/api/recommend/product/{id}` — 横向滑动展示
- 浏览者也买了 `/api/recommend/bought-also/{id}`
- 个性化推荐 `/api/recommend/user/me` — 展示推荐理由

### 评论模块
- 获取评论列表
- 发布评论

### 购物车模块
- 添加商品
- 修改数量
- 删除商品
- 实时价格计算

### 订单模块
- 创建订单
- 订单列表
- 订单状态展示

### 用户模块
- 查看/修改个人信息
- 修改密码（邮箱验证码）
- 登出

### 行为日志
- 进入商品详情 → 记录浏览
- 离开时记录停留时间

## UI 特性

- Material Design 3 组件：Scaffold, TopAppBar, NavigationBar, SearchBar, FilterChip, ElevatedCard, FilledTonalButton, Snackbar
- Skeleton Loading（骨架屏）
- EmptyState（空状态页面）
- ErrorView（错误重试）
- 页面切换动画：fade + slide
- 圆角 12dp，间距 8dp/16dp

## 安全机制

| 机制 | 说明 |
|------|------|
| JWT Token | 登录后自动保存，请求时自动附加 |
| 401 处理 | 自动清除 Token，跳转登录页 |
| 429 处理 | 提示"请求过于频繁" |
| 错误封装 | safeApiCall() 统一处理网络/HTTP/业务错误 |
| HTTPS | 支持 cleartext traffic（开发环境） |

## 后端 API

后端服务需在 `app/build.gradle.kts` 的 `buildConfigField` 定义。所有接口遵循统一响应格式：

```json
{
    "success": true,
    "data": {},
    "message": "",
    "error": { "code": "", "message": "" }
}
```

详细 API 文档见 `docs/api.md`。

## 构建与运行

1. 用 Android Studio 打开项目
2. 配置后端 API 地址（`app/src/main/java/.../data/config/AppConfig.kt` 中的 `BASE_URL`，实际值由 `app/build.gradle.kts` 的 `buildConfigField` 定义）
3. 运行 `app` 模块

```bash
# 或使用命令行
./gradlew assembleDebug
```

## 要求

- Android SDK 24+
- Kotlin 2.2+
- Gradle 9.1+
