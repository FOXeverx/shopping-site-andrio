# 任务说明（必须完整理解并严格执行）

必须严格依据 docs/ 下的所有文档生成代码，不允许忽略任何模块
每次修改代码后必须执行：
git add .
git commit -m "feat: <本次功能>"

你需要生成一个 **完整的 Android 电商应用（Kotlin）**，基于以下技术栈：

* Jetpack Compose（UI）
* Material Design 3（必须严格遵循）
* MVVM + Clean Architecture
* Hilt（依赖注入）
* Retrofit + OkHttp（网络）
* DataStore（本地存储）
* StateFlow（状态管理）
* Paging3（分页）
* Coil（图片加载）
* Navigation Compose（导航）

⚠️ **重要约束（必须遵守）：**

1. 不允许删除任何功能模块
2. 不允许简化架构（必须分层）
3. 所有接口必须严格按后端API设计实现
4. UI必须符合Material Design 3规范（简洁、克制、现代）
5. 不允许生成“示例代码”，必须是“可运行项目代码”
6. 不允许将业务逻辑写在UI层
7. 所有网络请求必须经过Repository封装
8. 必须实现完整错误处理机制
9. 必须实现Token认证系统
10. 必须支持分页（Paging3），不能一次加载全部数据

---

# 一、项目结构（必须严格生成）

```
app/
 ├── ui/
 │    ├── screen/
 │    │     ├── home/
 │    │     ├── detail/
 │    │     ├── login/
 │    │     ├── cart/
 │    │     ├── profile/
 │    │     └── order/
 │    │
 │    ├── component/
 │    │     ├── ProductCard.kt
 │    │     ├── Loading.kt
 │    │     ├── EmptyState.kt
 │    │     └── ErrorView.kt
 │    │
 │    └── theme/
 │
 ├── data/
 │    ├── api/
 │    ├── repository/
 │    ├── datastore/
 │    ├── paging/
 │    └── model/
 │
 ├── domain/
 │    ├── usecase/
 │    └── model/
 │
 ├── viewmodel/
 │
 ├── navigation/
 │
 ├── di/
 │
 └── MainActivity.kt
```

---

# 二、后端API规范（必须严格遵守）

所有接口遵循统一结构：

```
{
  "success": true,
  "data": {},
  "message": "",
  "error": {
    "code": "",
    "message": ""
  }
}
```

必须实现：

* ApiResponse<T>
* ApiError
* safeApiCall()

---

# 三、必须实现的核心功能（不允许缺失）

## 1. 认证模块（必须完整）

* 登录 `/api/auth/login`
* 注册 `/api/auth/register`
* 获取用户 `/api/auth/me`
* 登出 `/api/auth/logout`

必须实现：

* Token存储（DataStore）
* OkHttp拦截器自动加Token
* 401自动跳转登录
* Token失效自动清除

---

## 2. 商品模块（必须支持分页）

接口：

* `/api/product`
* `/api/product/{id}`

必须实现：

* Paging3分页加载
* 搜索（search）
* 筛选（min_price / max_price）
* 排序（sort / order）

UI必须包含：

* SearchBar（Material3）
* FilterChip
* 商品网格（LazyVerticalGrid）

---

## 3. 商品详情模块

必须包含：

* 商品图片
* 标题 + 价格
* 描述
* 评论列表
* 推荐模块（重点）

---

## 4. 推荐系统（必须完整实现）

接口：

* `/api/recommend/product/{id}`
* `/api/recommend/bought-also/{id}`
* `/api/recommend/user/me`

UI要求：

* 横向滑动（LazyRow）
* 必须显示推荐理由（reason字段）

---

## 5. 评论模块

* 获取评论
* 发布评论

---

## 6. 购物车模块

接口：

* `/api/cart`

必须实现：

* 添加商品
* 修改数量
* 删除商品
* 实时价格计算

---

## 7. 订单模块

接口：

* `/api/order`

必须实现：

* 创建订单
* 订单列表
* 订单详情
* DeepLink订单确认

---

## 8. 用户模块

* 用户信息
* 修改信息
* 修改密码

---

## 9. 行为日志（必须实现）

* 进入商品详情 → 记录浏览
* 记录停留时间

---

# 四、网络层设计（必须实现）

必须包含：

* Retrofit接口
* AuthInterceptor
* LoggingInterceptor
* safeApiCall封装

---

# 五、状态管理（必须严格实现）

必须使用：

* StateFlow
* UI State模式

示例：

```
data class UiState<T>(
    val loading: Boolean,
    val data: T?,
    val error: String?
)
```

---

# 六、Material Design 3 UI规范（必须遵守）

## 1. 设计要求

* 极简风格（不能复杂）
* 每屏一个核心任务
* 不超过3层嵌套
* 圆角：12dp
* 间距：8dp / 16dp

---

## 2. 必须使用组件

* Scaffold
* TopAppBar
* NavigationBar
* SearchBar（Material3）
* FilterChip
* ElevatedCard
* FilledTonalButton
* Snackbar

---

## 3. 必须实现

* Skeleton Loading（加载占位）
* 空状态页面
* 错误提示（Snackbar）

---

## 4. 动效要求

* 页面切换：fade + slide
* 列表动画：轻量

禁止复杂动画

---

# 七、导航系统

必须实现：

```
login
home
product_detail/{id}
cart
profile
order_list
```

---

# 八、性能要求

必须实现：

* Paging3分页
* 图片缓存（Coil）
* 请求去重
* 错误重试机制

---

# 九、安全机制

必须实现：

* 401 → 强制登录
* 429 → 提示“请求过于频繁”
* Token自动刷新或失效处理

---

# 十、输出要求（非常重要）

你必须输出：

1. 完整项目代码（不是片段）
2. 每个文件完整内容
3. 可直接导入Android Studio运行
4. 不允许省略任何模块
5. 不允许使用伪代码

---

# 十一、禁止行为

❌ 不允许省略分页
❌ 不允许省略推荐系统
❌ 不允许简化UI
❌ 不允许不写Repository层
❌ 不允许把逻辑写在Composable中
❌ 不允许只实现部分API

---

# 十二、最终目标

生成一个：

✔ 可运行
✔ 架构规范
✔ UI现代（Material3）
✔ 功能完整（电商闭环）
✔ 可扩展

的 Android 应用项目
