package com.example.shopping_site_andrio.ui.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shopping_site_andrio.ui.component.ErrorView
import com.example.shopping_site_andrio.ui.component.SkeletonLoading

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Profile") })
        }
    ) { padding ->
        when {
            uiState.user.loading -> {
                SkeletonLoading(modifier = Modifier.padding(padding))
            }
            uiState.user.error != null -> {
                ErrorView(
                    message = uiState.user.error ?: "Error",
                    onRetry = { viewModel.loadUser() },
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.user.data != null -> {
                val user = uiState.user.data!!
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Username: ${user.username}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Role: ${user.role ?: "customer"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = viewModel::updateEmail,
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Button(
                            onClick = { viewModel.saveProfile() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Update Profile")
                        }
                    }

                    item {
                        HorizontalDivider()
                        TextButton(
                            onClick = { viewModel.toggleChangePassword() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Change Password")
                        }
                    }

                    if (uiState.showChangePassword) {
                        item {
                            var oldPasswordVisible by remember { mutableStateOf(false) }
                            OutlinedTextField(
                                value = uiState.oldPassword,
                                onValueChange = viewModel::updateOldPassword,
                                label = { Text("Old Password") },
                                visualTransformation = if (oldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { oldPasswordVisible = !oldPasswordVisible }) {
                                        Icon(
                                            imageVector = if (oldPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (oldPasswordVisible) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        item {
                            var newPasswordVisible by remember { mutableStateOf(false) }
                            OutlinedTextField(
                                value = uiState.newPassword,
                                onValueChange = viewModel::updateNewPassword,
                                label = { Text("New Password") },
                                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                        Icon(
                                            imageVector = if (newPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                            contentDescription = if (newPasswordVisible) "Hide password" else "Show password"
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = uiState.verificationCode,
                                    onValueChange = viewModel::updateVerificationCode,
                                    label = { Text("Code") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                OutlinedButton(onClick = { viewModel.sendVerificationCode() }) {
                                    Text("Send")
                                }
                            }
                        }
                        item {
                            Button(
                                onClick = { viewModel.changePassword() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Change Password")
                            }
                        }
                    }

                    item {
                        HorizontalDivider()
                        Button(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout()
                    onLogout()
                }) {
                    Text("Logout", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
