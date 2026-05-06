# 安全与反爬虫机制

## 一、概述

本文档说明系统的安全防护措施和反爬虫机制。

---

## 二、当前安全措施

### 1. 中间件架构

系统通过 FastAPI 中间件实现安全防护，在 `app/middleware/security.py` 中定义：

```python
app.add_middleware(SecurityHeadersMiddleware)
app.add_middleware(UserAgentMiddleware)
app.add_middleware(RateLimitMiddleware)
```

中间件执行顺序（从外到内）：
1. RateLimitMiddleware - IP限流
2. UserAgentMiddleware - User-Agent检测
3. SecurityHeadersMiddleware - 安全响应头

---

### 2. IP限流（RateLimitMiddleware）

| 项目 | 配置值 |
|------|--------|
| 限流周期 | 1分钟 |
| 默认限制 | 60次/分钟（可配置） |
| 封禁时长 | 30分钟 |
| 触发阈值 | 超过限制立即封禁 |

**工作流程**：
```
请求 → 检查IP是否已封禁 → 检查请求频率 → 记录请求 → 放行/封禁
```

**封禁逻辑**：
- 超过限制后，该IP被封禁30分钟
- 封禁期间访问返回 `429 Too Many Requests`
- 封禁时间到期后自动解封

**配置方式**（config.yaml）：
```yaml
security:
  ip_limit_per_minute: 60
```

---

### 3. User-Agent检测（UserAgentMiddleware）

检测常见爬虫工具的User-Agent，发现后返回 `403 Forbidden`。

**已拦截的User-Agent**：
| 关键词 | 典型爬虫/工具 |
|--------|--------------|
| python-requests | Python requests库 |
| curl | curl命令行工具 |
| wget | wget命令行工具 |
| scrapy | Scrapy爬虫框架 |

**响应**：
```json
{
  "detail": "Access denied"
}
```

---

### 4. 安全响应头（SecurityHeadersMiddleware）

为所有响应添加安全相关的HTTP头：

| 响应头 | 值 | 作用 |
|--------|-----|------|
| X-Content-Type-Options | nosniff | 禁止MIME类型 sniffing |
| X-Frame-Options | DENY | 防止点击劫持 |
| X-XSS-Protection | 1; mode=block | XSS防护 |
| Strict-Transport-Security | max-age=31536000 | 强制HTTPS |

---

## 三、CORS跨域配置

在 `app/main.py` 中配置：

```python
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],          # 生产环境应限制具体域名
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
```

**生产环境建议**：
- 明确指定允许的域名
- 禁用 `allow_credentials` 或限制Origins

---

## 四、认证与授权

### 1. JWT认证

- 使用 `python-jose` 生成和验证JWT Token
- Token有效期可配置
- 支持黑名单机制（登出时加入黑名单）

### 2. 角色权限

| 角色 | 权限说明 |
|------|----------|
| customer | 普通用户，可购买、评论 |
| sales | 销售人员，可管理商品、查看用户行为 |
| admin | 管理员，全部权限 |

权限验证示例：
```python
from app.dependencies import require_role

@router.get("/admin/users")
async def get_users(
    current_user: User = Depends(require_role("admin")),
    ...
)
```

---

## 五、数据安全

### 1. 密码加密

使用bcrypt加密：
```python
import bcrypt

def hash_password(password: str) -> str:
    return bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode()
```

### 2. 敏感信息

- 密码Hash存储，不存储明文
- 数据库连接信息从配置文件读取
- API密钥等敏感信息使用环境变量

---

## 六、日志与监控

### 1. 登录日志

记录每次登录尝试：
- 用户名、IP地址
- User-Agent
- 成功/失败状态
- 时间

### 2. 浏览日志

记录用户浏览行为：
- 用户ID、商品ID
- 停留时间
- 浏览时间

### 3. 操作日志

记录敏感操作：
- 操作类型、目标类型、目标ID
- 操作者、时间

---

## 七、潜在风险与改进建议

### 当前不足

| 风险点 | 风险等级 | 说明 |
|--------|----------|------|
| User-Agent检测简单 | 中 | 可通过伪造UA绕过 |
| 无验证码机制 | 中 | 暴力破解风险 |
| 无行为分析 | 中 | 爬虫可模拟正常访问模式 |
| Referer检查缺失 | 低 | 无法验证请求来源 |

### 改进建议

#### 1. 增强User-Agent检测
- 增加更多爬虫特征
- 结合User-Agent和IP地址分析

#### 2. 引入验证码
- 图形验证码
- 滑块验证（如腾讯防水墙）
- 行为验证（如Google reCAPTCHA）

#### 3. 访问频率+行为分析
- 综合评分系统
- 异常访问模式检测
- 机器学习行为分析

#### 4. 添加蜜罐/陷阱
- 隐藏链接，仅爬虫会访问
- 发现访问即封禁

#### 5. 第三方防护
- Cloudflare CDN防护
- 阿里云WAF
- 自建Nginx防护层

---

## 八、安全配置示例

### 生产环境 config.yaml

```yaml
app:
  host: "0.0.0.0"
  port: 8000
  debug: false

security:
  ip_limit_per_minute: 30        # 生产环境更严格
  jwt_secret: "${JWT_SECRET}"     # 从环境变量读取
  jwt_expire_minutes: 60

database:
  host: "${DB_HOST}"
  port: 3306
  username: "${DB_USER}"
  password: "${DB_PASSWORD}"
  name: "shopping_site"
```

### 环境变量

```bash
export JWT_SECRET="your-secret-key"
export DB_HOST="localhost"
export DB_USER="root"
export DB_PASSWORD="your-password"
```

---

## 九、测试验证

### 测试IP封禁

```bash
# 快速发送超过限制的请求
for i in {1..70}; do curl -I http://localhost:8000/api/product; done
# 预期：返回429错误

# 封禁后访问
curl http://localhost:8000/api/product
# 预期：返回403 "IP temporarily blocked"
```

### 测试User-Agent拦截

```bash
# 使用curl访问
curl -A "scrapy" http://localhost:8000/api/product
# 预期：返回403 "Access denied"

curl -A "python-requests" http://localhost:8000/api/product
# 预期：返回403 "Access denied"
```

---

## 十、相关文件

| 文件路径 | 说明 |
|---------|------|
| app/middleware/security.py | 安全中间件实现 |
| app/main.py | 中间件注册 |
| app/config.py | 配置管理 |
| app/dependencies.py | 权限验证依赖 |
| config.yaml | 配置文件 |