package com.kaushalya.karnataka.ui.help

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaushalya.karnataka.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Contact support
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PrimaryBlueLight)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Support, null, tint = PrimaryBlue)
                        Spacer(Modifier.width(8.dp))
                        Text("Need Help?", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Contact our support team for any issues or questions.", style = MaterialTheme.typography.bodyMedium, color = TextMedium)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Email, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Contact Support")
                    }
                }
            }

            Text("Frequently Asked Questions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            val faqs = listOf(
                "How do I register as a worker?" to "Go to the signup page, select 'I'm a Worker', fill in your details including trade category, and create your account. Once registered, you can add services and portfolio photos from your dashboard.",
                "How do I hire a worker?" to "Browse workers on the home screen or search by category. Tap on a worker to view their profile, then click the 'Hire Me' button to send a hire request with your contact details.",
                "Is there a fee to use the app?" to "Kaushalya Karnataka is currently free for both workers and customers. We may introduce premium features in the future.",
                "How does the rating system work?" to "After a service is completed, customers can rate workers from 1 to 5 stars and leave a text review. The worker's average rating is calculated from all reviews.",
                "How do I update my services and pricing?" to "Go to your Worker Dashboard, where you can add, edit, or remove services. You can set fixed prices or 'starting at' prices for each service.",
                "Is my personal information safe?" to "Yes, we use Supabase for secure data storage with row-level security. Your personal details are only visible to users you interact with.",
                "How do I upload portfolio photos?" to "From your Worker Dashboard, tap 'Add Photo' in the portfolio section. You can upload photos of your completed work to showcase your skills.",
                "Can I use the app in rural areas?" to "Yes! The app is designed for minimal data usage and works well on slow connections. Some features may be limited without internet access."
            )

            faqs.forEach { (question, answer) ->
                FaqItem(question, answer)
            }

            // Community guidelines
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Community Guidelines", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    val guidelines = listOf(
                        "Be honest and transparent in your profile and pricing",
                        "Treat all users with respect and professionalism",
                        "Only accept jobs you can complete on time",
                        "Leave fair and constructive reviews",
                        "Report any suspicious or fraudulent activity"
                    )
                    guidelines.forEach { guideline ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("•", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Text(guideline, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.animateContentSize()
    ) {
        Column(modifier = Modifier.clickable { expanded = !expanded }.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(question, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    null, tint = PrimaryBlue
                )
            }
            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(answer, style = MaterialTheme.typography.bodySmall, color = TextMedium)
            }
        }
    }
}
