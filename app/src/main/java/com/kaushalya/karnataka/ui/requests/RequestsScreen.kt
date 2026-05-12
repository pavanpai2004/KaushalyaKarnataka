package com.kaushalya.karnataka.ui.requests

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaushalya.karnataka.data.model.HireRequest
import com.kaushalya.karnataka.data.model.RequestStatus
import com.kaushalya.karnataka.ui.components.*
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.AuthViewModel
import com.kaushalya.karnataka.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    onRequestClick: (String) -> Unit,
    workerViewModel: WorkerViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val hireRequests by workerViewModel.hireRequests.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Pending", "Active", "Completed")

    LaunchedEffect(Unit) {
        val userId = authViewModel.currentUserId
        if (userId.isNotEmpty()) {
            workerViewModel.loadHireRequests(userId, isWorker = true)
        }
    }

    val filteredRequests = when (selectedTab) {
        1 -> hireRequests.filter { it.status == RequestStatus.PENDING }
        2 -> hireRequests.filter { it.status == RequestStatus.ACCEPTED || it.status == RequestStatus.IN_PROGRESS }
        3 -> hireRequests.filter { it.status == RequestStatus.COMPLETED }
        else -> hireRequests
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.height(40.dp))
        Text(
            "Service Requests",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, style = MaterialTheme.typography.labelMedium) }
                )
            }
        }

        if (filteredRequests.isEmpty()) {
            EmptyState(
                icon = { Icon(Icons.Default.Assignment, null, modifier = Modifier.size(48.dp), tint = TextMedium) },
                title = "No requests",
                subtitle = "Your service requests will appear here"
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredRequests) { request ->
                    RequestCard(
                        request = request,
                        onClick = { onRequestClick(request.id) },
                        onAccept = { workerViewModel.updateRequestStatus(request.id, RequestStatus.ACCEPTED) },
                        onDecline = { workerViewModel.updateRequestStatus(request.id, RequestStatus.DECLINED) },
                        onComplete = { workerViewModel.updateRequestStatus(request.id, RequestStatus.COMPLETED) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun RequestCard(
    request: HireRequest,
    onClick: () -> Unit,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onComplete: () -> Unit
) {
    val statusColor = when (request.status) {
        RequestStatus.PENDING -> SecondarySaffron
        RequestStatus.ACCEPTED, RequestStatus.IN_PROGRESS -> PrimaryBlue
        RequestStatus.COMPLETED -> AccentGreen
        RequestStatus.CANCELLED, RequestStatus.DECLINED -> ErrorRed
        else -> TextMedium
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(request.serviceRequested, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text("From: ${request.customerName}", style = MaterialTheme.typography.bodySmall, color = TextMedium)
                    if (request.customerPhone.isNotEmpty()) {
                        Text("📞 ${request.customerPhone}", style = MaterialTheme.typography.bodySmall, color = TextMedium)
                    }
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        request.status,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (request.description.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(request.description, style = MaterialTheme.typography.bodySmall, color = TextMedium)
            }

            if (request.status == RequestStatus.PENDING) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Accept")
                    }
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Decline")
                    }
                }
            }

            if (request.status == RequestStatus.ACCEPTED || request.status == RequestStatus.IN_PROGRESS) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Mark Completed")
                }
            }
        }
    }
}
