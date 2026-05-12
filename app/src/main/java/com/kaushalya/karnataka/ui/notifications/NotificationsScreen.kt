package com.kaushalya.karnataka.ui.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaushalya.karnataka.data.model.Notification
import com.kaushalya.karnataka.ui.components.EmptyState
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.AuthViewModel
import com.kaushalya.karnataka.viewmodel.WorkerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    workerViewModel: WorkerViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val notifications by workerViewModel.notifications.collectAsState()

    LaunchedEffect(Unit) {
        val uid = authViewModel.currentUserId
        if (uid.isNotEmpty()) workerViewModel.loadNotifications(uid)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    if (notifications.any { !it.isRead }) {
                        TextButton(onClick = { workerViewModel.markAllNotificationsRead(authViewModel.currentUserId) }) {
                            Text("Mark all read")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = { Icon(Icons.Default.Notifications, null, modifier = Modifier.size(48.dp), tint = TextMedium) },
                    title = "No notifications",
                    subtitle = "You're all caught up!"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = { workerViewModel.markNotificationRead(notification.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: Notification, onClick: () -> Unit) {
    val icon = when (notification.type) {
        "request" -> Icons.Default.Assignment
        "review" -> Icons.Default.Star
        "update" -> Icons.Default.Update
        else -> Icons.Default.Notifications
    }
    val iconColor = when (notification.type) {
        "request" -> PrimaryBlue
        "review" -> SecondarySaffron
        "update" -> AccentGreen
        else -> TextMedium
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) SurfaceWhite else PrimaryBlueLight.copy(alpha = 0.3f)
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = iconColor.copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(notification.title, style = MaterialTheme.typography.titleSmall, fontWeight = if (!notification.isRead) FontWeight.Bold else FontWeight.Normal)
                Text(notification.message, style = MaterialTheme.typography.bodySmall, color = TextMedium)
            }
            if (!notification.isRead) {
                Surface(color = PrimaryBlue, shape = CircleShape, modifier = Modifier.size(8.dp)) {}
            }
        }
    }
}
