package com.kaushalya.karnataka.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaushalya.karnataka.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onHelp: () -> Unit,
    isLoggedIn: Boolean,
    userName: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            // Profile header
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = PrimaryBlueLight, shape = RoundedCornerShape(12.dp), modifier = Modifier.size(56.dp)) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.Default.Person, null, tint = PrimaryBlue, modifier = Modifier.size(28.dp))
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            if (userName.isNotEmpty()) userName else if (isLoggedIn) "User" else "Guest",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (isLoggedIn) "Logged in" else "Not logged in",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMedium
                        )
                    }
                }
            }

            // Settings sections
            SettingsSection("Account") {
                SettingsItem(Icons.Default.Person, "Edit Profile", onClick = {})
                SettingsItem(Icons.Default.Lock, "Change Password", onClick = {})
                SettingsItem(Icons.Default.Notifications, "Notification Preferences", onClick = {})
            }

            SettingsSection("Preferences") {
                SettingsItem(Icons.Default.LocationOn, "Location Settings", onClick = {})
                SettingsItem(Icons.Default.Language, "Language", subtitle = "English", onClick = {})
            }

            SettingsSection("Support") {
                SettingsItem(Icons.Default.Help, "Help & FAQ", onClick = onHelp)
                SettingsItem(Icons.Default.Feedback, "Send Feedback", onClick = {})
                SettingsItem(Icons.Default.Policy, "Privacy Policy", onClick = {})
                SettingsItem(Icons.Default.Description, "Terms of Service", onClick = {})
            }

            SettingsSection("About") {
                SettingsItem(Icons.Default.Info, "App Version", subtitle = "1.0.0", onClick = {})
            }

            if (isLoggedIn) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sign Out", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = PrimaryBlue, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
        Card(shape = RoundedCornerShape(12.dp)) {
            Column { content() }
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = TextMedium, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextMedium)
        }
        Icon(Icons.Default.ChevronRight, null, tint = TextMedium, modifier = Modifier.size(20.dp))
    }
}
