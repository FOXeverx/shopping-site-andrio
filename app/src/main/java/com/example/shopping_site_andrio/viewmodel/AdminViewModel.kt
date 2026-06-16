package com.example.shopping_site_andrio.ui.screen.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shopping_site_andrio.data.api.ApiResult
import com.example.shopping_site_andrio.data.model.*
import com.example.shopping_site_andrio.data.repository.AdminRepository
import com.example.shopping_site_andrio.data.repository.ProductRepository
import com.example.shopping_site_andrio.domain.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ========== Dashboard State ==========
data class AdminDashboardUiState(
    val userStats: UiState<UserStats> = UiState(),
    val anomalyStats: UiState<AnomalyStats> = UiState(),
    val threatStats: UiState<ThreatStats> = UiState(),
    val salesPredict: UiState<SalesPredict> = UiState()
)

// ========== Users State ==========
data class AdminUsersUiState(
    val users: UiState<List<AdminUserListItem>> = UiState.loading(),
    val roleFilter: String? = null,
    val message: String? = null,
    val showCreateDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingUser: AdminUserListItem? = null,
    val newUsername: String = "",
    val newEmail: String = "",
    val newPassword: String = "",
    val newRole: String = "customer",
    val editEmail: String = "",
    val editPassword: String = "",
    val editRole: String = "customer"
)

// ========== User Detail State ==========
data class AdminUserDetailUiState(
    val browseLogs: UiState<List<BrowseLogEntry>> = UiState.loading(),
    val loginLogs: UiState<List<LoginLogEntry>> = UiState(),
    val purchaseSummary: UiState<List<PurchaseSummary>> = UiState()
)

// ========== Logs State ==========
data class AdminLogsUiState(
    val operationLogs: UiState<List<OperationLogEntry>> = UiState.loading(),
    val browseLogs: UiState<List<BrowseLogEntry>> = UiState(),
    val selectedTab: Int = 0,
    val message: String? = null,
    val filterProductId: String = "",
    val filterUserId: String = ""
)

// ========== Anomalies State ==========
data class AdminAnomaliesUiState(
    val anomalies: UiState<List<AnomalyEntry>> = UiState.loading(),
    val message: String? = null
)

// ========== Security State ==========
data class AdminSecurityUiState(
    val threats: UiState<List<SecurityThreat>> = UiState.loading(),
    val ipBlocks: UiState<List<IpBlockEntry>> = UiState.loading(),
    val selectedTab: Int = 0,
    val message: String? = null,
    val showBlockDialog: Boolean = false,
    val blockIp: String = "",
    val blockReason: String = "",
    val blockExpiresMinutes: String = ""
)

data class AdminProductsUiState(
    val products: Flow<PagingData<ProductDto>>? = null,
    val message: String? = null,
    val showCreateDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editingProduct: ProductDto? = null,
    val newName: String = "",
    val newDescription: String = "",
    val newPrice: String = "",
    val newStock: String = "",
    val newCategoryId: String = "",
    val newImageUrl: String = "",
    val editName: String = "",
    val editDescription: String = "",
    val editPrice: String = "",
    val editStock: String = "",
    val editCategoryId: String = "",
    val editImageUrl: String = ""
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _dashboardState = MutableStateFlow(AdminDashboardUiState())
    val dashboardState: StateFlow<AdminDashboardUiState> = _dashboardState.asStateFlow()

    private val _usersState = MutableStateFlow(AdminUsersUiState())
    val usersState: StateFlow<AdminUsersUiState> = _usersState.asStateFlow()

    private val _userDetailState = MutableStateFlow(AdminUserDetailUiState())
    val userDetailState: StateFlow<AdminUserDetailUiState> = _userDetailState.asStateFlow()

    private val _logsState = MutableStateFlow(AdminLogsUiState())
    val logsState: StateFlow<AdminLogsUiState> = _logsState.asStateFlow()

    private val _anomaliesState = MutableStateFlow(AdminAnomaliesUiState())
    val anomaliesState: StateFlow<AdminAnomaliesUiState> = _anomaliesState.asStateFlow()

    private val _securityState = MutableStateFlow(AdminSecurityUiState())
    val securityState: StateFlow<AdminSecurityUiState> = _securityState.asStateFlow()

    private val _productsState = MutableStateFlow(AdminProductsUiState())
    val productsState: StateFlow<AdminProductsUiState> = _productsState.asStateFlow()

    // ===== Dashboard =====
    fun loadDashboard() {
        viewModelScope.launch {
            _dashboardState.value = _dashboardState.value.copy(userStats = UiState.loading())
            when (val r = adminRepository.getUserStats()) {
                is ApiResult.Success -> _dashboardState.value = _dashboardState.value.copy(userStats = UiState.success(r.data))
                is ApiResult.Error -> _dashboardState.value = _dashboardState.value.copy(userStats = UiState.error(r.message))
            }
        }
        viewModelScope.launch {
            _dashboardState.value = _dashboardState.value.copy(anomalyStats = UiState.loading())
            when (val r = adminRepository.getAnomalyStats()) {
                is ApiResult.Success -> _dashboardState.value = _dashboardState.value.copy(anomalyStats = UiState.success(r.data))
                is ApiResult.Error -> _dashboardState.value = _dashboardState.value.copy(anomalyStats = UiState.error(r.message))
            }
        }
        viewModelScope.launch {
            when (val r = adminRepository.getSecurityThreatStats()) {
                is ApiResult.Success -> _dashboardState.value = _dashboardState.value.copy(threatStats = UiState.success(r.data))
                is ApiResult.Error -> _dashboardState.value = _dashboardState.value.copy(threatStats = UiState.error(r.message))
            }
        }
        viewModelScope.launch {
            when (val r = adminRepository.getSalesPredict()) {
                is ApiResult.Success -> _dashboardState.value = _dashboardState.value.copy(salesPredict = UiState.success(r.data))
                is ApiResult.Error -> _dashboardState.value = _dashboardState.value.copy(salesPredict = UiState.error(r.message))
            }
        }
    }

    // ===== Users =====
    fun loadUsers() {
        viewModelScope.launch {
            val role = _usersState.value.roleFilter
            when (val r = adminRepository.getAdminUsers(role = role)) {
                is ApiResult.Success -> _usersState.value = _usersState.value.copy(users = UiState.success(r.data))
                is ApiResult.Error -> _usersState.value = _usersState.value.copy(users = UiState.error(r.message))
            }
        }
    }

    fun setRoleFilter(role: String?) {
        _usersState.value = _usersState.value.copy(roleFilter = role)
        loadUsers()
    }

    fun showCreateDialog() { _usersState.value = _usersState.value.copy(showCreateDialog = true) }
    fun hideCreateDialog() { _usersState.value = _usersState.value.copy(showCreateDialog = false, newUsername = "", newEmail = "", newPassword = "", newRole = "customer") }
    fun updateNewUsername(v: String) { _usersState.value = _usersState.value.copy(newUsername = v) }
    fun updateNewEmail(v: String) { _usersState.value = _usersState.value.copy(newEmail = v) }
    fun updateNewPassword(v: String) { _usersState.value = _usersState.value.copy(newPassword = v) }
    fun updateNewRole(v: String) { _usersState.value = _usersState.value.copy(newRole = v) }

    fun createUser() {
        val s = _usersState.value
        val req = CreateAdminUserRequest(s.newUsername, s.newEmail.ifBlank { null }, s.newPassword, s.newRole)
        viewModelScope.launch {
            when (val r = adminRepository.createUser(req)) {
                is ApiResult.Success -> { hideCreateDialog(); loadUsers(); _usersState.value = _usersState.value.copy(message = "User created") }
                is ApiResult.Error -> _usersState.value = _usersState.value.copy(message = r.message)
            }
        }
    }

    fun showEditDialog(user: AdminUserListItem) {
        _usersState.value = _usersState.value.copy(showEditDialog = true, editingUser = user, editEmail = user.email ?: "", editPassword = "", editRole = user.role ?: "customer")
    }
    fun hideEditDialog() { _usersState.value = _usersState.value.copy(showEditDialog = false, editingUser = null, editEmail = "", editPassword = "", editRole = "customer") }
    fun updateEditEmail(v: String) { _usersState.value = _usersState.value.copy(editEmail = v) }
    fun updateEditPassword(v: String) { _usersState.value = _usersState.value.copy(editPassword = v) }
    fun updateEditRole(v: String) { _usersState.value = _usersState.value.copy(editRole = v) }

    fun updateUser() {
        val s = _usersState.value
        val uid = s.editingUser?.id ?: return
        val req = UpdateAdminUserRequest(email = s.editEmail.ifBlank { null }, password = s.editPassword.ifBlank { null }, role = s.editRole)
        viewModelScope.launch {
            when (val r = adminRepository.updateUser(uid, req)) {
                is ApiResult.Success -> { hideEditDialog(); loadUsers(); _usersState.value = _usersState.value.copy(message = "User updated") }
                is ApiResult.Error -> _usersState.value = _usersState.value.copy(message = r.message)
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            when (val r = adminRepository.deleteUser(userId)) {
                is ApiResult.Success -> { loadUsers(); _usersState.value = _usersState.value.copy(message = "User deleted") }
                is ApiResult.Error -> _usersState.value = _usersState.value.copy(message = r.message)
            }
        }
    }

    fun clearUsersMessage() { _usersState.value = _usersState.value.copy(message = null) }

    // ===== User Detail =====
    fun loadUserDetail(userId: Int) {
        viewModelScope.launch {
            _userDetailState.value = _userDetailState.value.copy(browseLogs = UiState.loading())
            when (val r = adminRepository.getUserBrowse(userId)) {
                is ApiResult.Success -> _userDetailState.value = _userDetailState.value.copy(browseLogs = UiState.success(r.data))
                is ApiResult.Error -> _userDetailState.value = _userDetailState.value.copy(browseLogs = UiState.error(r.message))
            }
        }
        viewModelScope.launch {
            when (val r = adminRepository.getUserLogins(userId)) {
                is ApiResult.Success -> _userDetailState.value = _userDetailState.value.copy(loginLogs = UiState.success(r.data))
                is ApiResult.Error -> _userDetailState.value = _userDetailState.value.copy(loginLogs = UiState.error(r.message))
            }
        }
        viewModelScope.launch {
            when (val r = adminRepository.getUserPurchasesSummary(userId)) {
                is ApiResult.Success -> _userDetailState.value = _userDetailState.value.copy(purchaseSummary = UiState.success(r.data))
                is ApiResult.Error -> _userDetailState.value = _userDetailState.value.copy(purchaseSummary = UiState.error(r.message))
            }
        }
    }

    // ===== Logs =====
    fun loadLogs() {
        viewModelScope.launch {
            when (val r = adminRepository.getOperationLogs()) {
                is ApiResult.Success -> _logsState.value = _logsState.value.copy(operationLogs = UiState.success(r.data))
                is ApiResult.Error -> _logsState.value = _logsState.value.copy(operationLogs = UiState.error(r.message))
            }
        }
    }

    fun loadBrowseLogs() {
        val s = _logsState.value
        viewModelScope.launch {
            when (val r = adminRepository.getBrowseLogs(productId = s.filterProductId.toIntOrNull(), userId = s.filterUserId.toIntOrNull())) {
                is ApiResult.Success -> _logsState.value = _logsState.value.copy(browseLogs = UiState.success(r.data))
                is ApiResult.Error -> _logsState.value = _logsState.value.copy(browseLogs = UiState.error(r.message))
            }
        }
    }

    fun setLogsTab(tab: Int) { _logsState.value = _logsState.value.copy(selectedTab = tab) }
    fun updateLogFilterProductId(v: String) { _logsState.value = _logsState.value.copy(filterProductId = v) }
    fun updateLogFilterUserId(v: String) { _logsState.value = _logsState.value.copy(filterUserId = v) }
    fun clearLogsMessage() { _logsState.value = _logsState.value.copy(message = null) }

    fun triggerRecommend() {
        viewModelScope.launch {
            when (adminRepository.triggerRecommendation()) {
                is ApiResult.Success -> _logsState.value = _logsState.value.copy(message = "Recommendation triggered")
                is ApiResult.Error -> _logsState.value = _logsState.value.copy(message = "Failed")
            }
        }
    }

    // ===== Anomalies =====
    fun loadAnomalies() {
        viewModelScope.launch {
            when (val r = adminRepository.getAnomalies()) {
                is ApiResult.Success -> _anomaliesState.value = _anomaliesState.value.copy(anomalies = UiState.success(r.data))
                is ApiResult.Error -> _anomaliesState.value = _anomaliesState.value.copy(anomalies = UiState.error(r.message))
            }
        }
    }

    fun resolveAnomaly(id: Int) {
        viewModelScope.launch {
            when (adminRepository.resolveAnomaly(id)) {
                is ApiResult.Success -> { loadAnomalies(); _anomaliesState.value = _anomaliesState.value.copy(message = "Resolved") }
                is ApiResult.Error -> _anomaliesState.value = _anomaliesState.value.copy(message = "Failed")
            }
        }
    }

    fun clearAnomaliesMessage() { _anomaliesState.value = _anomaliesState.value.copy(message = null) }

    // ===== Security =====
    fun loadThreats() {
        viewModelScope.launch {
            when (val r = adminRepository.getSecurityThreats()) {
                is ApiResult.Success -> _securityState.value = _securityState.value.copy(threats = UiState.success(r.data))
                is ApiResult.Error -> _securityState.value = _securityState.value.copy(threats = UiState.error(r.message))
            }
        }
    }

    fun resolveThreat(id: Int) {
        viewModelScope.launch {
            when (adminRepository.resolveSecurityThreat(id)) {
                is ApiResult.Success -> { loadThreats(); _securityState.value = _securityState.value.copy(message = "Resolved") }
                is ApiResult.Error -> _securityState.value = _securityState.value.copy(message = "Failed")
            }
        }
    }

    fun loadIpBlocks() {
        viewModelScope.launch {
            when (val r = adminRepository.getIpBlocks()) {
                is ApiResult.Success -> _securityState.value = _securityState.value.copy(ipBlocks = UiState.success(r.data))
                is ApiResult.Error -> _securityState.value = _securityState.value.copy(ipBlocks = UiState.error(r.message))
            }
        }
    }

    fun setSecurityTab(tab: Int) { _securityState.value = _securityState.value.copy(selectedTab = tab) }

    fun showBlockDialog() { _securityState.value = _securityState.value.copy(showBlockDialog = true) }
    fun hideBlockDialog() { _securityState.value = _securityState.value.copy(showBlockDialog = false, blockIp = "", blockReason = "", blockExpiresMinutes = "") }
    fun updateBlockIp(v: String) { _securityState.value = _securityState.value.copy(blockIp = v) }
    fun updateBlockReason(v: String) { _securityState.value = _securityState.value.copy(blockReason = v) }
    fun updateBlockExpiresMinutes(v: String) { _securityState.value = _securityState.value.copy(blockExpiresMinutes = v) }

    fun blockIp() {
        val s = _securityState.value
        val req = BlockIpRequest(s.blockIp, s.blockReason.ifBlank { null }, s.blockExpiresMinutes.toIntOrNull())
        viewModelScope.launch {
            when (adminRepository.blockIp(req)) {
                is ApiResult.Success -> { hideBlockDialog(); loadIpBlocks(); _securityState.value = _securityState.value.copy(message = "IP blocked") }
                is ApiResult.Error -> _securityState.value = _securityState.value.copy(message = "Failed")
            }
        }
    }

    fun unblockIp(blockId: Int) {
        viewModelScope.launch {
            when (adminRepository.unblockIp(blockId)) {
                is ApiResult.Success -> { loadIpBlocks(); _securityState.value = _securityState.value.copy(message = "IP unblocked") }
                is ApiResult.Error -> _securityState.value = _securityState.value.copy(message = "Failed")
            }
        }
    }

    fun clearSecurityMessage() { _securityState.value = _securityState.value.copy(message = null) }

    // ===== Products =====
    fun loadProducts() {
        _productsState.value = _productsState.value.copy(
            products = productRepository.getProducts().cachedIn(viewModelScope)
        )
    }

    fun showCreateProductDialog() { _productsState.value = _productsState.value.copy(showCreateDialog = true) }
    fun hideCreateProductDialog() { _productsState.value = _productsState.value.copy(showCreateDialog = false, newName = "", newDescription = "", newPrice = "", newStock = "", newCategoryId = "", newImageUrl = "") }
    fun updateNewProductName(v: String) { _productsState.value = _productsState.value.copy(newName = v) }
    fun updateNewProductDesc(v: String) { _productsState.value = _productsState.value.copy(newDescription = v) }
    fun updateNewProductPrice(v: String) { _productsState.value = _productsState.value.copy(newPrice = v) }
    fun updateNewProductStock(v: String) { _productsState.value = _productsState.value.copy(newStock = v) }
    fun updateNewProductCategory(v: String) { _productsState.value = _productsState.value.copy(newCategoryId = v) }
    fun updateNewProductImage(v: String) { _productsState.value = _productsState.value.copy(newImageUrl = v) }

    fun createProduct() {
        val s = _productsState.value
        val req = CreateProductRequest(s.newName, s.newDescription.ifBlank { null }, s.newPrice.toDoubleOrNull() ?: 0.0, s.newStock.toIntOrNull() ?: 0, s.newCategoryId.toIntOrNull(), s.newImageUrl.ifBlank { null })
        viewModelScope.launch {
            when (val r = adminRepository.createProduct(req)) {
                is ApiResult.Success -> { hideCreateProductDialog(); loadProducts(); _productsState.value = _productsState.value.copy(message = "Product created") }
                is ApiResult.Error -> _productsState.value = _productsState.value.copy(message = r.message)
            }
        }
    }

    fun showEditProductDialog(product: ProductDto) {
        _productsState.value = _productsState.value.copy(showEditDialog = true, editingProduct = product, editName = product.name, editDescription = product.description ?: "", editPrice = product.price.toString(), editStock = product.stock.toString(), editCategoryId = product.category_id?.toString() ?: "", editImageUrl = product.image_url ?: "")
    }
    fun hideEditProductDialog() { _productsState.value = _productsState.value.copy(showEditDialog = false, editingProduct = null) }
    fun updateEditProductName(v: String) { _productsState.value = _productsState.value.copy(editName = v) }
    fun updateEditProductDesc(v: String) { _productsState.value = _productsState.value.copy(editDescription = v) }
    fun updateEditProductPrice(v: String) { _productsState.value = _productsState.value.copy(editPrice = v) }
    fun updateEditProductStock(v: String) { _productsState.value = _productsState.value.copy(editStock = v) }
    fun updateEditProductCategory(v: String) { _productsState.value = _productsState.value.copy(editCategoryId = v) }
    fun updateEditProductImage(v: String) { _productsState.value = _productsState.value.copy(editImageUrl = v) }

    fun updateProduct() {
        val s = _productsState.value
        val pid = s.editingProduct?.id ?: return
        val req = UpdateProductRequest(name = s.editName.ifBlank { null }, description = s.editDescription.ifBlank { null }, price = s.editPrice.toDoubleOrNull(), stock = s.editStock.toIntOrNull(), category_id = s.editCategoryId.toIntOrNull(), image_url = s.editImageUrl.ifBlank { null })
        viewModelScope.launch {
            when (val r = adminRepository.updateProduct(pid, req)) {
                is ApiResult.Success -> { hideEditProductDialog(); loadProducts(); _productsState.value = _productsState.value.copy(message = "Product updated") }
                is ApiResult.Error -> _productsState.value = _productsState.value.copy(message = r.message)
            }
        }
    }

    fun deleteProduct(productId: Int) {
        viewModelScope.launch {
            when (val r = adminRepository.deleteProduct(productId)) {
                is ApiResult.Success -> { loadProducts(); _productsState.value = _productsState.value.copy(message = "Product deleted") }
                is ApiResult.Error -> _productsState.value = _productsState.value.copy(message = r.message)
            }
        }
    }

    fun clearProductsMessage() { _productsState.value = _productsState.value.copy(message = null) }
}
