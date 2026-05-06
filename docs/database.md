# 数据库设计文档

## 一、数据库概述

- **数据库类型**: MySQL 8.0+
- **字符集**: utf8mb4
- **存储引擎**: InnoDB
- **数据库名**: shopping_site

---

## 二、表结构

### 1. user - 用户表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| username | VARCHAR(50) | 否 | | 用户名（唯一） |
| password_hash | VARCHAR(255) | 否 | | BCrypt哈希密码 |
| email | VARCHAR(100) | 否 | | 邮箱（唯一） |
| role_id | INT | 否 | 1 | 角色ID |
| is_active | BOOLEAN | 否 | TRUE | 账户状态 |
| last_login_at | DATETIME | 是 | NULL | 最后登录时间 |
| login_attempts | INT | 否 | 0 | 登录失败次数 |
| blocked_until | DATETIME | 是 | NULL | 封禁截止时间 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**:
- `idx_username` (username)
- `idx_email` (email)
- `idx_role_id` (role_id)

**外键**:
- `role_id` → `role(id)`

---

### 2. role - 角色表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| name | VARCHAR(50) | 否 | | 角色名 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 更新时间 |

**角色类型**:
- `customer` - 普通客户
- `sales` - 销售人员
- `admin` - 管理员

---

### 3. product - 商品表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| name | VARCHAR(200) | 否 | | 商品名称 |
| description | TEXT | 是 | NULL | 商品描述 |
| price | DECIMAL(10,2) | 否 | | 商品价格 |
| stock | INT | 否 | 0 | 库存数量 |
| category_id | INT | 是 | NULL | 分类ID |
| image_url | VARCHAR(500) | 是 | NULL | 商品图片URL |
| is_active | BOOLEAN | 否 | TRUE | 上架状态 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**:
- `idx_name` (name)
- `idx_category_id` (category_id)
- `idx_price` (price)
- `idx_is_active` (is_active)

**外键**:
- `category_id` → `category(id)`

---

### 4. comment - 商品评论表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| product_id | INT | 否 | | 商品ID |
| user_id | INT | 否 | | 用户ID |
| content | TEXT | 否 | | 评论内容 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 更新时间 |

**索引**:
- `idx_product_id` (product_id)
- `idx_user_id` (user_id)

**外键**:
- `product_id` → `product(id)` ON DELETE CASCADE
- `user_id` → `user(id)`

---

### 5. category - 分类表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| name | VARCHAR(100) | 否 | | 分类名称 |
| parent_id | INT | 是 | NULL | 父分类ID |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 更新时间 |

**层级**: 支持多级分类（parent_id自关联）

---

### 6. cart - 购物车表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| user_id | INT | 否 | | 用户ID |
| product_id | INT | 否 | | 商品ID |
| quantity | INT | 否 | 1 | 数量 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 更新时间 |

**唯一键**: `(user_id, product_id)`

**外键**:
- `user_id` → `user(id)` ON DELETE CASCADE
- `product_id` → `product(id)` ON DELETE CASCADE

---

### 7. order - 订单表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| user_id | INT | 否 | | 用户ID |
| status | ENUM | 否 | CREATED | 订单状态 |
| total_amount | DECIMAL(10,2) | 否 | | 订单总额 |
| confirm_token | VARCHAR(255) | 是 | NULL | 确认token |
| shipping_address | TEXT | 是 | NULL | 收货地址 |
| note | TEXT | 是 | NULL | 备注 |
| confirmed_at | DATETIME | 是 | NULL | 确认时间 |
| expires_at | DATETIME | 否 | | 过期时间 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 更新时间 |

**订单状态**:
- `CREATED` - 已创建（待确认）
- `CONFIRMED` - 已确认
- `SHIPPED` - 已发货
- `COMPLETED` - 已完成
- `CANCELLED` - 已取消

**生命周期**: 创建时扣减库存；创建后24小时未确认自动过期并恢复库存

**外键**:
- `user_id` → `user(id)`

---

### 8. order_item - 订单项表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| order_id | INT | 否 | | 订单ID |
| product_id | INT | 否 | | 商品ID |
| quantity | INT | 否 | | 数量 |
| price | DECIMAL(10,2) | 否 | | 下单时价格 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |

**外键**:
- `order_id` → `order(id)` ON DELETE CASCADE
- `product_id` → `product(id)`

---

### 9. login_log - 登录日志表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| user_id | INT | 是 | NULL | 用户ID |
| username | VARCHAR(50) | 否 | | 尝试用户名 |
| ip_address | VARCHAR(45) | 否 | | 客户端IP |
| user_agent | VARCHAR(500) | 是 | NULL | User-Agent |
| success | BOOLEAN | 否 | | 是否成功 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |

**外键**:
- `user_id` → `user(id)` ON DELETE SET NULL

---

### 10. browse_log - 浏览日志表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| user_id | INT | 否 | | 用户ID |
| product_id | INT | 否 | | 商品ID |
| stay_time | INT | 否 | 0 | 停留秒数 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |

**外键**:
- `user_id` → `user(id)` ON DELETE CASCADE
- `product_id` → `product(id)` ON DELETE CASCADE

---

### 11. operation_log - 操作日志表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| user_id | INT | 否 | | 用户ID |
| action | VARCHAR(100) | 否 | | 操作类型 |
| target_type | VARCHAR(50) | 是 | NULL | 目标类型 |
| target_id | INT | 是 | NULL | 目标ID |
| details | TEXT | 是 | NULL | 详情JSON |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |

**外键**:
- `user_id` → `user(id)` ON DELETE CASCADE

---

### 12. user_profile - 用户画像表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| user_id | INT | 否 | | 用户ID（唯一） |
| region | VARCHAR(50) | 是 | NULL | 地域 |
| total_spent | DECIMAL(12,2) | 否 | 0 | 总消费金额 |
| order_count | INT | 否 | 0 | 订单数 |
| avg_order_amount | DECIMAL(12,2) | 否 | 0 | 平均订单金额 |
| preferred_categories | JSON | 是 | NULL | 偏好分类 |
| browse_category_stats | JSON | 是 | NULL | 浏览统计 |
| spending_level | ENUM | 否 | low | 消费水平 |
| last_updated | DATETIME | 是 | NULL | 最后更新时间 |

**消费水平**: low / medium / high

**外键**:
- `user_id` → `user(id)` ON DELETE CASCADE

**索引**:
- `idx_user_id` (user_id)
- `idx_spending_level` (spending_level)
- `idx_region` (region)

---

### 13. sales_stat - 销售统计表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| stat_date | DATETIME | 否 | | 统计日期 |
| total_amount | DECIMAL(12,2) | 否 | 0 | 销售总额 |
| order_count | INT | 否 | 0 | 订单数 |
| user_count | INT | 否 | 0 | 购买用户数 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 更新时间 |

---

### 14. product_rank - 商品排行表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| product_id | INT | 否 | | 商品ID |
| rank_score | DECIMAL(10,2) | 否 | 0 | 排行分数 |
| rank_position | INT | 是 | NULL | 排行位置 |
| period | ENUM | 否 | daily | 统计周期 |
| last_analysis_at | DATETIME | 是 | NULL | 最后分析时间 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 更新时时间 |

**周期**: daily / weekly / monthly

**唯一键**: `(product_id)` - 每个商品每日排行唯一

**外键**:
- `product_id` → `product(id)` ON DELETE CASCADE

---

### 15. anomaly_log - 异常日志表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| anomaly_type | VARCHAR(100) | 否 | | 异常类型 |
| description | TEXT | 否 | | 异常描述 |
| severity | ENUM | 否 | medium | 严重程度 |
| details | JSON | 是 | NULL | 详情 |
| is_resolved | BOOLEAN | 否 | FALSE | 是否解决 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| resolved_at | DATETIME | 是 | NULL | 解决时间 |

**严重程度**: low / medium / high

---

### 16. recommend_item - 推荐结果表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| product_id | INT | 否 | | 源商品ID |
| recommend_product_id | INT | 否 | | 推荐商品ID |
| score | DECIMAL(5,4) | 否 | 0 | 推荐分数 |
| algorithm | VARCHAR(50) | 否 | cooccurrence | 算法 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 更新时间 |

**算法**: cooccurrence / collaborative

**唯一键**: `(product_id, recommend_product_id)`

**外键**:
- `product_id` → `product(id)`
- `recommend_product_id` → `product(id)`

---

### 17. verification_code - 验证码表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| email | VARCHAR(100) | 否 | | 邮箱 |
| code | VARCHAR(6) | 否 | | 验证码 |
| purpose | VARCHAR(20) | 否 | register | 用途 |
| expires_at | DATETIME | 否 | | 过期时间 |
| is_used | BOOLEAN | 否 | FALSE | 是否使用 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |

---

### 18. security_threat - 安全威胁表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| threat_type | ENUM | 否 | | 威胁类型 |
| ip_address | VARCHAR(45) | 否 | | 攻击者IP |
| user_agent | VARCHAR(500) | 是 | NULL | 请求UA |
| details | JSON | 是 | NULL | 详情 |
| severity | ENUM | 否 | medium | 严重程度 |
| is_resolved | BOOLEAN | 否 | FALSE | 是否处理 |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |
| resolved_at | DATETIME | 是 | NULL | 处理时间 |

**威胁类型**:
- `rate_limit` - 触发限流
- `blocked_ip` - IP被封禁
- `blocked_ua` - UA被拦截
- `brute_force` - 暴力破解
- `suspicious_access` - 可疑访问

**严重程度**: low / medium / high / critical

---

### 19. ip_block - IP封禁表

| 字段 | 类型 | 空 | 默认 | 说明 |
|------|------|-----|------|------|
| id | INT | 否 | AI | 主键 |
| ip_address | VARCHAR(45) | 否 | | 被封禁IP（唯一） |
| block_type | ENUM | 否 | | 封禁类型 |
| reason | VARCHAR(200) | 是 | NULL | 封禁原因 |
| expires_at | DATETIME | 是 | NULL | 过期时间 |
| created_by_id | INT | 是 | NULL | 操作者ID |
| created_at | TIMESTAMP | 否 | CURRENT_TIMESTAMP | 创建时间 |

**封禁类型**:
- `manual` - 手动封禁
- `auto` - 自动封禁（触发安全机制）

**说明**: expires_at为null表示永久封禁

---

## 三、ER图关系

```
user (1,N) ──┬── cart (N,1) product
             ├── order (N,1) user
             │    └── order_item (N,1) order
             │              └── product
             ├── login_log (N,1) user
             ├── browse_log (N,1) user
             │             └── product
             ├── operation_log (N,1) user
             ├── user_profile (N,1) user
             └── comment (N,1) user

product (1,N) ──┬── cart (N,1) product
                ├── order_item (N,1) product
                ├── browse_log (N,1) product
                ├── product_rank (N,1) product
                ├── recommend_item (N,1) product
                └── comment (N,1) product

category (1,N) ──┐
                 └── product (N,1) category
```

---

## 四、初始化数据

### 默认角色

```sql
INSERT INTO role (name) VALUES ('customer'), ('sales'), ('admin');
```

### 系统管理员（密码: admin123）

```sql
INSERT INTO user (username, password_hash, email, role_id) 
VALUES ('admin', '$2b$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYAHo8fW3Wy', 'admin@shop.com', 3);
```

---

## 五、执行方式

```bash
mysql -u root -p < scripts/init_db.sql
```

或

```bash
python scripts/init_db.py
```