package com.kaushalya.karnataka.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaushalya.karnataka.data.model.TradeCategories
import com.kaushalya.karnataka.data.model.UiState
import com.kaushalya.karnataka.data.model.Worker
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.AuthViewModel
import com.kaushalya.karnataka.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    onSignupSuccess: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onBack: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    workerViewModel: WorkerViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var isWorkerMode by remember { mutableStateOf(true) }

    val authState by authViewModel.authState.collectAsState()
    val saveState by workerViewModel.saveState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        if (authState is UiState.Success) {
            val uid = authViewModel.currentUserId
            if (isWorkerMode && name.isNotBlank() && selectedCategory.isNotBlank()) {
                workerViewModel.saveWorker(
                    Worker(
                        id = uid,
                        name = name.trim(),
                        tradeCategory = selectedCategory,
                        phone = phone.trim(),
                        location = location.trim(),
                        role = "worker"
                    )
                )
            } else {
                onSignupSuccess(uid)
            }
        } else if (authState is UiState.Error) {
            snackbarHostState.showSnackbar((authState as UiState.Error).message)
        }
    }

    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) {
            onSignupSuccess(authViewModel.currentUserId)
            workerViewModel.resetSaveState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Join Kaushalya Karnataka",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Role selector
            Text("I am a...", style = MaterialTheme.typography.titleSmall, color = TextMedium)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isWorkerMode) PrimaryBlueLight else Color(0xFFF1F5F9)
                    ),
                    border = if (isWorkerMode) CardDefaults.outlinedCardBorder().copy() else null,
                    onClick = { isWorkerMode = true }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Build, null, tint = if (isWorkerMode) PrimaryBlue else TextMedium)
                        Text("Worker", fontWeight = if (isWorkerMode) FontWeight.Bold else FontWeight.Normal)
                        Text("Offer my services", style = MaterialTheme.typography.labelSmall, color = TextMedium)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!isWorkerMode) PrimaryBlueLight else Color(0xFFF1F5F9)
                    ),
                    onClick = { isWorkerMode = false }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Person, null, tint = if (!isWorkerMode) PrimaryBlue else TextMedium)
                        Text("Customer", fontWeight = if (!isWorkerMode) FontWeight.Bold else FontWeight.Normal)
                        Text("Find workers", style = MaterialTheme.typography.labelSmall, color = TextMedium)
                    }
                }
            }

            Divider()

            // Common name/phone fields for both modes
            Text(
                if (isWorkerMode) "Worker Details" else "Your Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name *") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it.filter { c -> c.isDigit() }.take(10) },
                label = { Text("Phone Number *") },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (isWorkerMode) {

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Area / Locality") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                    placeholder = { Text("e.g., Indiranagar, Bengaluru") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Trade Category *") },
                        leadingIcon = { Icon(Icons.Default.Category, null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        TradeCategories.list.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(TradeCategories.icons[category] ?: "🔧")
                                        Spacer(Modifier.width(8.dp))
                                        Text(category)
                                    }
                                },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Divider()
            }

            Text("Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email *") },
                leadingIcon = { Icon(Icons.Default.Email, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password (min 6 chars) *") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            val isLoading = authState is UiState.Loading || saveState is UiState.Loading

            Button(
                onClick = { authViewModel.signUp(email.trim(), password, name.trim(), phone.trim()) },
                enabled = !isLoading && email.isNotBlank() && password.length >= 6 &&
                        name.isNotBlank() && phone.isNotBlank() &&
                        (!isWorkerMode || selectedCategory.isNotBlank()),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                    Spacer(Modifier.width(8.dp))
                }
                Text("Create Account", fontWeight = FontWeight.Bold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Already have an account? ", color = TextMedium)
                TextButton(onClick = onNavigateToLogin, contentPadding = PaddingValues(0.dp)) {
                    Text("Sign In", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
