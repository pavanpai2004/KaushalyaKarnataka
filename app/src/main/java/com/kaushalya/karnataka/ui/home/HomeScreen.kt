package com.kaushalya.karnataka.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaushalya.karnataka.data.model.TradeCategories
import com.kaushalya.karnataka.ui.components.*
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onWorkerClick: (String) -> Unit,
    onHireClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()
    val filteredWorkers by homeViewModel.filteredWorkers.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val errorMessage by homeViewModel.errorMessage.collectAsState()

    val categories = listOf("All") + TradeCategories.list

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Hero section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(PrimaryBlue, PrimaryBlueDark)
                        )
                    )
                    .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Text(
                        "Kaushalya Karnataka",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Find skilled workers near you",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(Modifier.height(16.dp))

                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = homeViewModel::onSearchQueryChange,
                        placeholder = { Text("Search by name, trade, or location…", color = Color.White.copy(alpha = 0.6f)) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.7f)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        )
                    )
                }
            }
        }

        // Category cards
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    "Categories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(TradeCategories.list) { category ->
                        CategoryCard(
                            category = category,
                            icon = TradeCategories.icons[category] ?: "🔧",
                            onClick = { onCategoryClick(category) }
                        )
                    }
                }
            }
        }

        // Category filter chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        label = category,
                        isSelected = selectedCategory == category,
                        onClick = { homeViewModel.onCategorySelected(category) }
                    )
                }
            }
        }

        // Worker count
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredWorkers.size} workers found",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMedium
                )
                if (selectedCategory != "All") {
                    TextButton(onClick = { homeViewModel.onCategorySelected("All") }) {
                        Text("Clear filter", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Error message
        errorMessage?.let { msg ->
            item {
                ErrorMessage(msg)
            }
        }

        // Loading or content
        when {
            isLoading -> {
                item { LoadingScreen() }
            }
            filteredWorkers.isEmpty() && errorMessage == null -> {
                item {
                    EmptyState(
                        icon = { Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(48.dp), tint = TextMedium) },
                        title = "No workers found",
                        subtitle = "Try a different category or search term"
                    )
                }
            }
            else -> {
                items(filteredWorkers, key = { it.id }) { worker ->
                    WorkerCard(
                        worker = worker,
                        onClick = { onWorkerClick(worker.id) },
                        onHireClick = { onHireClick(worker.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
