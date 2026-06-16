package com.example.shopping_site_andrio.ui.screen.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shopping_site_andrio.data.model.AdminUserListItem
import com.example.shopping_site_andrio.ui.component.ErrorView
import com.example.shopping_site_andrio.ui.component.SkeletonLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    onBack: () -> Unit,
    onUserClick: (Int) -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.usersState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadUsers() }
    LaunchedEffect(state.message) {
        state.message?.let { snackbarHostState.showSnackbar(it); viewModel.clearUsersMessage() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("User Management") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") } },
                actions = {
                    IconButton(onClick = { viewModel.showCreateDialog() }) { Icon(Icons.Filled.Add, contentDescription = "Add user") }
                    IconButton(onClick = { viewModel.loadUsers() }) { Icon(Icons.Filled.Refresh, contentDescription = "Refresh") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(null to "All", "customer" to "Customer", "sales" to "Sales", "admin" to "Admin").forEach { (role, label) ->
                    FilterChip(
                        selected = state.roleFilter == role,
                        onClick = { viewModel.setRoleFilter(role) },
                        label = { Text(label) }
                    )
                }
            }
            when {
                state.users.loading -> SkeletonLoading()
                state.users.error != null -> ErrorView(message = state.users.error ?: "Error", onRetry = { viewModel.loadUsers() })
                state.users.data.isNullOrEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No users found") }
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.users.data ?: emptyList(), key = { it.id }) { user ->
                        ElevatedCard(modifier = Modifier.fillMaxWidth().clickable { onUserClick(user.id) }) {
                            Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(user.username, style = MaterialTheme.typography.titleSmall)
                                    Text(user.email ?: "No email", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AssistChip(onClick = { }, label = { Text(user.role ?: "?", style = MaterialTheme.typography.labelSmall) })
                                    TextButton(onClick = { viewModel.showEditDialog(user) }) { Text("Edit") }
                                    TextButton(onClick = { viewModel.deleteUser(user.id) }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Del") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showCreateDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideCreateDialog() },
            title = { Text("Create User") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = state.newUsername, onValueChange = viewModel::updateNewUsername, label = { Text("Username") }, singleLine = true)
                    OutlinedTextField(value = state.newEmail, onValueChange = viewModel::updateNewEmail, label = { Text("Email") }, singleLine = true)
                    OutlinedTextField(value = state.newPassword, onValueChange = viewModel::updateNewPassword, label = { Text("Password") }, singleLine = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("customer", "sales", "admin").forEach { role ->
                            FilterChip(selected = state.newRole == role, onClick = { viewModel.updateNewRole(role) }, label = { Text(role) })
                        }
                    }
                }
            },
            confirmButton = { Button(onClick = { viewModel.createUser() }) { Text("Create") } },
            dismissButton = { TextButton(onClick = { viewModel.hideCreateDialog() }) { Text("Cancel") } }
        )
    }

    if (state.showEditDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideEditDialog() },
            title = { Text("Edit User") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = state.editEmail, onValueChange = viewModel::updateEditEmail, label = { Text("Email") }, singleLine = true)
                    OutlinedTextField(value = state.editPassword, onValueChange = viewModel::updateEditPassword, label = { Text("New Password (optional)") }, singleLine = true)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("customer", "sales", "admin").forEach { role ->
                            FilterChip(selected = state.editRole == role, onClick = { viewModel.updateEditRole(role) }, label = { Text(role) })
                        }
                    }
                }
            },
            confirmButton = { Button(onClick = { viewModel.updateUser() }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { viewModel.hideEditDialog() }) { Text("Cancel") } }
        )
    }
}
