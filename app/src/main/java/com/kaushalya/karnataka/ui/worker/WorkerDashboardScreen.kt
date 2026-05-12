package com.kaushalya.karnataka.ui.worker

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaushalya.karnataka.data.model.Service
import com.kaushalya.karnataka.data.model.UiState
import com.kaushalya.karnataka.ui.components.*
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDashboardScreen(
    workerId: String,
    onBack: () -> Unit,
    onViewPublicProfile: (String) -> Unit,
    viewModel: WorkerViewModel = hiltViewModel()
) {
    val workerState by viewModel.workerState.collectAsState()
    val services by viewModel.services.collectAsState()
    val bioState by viewModel.bioState.collectAsState()
    val saveState by viewModel.saveState.collectAsState()
    val portfolioPhotos by viewModel.portfolioPhotos.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddServiceDialog by remember { mutableStateOf(false) }

    val portfolioPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.uploadPortfolioPhoto(workerId, it, context) }
    }
    val profilePhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { viewModel.uploadProfilePhoto(workerId, it, context) }
    }

    LaunchedEffect(workerId) { viewModel.loadWorker(workerId) }
    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) { snackbarHostState.showSnackbar("Saved!"); viewModel.resetSaveState() }
        else if (saveState is UiState.Error) { snackbarHostState.showSnackbar((saveState as UiState.Error).message) }
    }
    LaunchedEffect(bioState) {
        if (bioState is UiState.Success) {
            val bio = (bioState as UiState.Success).data
            if (workerState is UiState.Success) viewModel.saveWorker((workerState as UiState.Success<com.kaushalya.karnataka.data.model.Worker>).data.copy(bio = bio))
            viewModel.resetBioState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Dashboard") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = { TextButton(onClick = { onViewPublicProfile(workerId) }) { Text("View Profile") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = workerState) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Success -> {
                val worker = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Stats card
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Profile Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    FilledTonalIconButton(onClick = { profilePhotoPicker.launch("image/*") }) { Icon(Icons.Default.AddAPhoto, "Photo") }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(worker.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                                Text(worker.tradeCategory, style = MaterialTheme.typography.bodyMedium, color = PrimaryBlue)
                                if (worker.phone.isNotEmpty()) Text(worker.phone, style = MaterialTheme.typography.bodySmall, color = TextMedium)
                                Spacer(Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                    StatItem(String.format("%.1f", worker.averageRating), "Rating")
                                    StatItem("${worker.totalRatings}", "Reviews")
                                    StatItem("${services.size}", "Services")
                                    StatItem("${portfolioPhotos.size}", "Photos")
                                }
                            }
                        }
                    }

                    // AI Bio
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = AccentGreenLight)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, null, tint = AccentGreen)
                                    Spacer(Modifier.width(8.dp))
                                    Text("AI Bio Generator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                if (worker.bio.isNotEmpty()) { Spacer(Modifier.height(8.dp)); Text(worker.bio, style = MaterialTheme.typography.bodyMedium) }
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.generateBio(worker.name, worker.tradeCategory) },
                                    enabled = bioState !is UiState.Loading && services.isNotEmpty(),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    if (bioState is UiState.Loading) { CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp); Spacer(Modifier.width(8.dp)) }
                                    Text(if (worker.bio.isEmpty()) "Generate Bio" else "Regenerate Bio")
                                }
                                if (services.isEmpty()) Text("Add services first to generate a bio", style = MaterialTheme.typography.bodySmall, color = TextMedium)
                            }
                        }
                    }

                    // Services
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("My Services", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            FilledTonalButton(onClick = { showAddServiceDialog = true }) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Add")
                            }
                        }
                    }
                    if (services.isEmpty()) { item { Text("No services added yet.", style = MaterialTheme.typography.bodyMedium, color = TextMedium) } }
                    else { items(services, key = { it.id }) { service -> ServiceCard(service = service, isOwner = true, onDelete = { viewModel.deleteService(workerId, service.id) }) } }

                    // Portfolio
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Portfolio (${portfolioPhotos.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            FilledTonalButton(onClick = { portfolioPicker.launch("image/*") }) {
                                Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("Add")
                            }
                        }
                    }
                    if (portfolioPhotos.isEmpty()) { item { Text("Upload photos of your best work!", style = MaterialTheme.typography.bodyMedium, color = TextMedium) } }
                }
            }
            is UiState.Error -> ErrorMessage(state.message)
            else -> {}
        }
    }

    if (showAddServiceDialog) {
        AddServiceDialog(onDismiss = { showAddServiceDialog = false }, onConfirm = { service -> viewModel.addService(workerId, service); showAddServiceDialog = false })
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextMedium)
    }
}

@Composable
private fun AddServiceDialog(onDismiss: () -> Unit, onConfirm: (Service) -> Unit) {
    var serviceName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceType by remember { mutableStateOf("Fixed") }
    var estimatedTime by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Service", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = serviceName, onValueChange = { serviceName = it }, label = { Text("Service Name *") }, placeholder = { Text("e.g., Fan Installation") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                OutlinedTextField(value = estimatedTime, onValueChange = { estimatedTime = it }, label = { Text("Estimated Time") }, placeholder = { Text("e.g., 1-2 hours") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = price, onValueChange = { price = it.filter { c -> c.isDigit() || c == '.' } }, label = { Text("Price (₹) *") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp))
                    Column {
                        Text("Type", style = MaterialTheme.typography.labelSmall)
                        Row { listOf("Fixed", "Starting at").forEach { type -> FilterChip(selected = priceType == type, onClick = { priceType = type }, label = { Text(type, style = MaterialTheme.typography.labelSmall) }, modifier = Modifier.padding(end = 4.dp)) } }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (serviceName.isNotBlank() && price.isNotBlank()) onConfirm(Service(serviceName = serviceName.trim(), price = price.toDoubleOrNull() ?: 0.0, priceType = priceType, description = description.trim(), estimatedTime = estimatedTime.trim()))
            }, enabled = serviceName.isNotBlank() && price.isNotBlank(), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
