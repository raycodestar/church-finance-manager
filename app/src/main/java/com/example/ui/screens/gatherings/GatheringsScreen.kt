package com.example.ui.screens.gatherings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MainViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GatheringCard

@Composable
fun GatheringsScreen(
    viewModel: MainViewModel,
    onOpenGatheringDetail: (String) -> Unit,
    onCreateGathering: () -> Unit
) {
    val gatherings by viewModel.allGatherings.collectAsState()
    val incomeList by viewModel.allIncome.collectAsState()
    val expenseList by viewModel.allExpense.collectAsState()
    val churchProfile by viewModel.churchProfile.collectAsState()

    val currency = churchProfile?.defaultCurrency ?: "UGX"

    var searchQuery by remember { mutableStateOf("") }

    val filteredGatherings = gatherings.filter { g ->
        searchQuery.isBlank() ||
            g.name.contains(searchQuery, ignoreCase = true) ||
            g.gatheringTypeName.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateGathering,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Create Gathering")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Church Gatherings",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Sunday services, midweek prayers, and special events",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search gatherings by name or type...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (filteredGatherings.isEmpty()) {
                EmptyStateView(
                    title = "No Gatherings Found",
                    message = if (searchQuery.isNotBlank()) "No gatherings match '$searchQuery'." else "Create your first Sunday service or event gathering.",
                    actionLabel = "Create Gathering",
                    onAction = onCreateGathering
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(filteredGatherings, key = { it.id }) { gathering ->
                        val gInc = incomeList.filter { it.gatheringId == gathering.id }.sumOf { it.amount }
                        val gExp = expenseList.filter { it.gatheringId == gathering.id }.sumOf { it.amount }

                        GatheringCard(
                            name = gathering.name,
                            typeName = gathering.gatheringTypeName,
                            dateMillis = gathering.dateMillis,
                            totalIncome = gInc,
                            totalExpense = gExp,
                            currencyCode = currency,
                            onClick = { onOpenGatheringDetail(gathering.id) },
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
