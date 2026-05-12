package com.kaushalya.karnataka.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.kaushalya.karnataka.data.model.UiState
import com.kaushalya.karnataka.ui.components.*
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerProfileScreen(
    workerId: String,
    onBack: () -> Unit,
    onWriteReview: (String) -> Unit,
    viewModel: WorkerViewModel = hiltViewModel()
) {
    val workerState by viewModel.workerState.collectAsState()
    val services by viewModel.services.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val portfolioPhotos by viewModel.portfolioPhotos.collectAsState()
    val hireState by viewModel.hireState.collectAsState()

    var showHireDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(workerId) { viewModel.loadWorker(workerId) }

    LaunchedEffect(hireState) {
        if (hireState is UiState.Success) {
            snackbarHostState.showSnackbar("Hire request sent! The worker will contact you.")
            viewModel.resetHireState()
        } else if (hireState is UiState.Error) {
            snackbarHostState.showSnackbar((hireState as UiState.Error).message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Worker Profile") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when (val state = workerState) {
            is UiState.Loading -> LoadingScreen()
            is UiState.Error -> ErrorMessage(state.message, Modifier.padding(paddingValues))
            is UiState.Success -> {
                val worker = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    // Profile header card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier.size(100.dp).clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(PrimaryBlue, PrimaryBlueDark))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (worker.profilePhotoUrl.isNotEmpty()) {
                                        AsyncImage(
                                            model = worker.profilePhotoUrl,
                                            contentDescription = "Profile",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Text(
                                            worker.name.take(1).uppercase(),
                                            style = MaterialTheme.typography.displaySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(worker.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                    if (worker.isVerified) {
                                        Spacer(Modifier.width(6.dp))
                                        Icon(Icons.Default.Verified, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                                    }
                                }

                                Surface(
                                    color = SecondarySaffronLight,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        worker.tradeCategory,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RatingStars(worker.averageRating.toFloat(), size = 20)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "${String.format("%.1f", worker.averageRating)} (${worker.totalRatings} ratings)",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                if (worker.location.isNotEmpty()) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                        Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp), tint = TextMedium)
                                        Text(worker.location, style = MaterialTheme.typography.bodyMedium, color = TextMedium)
                                    }
                                }

                                if (worker.experienceYears > 0) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                        Icon(Icons.Default.WorkHistory, null, modifier = Modifier.size(16.dp), tint = TextMedium)
                                        Spacer(Modifier.width(4.dp))
                                        Text("${worker.experienceYears} years experience", style = MaterialTheme.typography.bodyMedium, color = TextMedium)
                                    }
                                }

                                // Availability badge
                                Surface(
                                    color = if (worker.isAvailable) AccentGreenLight else Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = if (worker.isAvailable) AccentGreen else ErrorRed,
                                            shape = CircleShape,
                                            modifier = Modifier.size(8.dp)
                                        ) {}
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            if (worker.isAvailable) "Available for hire" else "Currently busy",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = if (worker.isAvailable) Color(0xFF065F46) else Color(0xFF991B1B)
                                        )
                                    }
                                }

                                if (worker.bio.isNotEmpty()) {
                                    Spacer(Modifier.height(12.dp))
                                    Text(worker.bio, style = MaterialTheme.typography.bodyMedium, color = TextMedium)
                                }

                                Spacer(Modifier.height(16.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Button(
                                        onClick = { showHireDialog = true },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Handshake, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Hire Me", fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { onWriteReview(workerId) },
                                        modifier = Modifier.weight(1f).height(48.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.RateReview, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Review")
                                    }
                                }

                                if (worker.phone.isNotEmpty()) {
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = { /* Call intent would go here */ },
                                        modifier = Modifier.fillMaxWidth().height(44.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Phone, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Call ${worker.phone}")
                                    }
                                }
                            }
                        }
                    }

                    // Services section
                    if (services.isNotEmpty()) {
                        item {
                            Text(
                                "Services & Pricing",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(services) { service ->
                            ServiceCard(service = service, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                        }
                    }

                    // Portfolio section
                    if (portfolioPhotos.isNotEmpty()) {
                        item {
                            Text(
                                "Work Portfolio",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(portfolioPhotos) { photoUrl ->
                                    Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                                        AsyncImage(
                                            model = photoUrl,
                                            contentDescription = "Portfolio",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(140.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Reviews section
                    item {
                        Text(
                            "Reviews (${reviews.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    if (reviews.isEmpty()) {
                        item {
                            EmptyState(
                                icon = { Icon(Icons.Default.Reviews, null, modifier = Modifier.size(40.dp), tint = TextMedium) },
                                title = "No reviews yet",
                                subtitle = "Be the first to review this worker!"
                            )
                        }
                    } else {
                        items(reviews) { review ->
                            ReviewCard(review = review, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                        }
                    }
                }
            }
            else -> {}
        }
    }

    // Hire Me dialog
    if (showHireDialog) {
        var customerName by remember { mutableStateOf("") }
        var customerPhone by remember { mutableStateOf("") }
        var serviceName by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showHireDialog = false },
            title = { Text("Send Hire Request", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Your Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it.filter { c -> c.isDigit() }.take(10) },
                        label = { Text("Your Phone *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = serviceName,
                        onValueChange = { serviceName = it },
                        label = { Text("Service Needed *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (optional)") },
                        minLines = 2,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendHireRequest(workerId, customerName, customerPhone, serviceName, description)
                        showHireDialog = false
                    },
                    enabled = customerName.isNotBlank() && customerPhone.isNotBlank() && serviceName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) { Text("Send Request") }
            },
            dismissButton = { TextButton(onClick = { showHireDialog = false }) { Text("Cancel") } }
        )
    }
}
