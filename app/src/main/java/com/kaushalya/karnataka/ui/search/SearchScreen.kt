package com.kaushalya.karnataka.ui.search

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaushalya.karnataka.data.model.TradeCategories
import com.kaushalya.karnataka.ui.components.*
import com.kaushalya.karnataka.ui.theme.*
import com.kaushalya.karnataka.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onWorkerClick: (String) -> Unit,
    onHireClick: (String) -> Unit,
    initialCategory: String? = null,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val searchQuery by homeViewModel.searchQuery.collectAsState()
    val selectedCategory by homeViewModel.selectedCategory.collectAsState()
    val filteredWorkers by homeViewModel.filteredWorkers.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    val categories = listOf("All") + TradeCategories.list

    LaunchedEffect(initialCategory) {
        if (initialCategory != null && initialCategory.isNotBlank()) {
            homeViewModel.onCategorySelected(initialCategory)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search header
        Surface(
            color = SurfaceWhite,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Spacer(Modifier.height(32.dp))
                Text(
                    "Search Workers",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = homeViewModel::onSearchQueryChange,
                    placeholder = { Text("Search by name, trade, or location…") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { homeViewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(Modifier.height(12.dp))

                // Category filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryChip(
                            label = if (category == "All") "All Categories" else category,
                            isSelected = selectedCategory == category,
                            onClick = { homeViewModel.onCategorySelected(category) }
                        )
                    }
                }
            }
        }

        // Results count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${filteredWorkers.size} results",
                style = MaterialTheme.typography.bodySmall,
                color = TextMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Sort, null, modifier = Modifier.size(16.dp), tint = TextMedium)
                Spacer(Modifier.width(4.dp))
                Text("By Rating", style = MaterialTheme.typography.labelSmall, color = TextMedium)
            }
        }

        // Results list
        when {
            isLoading -> LoadingScreen()
            filteredWorkers.isEmpty() -> {
                EmptyState(
                    icon = { Icon(Icons.Default.SearchOff, null, modifier = Modifier.size(48.dp), tint = TextMedium) },
                    title = "No workers found",
                    subtitle = "Try adjusting your search or filters"
                )
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredWorkers, key = { it.id }) { worker ->
                        WorkerCard(
                            worker = worker,
                            onClick = { onWorkerClick(worker.id) },
                            onHireClick = { onHireClick(worker.id) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}
