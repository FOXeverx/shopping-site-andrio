# 数据分析系统文档

## 一、系统概述

本系统提供用户画像分析、销售趋势分析、异常检测等功能，支持销售后台和管理后台使用。

---

## 二、用户画像分析

### 2.1 数据采集

| 数据类型 | 采集内容 | 数据来源 |
|----------|----------|----------|
| 地域分布 | IP地址推断 | login_log |
| 购买力 | 消费金额、订单数 | order |
| 偏好分类 | 浏览/购买商品类别 | browse_log, order_item |

### 2.2 用户分群

**购买力分级**：

| 级别 | 条件 | 说明 |
|------|------|------|
| 高消费 | 平均订单金额 > 1000元 | 高价值用户 |
| 中等消费 | 平均订单金额 500-1000元 | 潜力用户 |
| 低消费 | 平均订单金额 < 500元 | 普通用户 |

### 2.3 API接口

```bash
GET /api/admin/user-stats
Authorization: Bearer <token>
```

**响应**：

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

## 三、销售趋势分析

### 3.1 趋势预测

基于历史7天数据计算趋势：

| 趋势 | 判断条件 |
|------|----------|
| 上升 | 当前均值 > 首日均值 * 1.2 |
| 下降 | 当前均值 < 首日均值 * 0.8 |
| 稳定 | 其他情况 |

### 3.2 预测算法

```python
# scripts/analysis.py - SalesAnalyzer
def predict_trend(self, days=7):
    stats = db.query(SalesStat).order_by(
        SalesStat.stat_date.desc()
    ).limit(30).all()
    
    amounts = [float(s.total_amount) for s in stats[:7]]
    avg = sum(amounts) / len(amounts)
    
    return {
        "trend": "increasing/stable/decreasing",
        "current_avg": avg,
        "prediction": avg * adjustment_factor
    }
```

### 3.3 API接口

```bash
GET /api/admin/sales-predict?days=7
Authorization: Bearer <token>
```

**响应**：

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
      {"date": "2026-04-06", "amount": 4800.00},
      ...
    ]
  }
}
```

---

## 四、异常检测系统

### 4.1 检测类型

| 异常类型 | 检测条件 | 严重程度 |
|----------|----------|----------|
| 大额订单 | 订单金额 > 10000元 | 中 |
| 超大额订单 | 订单金额 > 50000元 | 高 |
| 登录失败 | 1小时内失败 >= 5次 | 中 |
| 严重登录失败 | 1小时内失败 >= 10次 | 高 |
| 库存不足 | 库存 < 5件 | 低 |

### 4.2 检测逻辑

```python
# scripts/analysis.py - AnomalyDetector
def detect_anomalies(self):
    anomalies = []
    
    # 大额订单检测
    large_orders = db.query(Order).filter(
        Order.status == OrderStatus.CONFIRMED,
        Order.total_amount > 10000
    ).all()
    
    # 登录失败检测
    failed = db.query(LoginLog).filter(
        LoginLog.success == False,
        LoginLog.created_at > one_hour_ago
    ).group_by(LoginLog.username).having(
        func.count(LoginLog.id) >= 5
    ).all()
    
    # 库存检测
    low_stock = db.query(Product).filter(
        Product.stock < 5
    ).all()
```

### 4.3 API接口

**获取异常统计**：

```bash
GET /api/admin/anomaly-stats
Authorization: Bearer <token>
```

**响应**：

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

**处理异常**：

```bash
POST /api/admin/anomaly/{id}/resolve
Authorization: Bearer <admin_token>
```

---

## 五、数据分析脚本

### 5.1 运行方式

```bash
cd D:\VS Code\python code\shopping_site_pro
python scripts/analysis.py
```

### 5.2 脚本功能

1. **用户画像分析** - 更新用户消费数据、偏好分类
2. **销售统计汇总** - 汇总30天销售数据
3. **商品排行计算** - 计算日/周/月商品排行
4. **异常检测** - 检测异常订单、登录失败、库存问题

### 5.3 输出示例

```
[Analysis] Starting analysis at 2026-04-11 21:19:02.879786
[Analysis] Analyzing user profiles...
[Analysis] Analyzed 1 user profiles
[Analysis] Analyzing sales for last 30 days...
[Analysis] Analyzed 30 days of sales
[Analysis] Ranked 2 products (daily)
[Analysis] Ranked 2 products (weekly)
[Analysis] Ranked 2 products (monthly)
[AnomalyDetector] Detected 3 anomalies
[Analysis] Completed at 2026-04-11 21:19:03.123456
```

### 5.4 定时执行

**Windows任务计划**：
```
任务: 每日凌晨2点运行分析脚本
操作: 启动程序
程序: python
参数: D:\VS Code\python code\shopping_site_pro\scripts\analysis.py
```

---

## 六、前端页面

### 6.1 数据分析页面

**访问路径**：
- 管理员：`/admin/analysis`
- 销售：`/sales/analysis`

**展示内容**：

1. **统计卡片** - 用户总数、平均消费、异常总数、待处理异常
2. **用户消费分布饼图** - 低/中/高消费用户比例
3. **用户地域分布柱状图** - 按地域统计用户数量
4. **销售趋势折线图** - 最近7天销售金额
5. **异常告警监控** - 按严重程度分类显示异常数量

---

## 七、数据库表

### user_profile 表（已更新）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| user_id | INT | 用户ID（唯一） |
| region | VARCHAR(50) | 地域 |
| total_spent | DECIMAL(12,2) | 总消费金额 |
| order_count | INT | 订单数 |
| avg_order_amount | DECIMAL(12,2) | 平均订单金额 |
| preferred_categories | JSON | 偏好分类 |
| browse_category_stats | JSON | 浏览统计 |
| spending_level | ENUM | 消费水平 |
| last_updated | DATETIME | 最后更新时间 |

### sales_stat 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| stat_date | DATE | 统计日期 |
| total_amount | DECIMAL(12,2) | 销售总额 |
| order_count | INT | 订单数 |
| user_count | INT | 购买用户数 |

### anomaly_log 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT | 主键 |
| anomaly_type | VARCHAR(100) | 异常类型 |
| description | TEXT | 异常描述 |
| severity | ENUM | 严重程度 |
| details | JSON | 详情 |
| is_resolved | BOOLEAN | 是否解决 |
| created_at | TIMESTAMP | 创建时间 |
| resolved_at | DATETIME | 解决时间 |

---

## 八、效果评估

### 8.1 画像准确度

- 购买力分级准确率
- 地域识别准确率

### 8.2 预测准确度

- 趋势判断准确率
- 销售额预测偏差

### 8.3 异常检测

- 检出率
- 误报率

---

## 九、扩展方向

### 9.1 实时画像更新

用户产生订单或浏览行为时实时更新画像

### 9.2 智能预警

基于机器学习预测异常，提前预警

### 9.3 多维度分析

- 时间维度分析（工作日/周末、节假日）
- 商品维度分析（品类、品牌）
- 用户留存分析