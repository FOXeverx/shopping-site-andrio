package com.example.shopping_site_andrio.data.model

data class AdminUserListItem(
    val id: Int,
    val username: String,
    val email: String?,
    val role: String?,
    val is_active: Boolean?,
    val created_at: String?
)

data class CreateAdminUserRequest(
    val username: String,
    val email: String?,
    val password: String,
    val role: String
)

data class UpdateAdminUserRequest(
    val email: String? = null,
    val password: String? = null,
    val role: String? = null
)

data class BrowseLogEntry(
    val id: Int,
    val user_id: Int,
    val username: String,
    val product_id: Int,
    val product_name: String?,
    val stay_time: Int?,
    val created_at: String?
)

data class LoginLogEntry(
    val id: Int,
    val user_id: Int,
    val ip_address: String?,
    val user_agent: String?,
    val success: Boolean?,
    val created_at: String?
)

data class PurchaseSummary(
    val category_id: Int?,
    val category_name: String?,
    val total_quantity: Int?,
    val total_amount: Double?
)

data class PurchaseDetail(
    val order_id: Int?,
    val order_number: String?,
    val created_at: String?,
    val product_id: Int?,
    val product_name: String?,
    val quantity: Int?,
    val price: Double?,
    val subtotal: Double?
)

data class OperationLogEntry(
    val id: Int,
    val user_id: Int,
    val username: String,
    val action: String?,
    val target_type: String?,
    val target_id: Int?,
    val created_at: String?
)

data class AnomalyEntry(
    val id: Int,
    val anomaly_type: String?,
    val description: String?,
    val severity: String?,
    val is_resolved: Boolean?,
    val created_at: String?
)

data class UserStats(
    val total_users: Int?,
    val spending_distribution: Map<String, Int>?,
    val avg_spent: Double?,
    val avg_orders: Double?,
    val region_distribution: Map<String, Int>?
)

data class AnomalyStats(
    val total: Int?,
    val unresolved: Int?,
    val by_severity: Map<String, Int>?,
    val last_24h: Int?
)

data class SalesDataPoint(
    val date: String?,
    val amount: Double?
)

data class SalesPredict(
    val trend: String?,
    val current_avg: Double?,
    val prediction: Double?,
    val confidence: Int?,
    val recent_data: List<SalesDataPoint>?
)

data class SecurityThreat(
    val id: Int,
    val threat_type: String?,
    val ip_address: String?,
    val user_agent: String?,
    val severity: String?,
    val is_resolved: Boolean?,
    val created_at: String?,
    val resolved_at: String?
)

data class ThreatStats(
    val total: Int?,
    val unresolved: Int?,
    val high_critical: Int?,
    val today: Int?
)

data class IpBlockEntry(
    val id: Int,
    val ip_address: String?,
    val block_type: String?,
    val reason: String?,
    val expires_at: String?,
    val created_by_id: Int?,
    val created_at: String?
)

data class BlockIpRequest(
    val ip_address: String,
    val reason: String?,
    val expires_minutes: Int?
)

data class RecommendTriggerResult(
    val cooccurrence_items: Int?
)

data class ImageUploadResult(
    val url: String?,
    val filename: String?
)

data class CreateProductRequest(
    val name: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val category_id: Int?,
    val image_url: String?
)

data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val stock: Int? = null,
    val category_id: Int? = null,
    val image_url: String? = null
)
