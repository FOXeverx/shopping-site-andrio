# 推荐系统文档

## 一、算法介绍

### 1.1 共现推荐（Co-occurrence）

**原理**：分析订单中同时出现的商品，计算商品之间的共现频率。

```
商品A和商品B的共现分数 = 同时购买A和B的订单数 / 购买A的总订单数
```

**用途**：
- 商品详情页「相关推荐」模块
- 个性化推荐（基于用户已购买商品查询推荐）

**数据来源**：仅使用 `CONFIRMED`（已确认）状态的订单

**优点**：
- 简单高效
- 可解释性强
- 适合商品数量适中的场景

**实现**：

```python
# scripts/recommend.py - CooccurrenceRecommender
def build_cooccurrence_matrix(self):
    orders = db.query(Order).filter(Order.status == OrderStatus.CONFIRMED).all()
    cooccurrence = defaultdict(lambda: defaultdict(int))
    for order in orders:
        product_ids = [item.product_id for item in order.items]
        for p1 in product_ids:
            for p2 in product_ids:
                if p1 != p2:
                    cooccurrence[p1][p2] += 1
    return cooccurrence
```

### 1.2 协同过滤（Collaborative Filtering）

**原理**：基于用户购买行为的相似度进行 User-Based 协同过滤。

```
用户A购买了商品X，用户U也购买了X
→ 找出与用户U购买行为相似的用户V（买了相同商品但未买X）
→ 推荐用户V购买过的其他商品
```

**用途**：
- 离线生成推荐数据，补充共现推荐的覆盖范围
- API 层「浏览过此商品的人也买了」模块（实时查询 BrowseLog + Order）

**数据来源**：
- Order CONFIRMED（已确认订单）— 离线批量计算
- BrowseLog（浏览记录）— API 实时查询

**优点**：
- 发现潜在兴趣，弥补共现推荐的稀疏性
- 不依赖商品内容

**实现**（离线批量, `scripts/recommend.py`）：

```python
# 构建用户-商品映射
user_products = defaultdict(set)     # user_id → {product_id, ...}
product_users = defaultdict(set)     # product_id → {user_id, ...}

# 针对目标商品X，找出邻居用户（与购买X的用户购买了相似商品的其他人）
target_users = product_users[X]
target_products = 所有 target_users 买过的商品集合

for each user V (未买X):
    similarity = len(V买过的商品 ∩ target_products)  # 购买重合度
    if similarity > 0:
        将V计入邻居用户，聚合V买过的商品作为推荐候选
```

---

## 二、数据流

```
用户下单 → 订单确认（CONFIRMED）
                ↓
推荐脚本读取 CONFIRMED 订单
                ↓
共现推荐：构建商品共现矩阵
协同过滤：计算用户购买行为相似度，聚合邻居用户推荐
                ↓
写入 recommend_item 表 (algorithm = cooccurrence / collaborative / cooccurrence+collaborative)
                ↓
API 查询推荐结果
```

---

## 三、API接口

### 3.1 获取商品相关推荐（共现推荐）

```bash
GET /api/recommend/product/{product_id}?limit=5
```

**说明**：基于共现推荐，查询该商品的相关推荐

**响应**：

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

### 3.2 获取浏览过此商品的用户也买了

```bash
GET /api/recommend/bought-also/{product_id}?limit=5
Authorization: Bearer <token>  # 可选，登录后可排除自己的购买记录
```

**说明**：
- 优先查询浏览过该商品的用户（排除当前登录用户），统计他们已确认订单中的其他商品
- 如无浏览记录或无购买，回退到基于已确认订单查询

**响应**：

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

### 3.3 获取用户个性化推荐

```bash
GET /api/recommend/user/me?limit=10
Authorization: Bearer <token>
```

**说明**：需要登录，优先级如下：
1. 已购买商品 → 推荐（排除已购买商品）
2. 浏览记录 → 推荐
3. 热门商品（兜底）

**响应**：

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

**reason 说明**：
| 值 | 说明 |
|----|------|
| `Based on your purchases` | 基于已购买商品查询的推荐 |
| `Based on your browsing history` | 基于浏览记录查询的推荐 |
| `热门商品` | 兜底推荐 |

---

## 四、推荐模块与算法对应

| 前端位置 | API | 算法 | 数据来源 |
|---------|-----|------|---------|
| 商品详情页 - 相关推荐 | `/recommend/product/{id}` | 共现推荐 + 协同过滤 | RecommendItem 表 |
| 商品详情页 - 浏览过此商品的人也买了 | `/recommend/bought-also/{id}` | 协同过滤（实时） | BrowseLog + Order CONFIRMED |
| 个性化推荐页 | `/recommend/user/me` | 共现推荐 + 协同过滤 + 历史过滤 | RecommendItem + 购买/浏览历史 |

---

## 五、运行方式

### 5.1 手动触发（Admin）

```bash
POST /api/admin/recommend/trigger
Authorization: Bearer <admin_token>
```

**响应**：

```json
{
  "success": true,
  "message": "Recommendation completed: 150 items created",
  "data": {
    "cooccurrence_items": 150
  }
}
```

> 注：`collaborative.run()` 也会产出推荐记录（算法类型为 `collaborative` 或 `cooccurrence+collaborative`），与共现推荐结果一同写入 `recommend_item` 表。

### 5.2 定时任务（cron）

```bash
# 每天凌晨2点执行
0 2 * * * cd /app && python scripts/recommend.py
```

### Linux crontab

```bash
crontab -e
# 添加以下行
0 2 * * * /usr/bin/python3 /app/scripts/recommend.py >> /var/log/recommend.log 2>&1
```

---

## 六、定时触发配置

### Windows任务计划程序

```
操作：启动程序
程序：python
参数：D:\VS Code\python code\shopping_site_pro\scripts\recommend.py
```

---

## 七、数据库表

### recommend_item表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| product_id | INT | 源商品ID |
| recommend_product_id | INT | 推荐商品ID |
| score | DECIMAL(5,4) | 推荐分数 |
| algorithm | VARCHAR(50) | 算法类型 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

**索引**：
- `uk_product_recommend` (product_id, recommend_product_id) UNIQUE
- `idx_product_id` (product_id)
- `idx_score` (score)

---

## 八、算法参数调优

### 共现矩阵阈值

| 参数 | 默认值 | 说明 |
|------|--------|------|
| TOP_K | 10 | 每个商品最多推荐数 |
| MIN_SCORE | 0.01 | 最低推荐分数 |

### 调整建议

```python
# 增加推荐多样性
TOP_K = 15

# 提高推荐精度
MIN_SCORE = 0.1
```

---

## 九、常见问题

### Q1: 推荐结果为空

**原因**：订单数据不足

**解决方案**：
- 增加商品品类
- 等待更多订单

### Q2: 推荐不准确

**原因**：共现矩阵稀疏

**解决方案**：
- 结合协同过滤
- 调整阈值参数

### Q3: 更新频率

**建议**：每日更新一次，高峰期可每小时更新

### Q4: 未登录用户看不到推荐

**正常现象**：商品详情页的「相关推荐」和「浏览过此商品的人也买了」模块仅对登录用户展示。

---

## 十、订单状态与推荐关系

只有 `CONFIRMED`（已确认）的订单才会被用于推荐计算：
- ❌ CREATED（已创建未确认）
- ✅ CONFIRMED（已确认）
- ❌ CANCELLED（已取消）

---

## 十一、扩展方向

### 11.1 实时推荐

```python
# 用户浏览时实时推荐
@app.post("/browse")
async def log_browse(req):
    recs = get_realtime_recommend(req.product_id)
    return recs
```

### 11.2 个性化排序

```python
# 使用结合用户画像排序
@router.get("/recommend/user/personalized")
async def get_personalized_recommend(user_profile):
    base_recs = get_base_recommendations()
    return sorted_by_preference(base_recs, user_profile)
```

### 11.3 深度学习推荐

```python
# 使用神经协同过滤
import torch
import torch.nn as nn

class NCF(nn.Module):
    def __init__(self, n_users, n_items, embed_dim=64):
        self.user_embed = nn.Embedding(n_users, embed_dim)
        self.item_embed = nn.Embedding(n_items, embed_dim)

    def forward(self, user_ids, item_ids):
        user_emb = self.user_embed(user_ids)
        item_emb = self.item_embed(item_ids)
        return torch.sigmoid((user_emb * item_emb).sum(dim=1))
```
```