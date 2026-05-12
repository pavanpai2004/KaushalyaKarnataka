package com.kaushalya.karnataka.ui.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaushalya.karnataka.data.model.Review
import com.kaushalya.karnataka.data.model.UiState
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.AuthViewModel
import com.kaushalya.karnataka.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    workerId: String,
    onBack: () -> Unit,
    workerViewModel: WorkerViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var selectedRating by remember { mutableStateOf(0f) }
    var reviewText by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var isRecommended by remember { mutableStateOf(true) }

    val saveState by workerViewModel.saveState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) {
            snackbarHostState.showSnackbar("Review submitted! Thank you.")
            workerViewModel.resetSaveState()
            onBack()
        } else if (saveState is UiState.Error) {
            snackbarHostState.showSnackbar((saveState as UiState.Error).message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rate & Review") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Share your experience", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            // Star rating
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("How would you rate this worker?", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in 1..5) {
                            IconButton(onClick = { selectedRating = i.toFloat() }) {
                                Icon(
                                    imageVector = if (selectedRating >= i) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = "$i stars",
                                    tint = if (selectedRating >= i) SecondarySaffron else Color.Gray,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                    if (selectedRating > 0) {
                        val label = when (selectedRating.toInt()) {
                            1 -> "Poor"; 2 -> "Fair"; 3 -> "Good"; 4 -> "Very Good"; 5 -> "Excellent"; else -> ""
                        }
                        Text(label, style = MaterialTheme.typography.bodyMedium, color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }

            OutlinedTextField(
                value = customerName,
                onValueChange = { customerName = it },
                label = { Text("Your Name *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = reviewText,
                onValueChange = { if (it.length <= 300) reviewText = it },
                label = { Text("Write a review (optional)") },
                placeholder = { Text("Describe your experience…") },
                minLines = 4,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth(),
                supportingText = { Text("${reviewText.length}/300") },
                shape = RoundedCornerShape(12.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isRecommended, onCheckedChange = { isRecommended = it })
                Spacer(Modifier.width(8.dp))
                Text("I recommend this worker", style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = {
                    if (selectedRating > 0 && customerName.isNotBlank()) {
                        workerViewModel.submitReview(
                            workerId,
                            Review(
                                workerId = workerId,
                                customerId = authViewModel.currentUserId,
                                customerName = customerName.trim(),
                                rating = selectedRating,
                                reviewText = reviewText.trim(),
                                isRecommended = isRecommended
                            )
                        )
                    }
                },
                enabled = selectedRating > 0 && customerName.isNotBlank() && saveState !is UiState.Loading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                if (saveState is UiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                    Spacer(Modifier.width(8.dp))
                }
                Text("Submit Review", fontWeight = FontWeight.Bold)
            }
        }
    }
}
