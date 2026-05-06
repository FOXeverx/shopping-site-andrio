# API设计文档

## 一、API概览

- **基础URL**: `/api`
- **认证方式**: JWT Bearer Token
- **请求格式**: JSON
- **响应格式**: JSON

---

## 二、通用响应格式

### 成功响应

```json
{
  "success": true,
  "data": { },
  "message": "Success"
}
```

### 错误响应

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Error message"
  }
}
```

### 分页响应

```json
{
  "success": true,
  "data": [ ],
  "pagination": {
    "page": 1,
    "page_size": 20,
    "total": 100,
    "total_pages": 5
  }
}
```

---

## 三、认证接口

### 1. 用户注册

| 项目 | 内容 |
|------|------|
| **URL** | `/api/auth/register` |
| **方法** | POST |
| **权限** | 公开 |

**请求体**:

```json
{
  "username": "user123",
  "email": "user@example.com",
  "password": "securePassword123",
  "confirm_password": "securePassword123"
}
```

**响应 (201)**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "user123",
    "email": "user@example.com",
    "role": "customer",
    "created_at": "2024-01-01T00:00:00Z"
  },
  "message": "Registration successful"
}
```

---

### 2. 用户登录

| 项目 | 内容 |
|------|------|
| **URL** | `/api/auth/login` |
| **方法** | POST |
| **权限** | 公开 |

**请求体**:

```json
{
  "username": "user123",
  "password": "securePassword123"
}
```

**说明**：
- `username` 支持用户名或邮箱登录

**响应 (200)**:

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "token_type": "bearer",
    "expires_in": 86400,
    "user": {
      "id": 1,
      "username": "user123",
      "email": "user@example.com",
      "role": "customer"
    }
  },
  "message": "Login successful"
}
```

---

### 3. 忘记密码

| 项目 | 内容 |
|------|------|
| **URL** | `/api/auth/forgot-password` |
| **方法** | POST |
| **权限** | 公开 |

**请求体**:

```json
{
  "username": "user123"
}
```

**说明**：
- `username` 支持用户名或注册邮箱
- 系统将随机生成新密码并发送到用户注册邮箱

**响应 (200)**:

```json
{
  "success": true,
  "message": "If the account exists, a new password will be sent to the registered email"
}
```

---

### 4. 用户注销

| 项目 | 内容 |
|------|------|
| **URL** | `/api/auth/logout` |
| **方法** | POST |
| **权限** | 需认证 |

**请求头**:

```
Authorization: Bearer <token>
```

**响应 (200)**:

```json
{
  "success": true,
  "message": "Logout successful"
}
```

---

### 5. 发送修改密码验证码

| 项目 | 内容 |
|------|------|
| **URL** | `/api/auth/send-change-password-code` |
| **方法** | POST |
| **权限** | 需认证 |

**响应 (200)**:

```json
{
  "success": true,
  "message": "Verification code sent to your email"
}
```

---

### 6. 修改密码

| 项目 | 内容 |
|------|------|
| **URL** | `/api/auth/change-password` |
| **方法** | POST |
| **权限** | 需认证 |

**请求体**:

```json
{
  "old_password": "oldPassword123",
  "new_password": "newPassword456",
  "verification_code": "123456"
}
```

**说明**：
- 需要先获取邮箱验证码

**响应 (200)**:

```json
{
  "success": true,
  "message": "Password changed successfully"
}
```

---

## 四、用户接口

### 7. 获取当前用户信息

| 项目 | 内容 |
|------|------|
| **URL** | `/api/auth/me` |
| **方法** | GET |
| **权限** | 需认证 |

**响应 (200)**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "user123",
    "email": "user@example.com",
    "role": "customer",
    "is_active": true,
    "last_login_at": "2024-01-01T00:00:00Z",
    "created_at": "2024-01-01T00:00:00Z"
  }
}
```

---

### 8. 更新用户信息

| 项目 | 内容 |
|------|------|
| **URL** | `/api/user/me` |
| **方法** | PUT |
| **权限** | 需认证 |

**请求体**:

```json
{
  "email": "newemail@example.com"
}
```

**响应 (200)**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "username": "user123",
    "email": "newemail@example.com"
  },
  "message": "User updated successfully"
}
```

---

### 9. 删除账户（永久注销）

| 项目 | 内容 |
|------|------|
| **URL** | `/api/user/me` |
| **方法** | DELETE |
| **权限** | 需认证 |

**请求体**:

```json
{
  "password": "your_password"
}
```

**说明**：
- 注销采用匿名化处理，保留订单等业务数据
- 用户名标记为 `deleted_{id}_{timestamp}`，邮箱和密码置空
- 购物车数据会被清空
- 禁止登录，但订单记录仍保留用于数据分析

**响应 (200)**:

```json
{
  "success": true,
  "message": "Account deleted successfully"
}
```

---

## 五、图片上传接口

### 1. 上传商品图片

| 项目 | 内容 |
|------|------|
| **URL** | `/api/upload/image` |
| **方法** | POST |
| **权限** | 需认证 (sales/admin) |

**请求体**: `multipart/form-data`

- `file`: 图片文件 (jpg, png, gif, webp)

**响应 (200)**:

```json
{
  "success": true,
  "data": {
    "url": "/uploads/abc123.jpg",
    "filename": "abc123.jpg"
  }
}
```

**响应 (200)**:

```json
{
  "success": true,
  "message": "Password changed successfully"
}
```

---

## 五、商品接口

### 7. 获取商品列表

| 项目 | 内容 |
|------|------|
| **URL** | `/api/product` |
| **方法** | GET |
| **权限** | 公开 |

---

### 8. 获取商品详情

| 项目 | 内容 |
|------|------|
| **URL** | `/api/product/{product_id}` |
| **方法** | GET |
| **权限** | 公开 |

**响应 (200)**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "iPhone 15",
    "description": "Latest iPhone",
    "price": 999.99,
    "stock": 100,
    "image_url": "/uploads/iphone15.jpg",
    "created_at": "2024-01-01T00:00:00Z"
  }
}
```

---

### 9. 获取商品评论

| 项目 | 内容 |
|------|------|
| **URL** | `/api/product/{product_id}/comments` |
| **方法** | GET |
| **权限** | 公开 |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "product_id": 1,
      "user_id": 2,
      "username": "user123",
      "content": "Great product!",
      "created_at": "2024-01-01T00:00:00Z"
    }
  ]
}
```

---

### 10. 添加商品评论

| 项目 | 内容 |
|------|------|
| **URL** | `/api/product/{product_id}/comments` |
| **方法** | POST |
| **权限** | 需认证 |

**请求体**:

```json
{
  "content": "This product is amazing!"
}
```

**响应 (201)**:

```json
{
  "success": true,
  "data": {
    "id": 1,
    "product_id": 1,
    "user_id": 2,
    "username": "user123",
    "content": "This product is amazing!",
    "created_at": "2024-01-01T00:00:00Z"
  },
  "message": "Comment added successfully"
}
```

---

### 11. 删除商品评论

| 项目 | 内容 |
|------|------|
| **URL** | `/api/product/comments/{comment_id}` |
| **方法** | DELETE |
| **权限** | sales/admin |

**响应 (200)**:

```json
{
  "success": true,
  "message": "Comment deleted successfully"
}
```

---

**查询参数**:

| 参数 | 类型 | 默认 | 说明 |
|------|------|------|------|
| page | int | 1 | 页码 |
| page_size | int | 20 | 每页数量 |
| category_id | int | - | 分类ID |
| search | string | - | 搜索关键词 |
| min_price | decimal | - | 最低价格 |
| max_price | decimal | - | 最高价格 |
| sort | string | created_at | 排序字段 |
| order | string | desc | 排序方向 |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "user_id": 1,
      "username": "admin",
      "action": "UPDATE_PRODUCT",
      "target_type": "product",
      "target_id": 1,
      "created_at": "2024-01-01T00:00:00Z"
    }
  ]
}
```

---

### 32. 获取用户列表（销售版）

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/users/simple` |
| **方法** | GET |
| **权限** | sales/admin |

**查询参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| search | string | 用户名/邮箱搜索 |
| include_inactive | boolean | 是否包含已删除/禁用用户，默认 false |
| page | int | 页码 |
| page_size | int | 每页数量 |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "username": "user123",
      "email": "user@example.com",
      "role": "customer",
      "created_at": "2024-01-01T00:00:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "page_size": 20,
    "total": 100
  }
}
```

---

### 33. 获取用户浏览记录

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/user/{user_id}/browse` |
| **方法** | GET |
| **权限** | sales/admin |

**查询参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| page | int | 页码 |
| page_size | int | 每页数量 |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "product_id": 1,
      "product_name": "iPhone 15",
      "stay_time": 120,
      "created_at": "2024-01-01T00:00:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "page_size": 20,
    "total": 100
  }
}
```

---

### 34. 获取用户登录记录

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/user/{user_id}/logins` |
| **方法** | GET |
| **权限** | sales/admin |

**查询参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| page | int | 页码 |
| page_size | int | 每页数量 |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "ip_address": "192.168.1.1",
      "user_agent": "Mozilla/5.0...",
      "success": true,
      "created_at": "2024-01-01T00:00:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "page_size": 20,
    "total": 100
  }
}
```

---

### 35. 获取用户购买统计

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/user/{user_id}/purchases/summary` |
| **方法** | GET |
| **权限** | sales/admin |

**说明**：只返回已确认的订单，按金额降序排列

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "category_id": 1,
      "category_name": "电子产品",
      "total_quantity": 3,
      "total_amount": 5999.00
    },
    {
      "category_id": 2,
      "category_name": "配件",
      "total_quantity": 5,
      "total_amount": 450.00
    }
  ]
}
```

---

### 36. 获取用户购买详情

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/user/{user_id}/purchases/{category_id}` |
| **方法** | GET |
| **权限** | sales/admin |

**查询参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| page | int | 页码 |
| page_size | int | 每页数量 |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "order_id": 1,
      "order_number": "ORD00000001",
      "created_at": "2024-01-01T00:00:00Z",
      "product_id": 1,
      "product_name": "iPhone 15",
      "quantity": 1,
      "price": 9999.00,
      "subtotal": 9999.00
    }
  ],
  "pagination": {
    "page": 1,
    "page_size": 20,
    "total": 100
  }
}
```

---

### 33. 获取用户列表

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/users` |
| **方法** | GET |
| **权限** | Admin |

**查询参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| `role` | string | 按角色筛选（customer/sales/admin） |
| `include_inactive` | boolean | 是否包含已删除/禁用用户，默认 false |
| `page` | integer | 页码，默认 1 |
| `page_size` | integer | 每页数量，默认 20 |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "username": "user123",
      "email": "user@example.com",
      "role": "customer",
      "is_active": true,
      "created_at": "2024-01-01T00:00:00Z"
    }
  ]
}
```

---

### 34. 创建用户

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/user` |
| **方法** | POST |
| **权限** | Admin |

**请求体**:

```json
{
  "username": "sales1",
  "email": "sales@shop.com",
  "password": "password123",
  "role": "sales"
}
```

**响应 (201)**:

```json
{
  "success": true,
  "data": {
    "id": 2,
    "username": "sales1",
    "role": "sales"
  },
  "message": "User created successfully"
}
```

**错误响应 (400)**:

```json
{
  "detail": "Username or email already exists"
}
```

---

### 35. 更新用户

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/user/{user_id}` |
| **方法** | PUT |
| **权限** | Admin |

**请求体（所有字段可选）**:

```json
{
  "email": "newemail@shop.com",
  "password": "newpassword123",
  "role": "admin"
}
```

**响应 (200)**:

```json
{
  "success": true,
  "data": {
    "id": 2,
    "username": "sales1",
    "email": "newemail@shop.com",
    "role": "admin"
  },
  "message": "User updated successfully"
}
```

---

### 36. 删除用户（软删除）

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/user/{user_id}` |
| **方法** | DELETE |
| **权限** | Admin |

> 软删除会将用户设为禁用状态，**清除密码哈希**，并**匿名化用户名和邮箱**，
> 使其无法登录。被删除用户默认不会显示在用户列表中。

**响应 (200)**:

```json
{
  "success": true,
  "message": "User deleted"
}
```

**错误响应 (400)**:

```json
{
  "detail": "Cannot delete yourself"
}
```

---

### 31. 获取操作日志

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/logs` |
| **方法** | GET |
| **权限** | Admin |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "user_id": 1,
      "username": "admin",
      "action": "UPDATE_PRODUCT",
      "target_type": "product",
      "target_id": 1,
      "created_at": "2024-01-01T00:00:00Z"
    }
  ]
}
```

---

### 32. 获取商品相关推荐（共现推荐）

| 项目 | 内容 |
|------|------|
| **URL** | `/api/recommend/product/{product_id}` |
| **方法** | GET |
| **权限** | 公开 |

**参数**：
- `product_id`: 商品ID
- `limit`: 返回数量（默认5）

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "product_id": 2,
      "product_name": "iPhone 15 Case",
      "score": 0.85,
      "image_url": "/images/case.jpg"
    }
  ]
}
```

---

### 33. 获取浏览过此商品的用户也买了

| 项目 | 内容 |
|------|------|
| **URL** | `/api/recommend/bought-also/{product_id}` |
| **方法** | GET |
| **权限** | 公开（登录后可排除自己的购买记录） |

**参数**：
- `product_id`: 商品ID
- `limit`: 返回数量（默认5）

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "product_id": 5,
      "product_name": "Perfume",
      "score": 3,
      "image_url": "/images/perfume.jpg",
      "reason": "3人购买"
    }
  ]
}
```

---

### 34. 获取用户个性化推荐

| 项目 | 内容 |
|------|------|
| **URL** | `/api/recommend/user/me` |
| **方法** | GET |
| **权限** | 需要登录 |

**参数**：
- `limit`: 返回数量（默认10）

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "product_id": 4,
      "product_name": "MacBook Air",
      "score": 0.95,
      "reason": "Based on your purchases"
    }
  ]
}
```

---

### 36. 手动触发推荐系统

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/recommend/trigger` |
| **方法** | POST |
| **权限** | Admin |

**响应 (200)**:

```json
{
  "success": true,
  "message": "Recommendation completed: 150 items created",
  "data": {
    "cooccurrence_items": 150
  }
}
```

---

### 33. 获取异常日志

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/anomalies` |
| **方法** | GET |
| **权限** | Admin |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "anomaly_type": "UNUSUAL_PURCHASE",
      "description": "User purchased 100 items in 1 minute",
      "severity": "high",
      "is_resolved": false,
      "created_at": "2024-01-01T00:00:00Z"
    }
  ]
}
```

---

## 十一、错误码说明

| 错误码 | HTTP状态码 | 说明 |
|--------|------------|------|
| AUTH_001 | 401 | 无效的token |
| AUTH_002 | 401 | token已过期 |
| AUTH_003 | 400 | 用户名已存在 |
| AUTH_004 | 400 | 邮箱已存在 |
| AUTH_005 | 401 | 登录失败 |
| AUTH_006 | 403 | 账号已被封禁 |
| USER_001 | 404 | 用户不存在 |
| USER_002 | 400 | 旧密码错误 |
| PRODUCT_001 | 404 | 商品不存在 |
| PRODUCT_002 | 400 | 库存不足 |
| CART_001 | 400 | 购物车为空 |
| ORDER_001 | 400 | 订单已过期 |
| ORDER_002 | 400 | 订单状态错误 |
| ORDER_003 | 400 | 库存不足，无法确认 |
| PERM_001 | 403 | 权限不足 |
| SYS_001 | 429 | 请求过于频繁 |
| SYS_002 | 403 | IP被封禁 |
| VAL_001 | 400 | 参数验证失败 |

---

## 十二、请求频率限制

| 接口 | 限制 |
|------|------|
| 登录接口 | 5次/分钟 |
| 注册接口 | 3次/分钟 |
| 商品查询 | 60次/分钟 |
| 下单接口 | 10次/分钟 |

---

## 十三、数据分析接口

### 1. 用户画像统计

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/user-stats` |
| **方法** | GET |
| **权限** | Admin/Sales |

**响应 (200)**:

```json
{
  "success": true,
  "data": {
    "total_users": 100,
    "spending_distribution": {
      "low": 60,
      "medium": 30,
      "high": 10
    },
    "avg_spent": 850.50,
    "avg_orders": 3.2,
    "region_distribution": {
      "内网": 20,
      "局域网": 50,
      "其他": 30
    }
  }
}
```

---

### 2. 异常统计

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/anomaly-stats` |
| **方法** | GET |
| **权限** | Admin/Sales |

**响应 (200)**:

```json
{
  "success": true,
  "data": {
    "total": 50,
    "unresolved": 5,
    "by_severity": {
      "high": 2,
      "medium": 3,
      "low": 0
    },
    "last_24h": 10
  }
}
```

---

### 3. 处理异常

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/anomaly/{id}/resolve` |
| **方法** | POST |
| **权限** | Admin |

**响应 (200)**:

```json
{
  "success": true,
  "message": "Anomaly resolved"
}
```

---

### 4. 销售预测

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/sales-predict` |
| **方法** | GET |
| **权限** | Admin/Sales |

**查询参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| days | int | 预测天数，默认7 |

**响应 (200)**:

```json
{
  "success": true,
  "data": {
    "trend": "increasing",
    "current_avg": 5000.00,
    "prediction": 5250.00,
    "confidence": 70,
    "recent_data": [
      {"date": "2026-04-05", "amount": 4500.00},
      {"date": "2026-04-06", "amount": 4800.00}
    ]
  }
}
```

---

### 5. 用户浏览日志

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/logs/browse` |
| **方法** | GET |
| **权限** | Admin/Sales |

**查询参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| page | int | 页码 |
| page_size | int | 每页数量 |
| product_id | int | 商品ID筛选 |
| user_id | int | 用户ID筛选 |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "user_id": 5,
      "username": "user123",
      "product_id": 10,
      "product_name": "iPhone 15",
      "stay_time": 120,
      "created_at": "2026-04-11T10:30:00"
    }
  ],
  "pagination": {
    "page": 1,
    "page_size": 20,
    "total": 100
  }
}
```

---

## 十四、安全头

```
X-API-Key: <optional>
Authorization: Bearer <token>
Content-Type: application/json
X-Request-ID: <uuid>
X-Timestamp: <unix_timestamp>
```

---

## 十五、安全威胁接口

### 1. 获取威胁列表

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/security/threats` |
| **方法** | GET |
| **权限** | Admin |

**查询参数**:

| 参数 | 类型 | 说明 |
|------|------|------|
| threat_type | string | 威胁类型 |
| severity | string | 严重程度 |
| is_resolved | boolean | 是否处理 |
| page | int | 页码 |
| page_size | int | 每页数量 |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "threat_type": "rate_limit",
      "ip_address": "192.168.1.100",
      "user_agent": "python-requests/2.25.1",
      "details": {"path": "/api/product", "request_count": 65},
      "severity": "medium",
      "is_resolved": false,
      "created_at": "2026-04-16T10:30:00",
      "resolved_at": null
    }
  ],
  "pagination": {
    "page": 1,
    "page_size": 20,
    "total": 100
  }
}
```

---

### 2. 标记威胁已处理

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/security/threats/{threat_id}/resolve` |
| **方法** | POST |
| **权限** | Admin |

**响应 (200)**:

```json
{
  "success": true,
  "message": "Threat resolved"
}
```

---

### 3. 获取威胁统计

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/security/threats/stats` |
| **方法** | GET |
| **权限** | Admin |

**响应 (200)**:

```json
{
  "success": true,
  "data": {
    "total": 150,
    "unresolved": 23,
    "high_critical": 5,
    "today": 12
  }
}
```

---

### 4. 获取IP封禁列表

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/security/ip-blocks` |
| **方法** | GET |
| **权限** | Admin |

**响应 (200)**:

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "ip_address": "192.168.1.100",
      "block_type": "auto",
      "reason": "Rate limit exceeded",
      "expires_at": "2026-04-16T11:00:00",
      "created_by_id": null,
      "created_at": "2026-04-16T10:30:00"
    }
  ],
  "pagination": {
    "page": 1,
    "page_size": 20,
    "total": 10
  }
}
```

---

### 5. 手动封禁IP

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/security/ip-blocks` |
| **方法** | POST |
| **权限** | Admin |

**请求体**:

```json
{
  "ip_address": "192.168.1.100",
  "reason": "恶意攻击",
  "expires_minutes": 1440
}
```

**说明**: `expires_minutes` 为 null 表示永久封禁

**响应 (200)**:

```json
{
  "success": true,
  "message": "IP blocked"
}
```

---

### 6. 解封IP

| 项目 | 内容 |
|------|------|
| **URL** | `/api/admin/security/ip-blocks/{block_id}` |
| **方法** | DELETE |
| **权限** | Admin |

**响应 (200)**:

```json
{
  "success": true,
  "message": "IP unblocked"
}
```