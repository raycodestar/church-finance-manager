package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.EmptyStateView
import com.example.utils.CurrencyFormatter
import com.example.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentlyDeletedScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val deletedGatherings by viewModel.deletedGatherings.collectAsState()
    val deletedIncome by viewModel.deletedIncome.collectAsState()
    val deletedExpense by viewModel.deletedExpense.collectAsState()
    val churchProfile by viewModel.churchProfile.collectAsState()

    val currency = churchProfile?.defaultCurrency ?: "UGX"

    val totalDeletedCount = deletedGatherings.size + deletedIncome.size + deletedExpense.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recently Deleted") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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
                    text = "Soft-Deleted Records (Trash)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Restore items if deleted by mistake",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            if (totalDeletedCount == 0) {
                EmptyStateView(
                    title = "Trash Bin is Empty",
                    message = "No soft-deleted gatherings or financial records."
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    if (deletedGatherings.isNotEmpty()) {
                        item {
                            Text(
                                text = "Gatherings (${deletedGatherings.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(deletedGatherings) { g ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(g.name, fontWeight = FontWeight.Bold)
                                        Text("${g.gatheringTypeName} • ${DateUtils.formatDate(g.dateMillis)}", fontSize = 12.sp)
                                    }
                                    Button(onClick = { viewModel.restoreGathering(g.id, g.name) }) {
                                        Icon(Icons.Default.Restore, contentDescription = null)
                                        Spacer(modifier = Modifier.padding(2.dp))
                                        Text("Restore")
                                    }
                                }
                            }
                        }
                    }

                    if (deletedIncome.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Income Records (${deletedIncome.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(deletedIncome) { inc ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(inc.categoryName, fontWeight = FontWeight.Bold)
                                        Text("${CurrencyFormatter.format(inc.amount, currency)} • ${DateUtils.formatIso(inc.dateMillis)}", fontSize = 12.sp)
                                    }
                                    Button(onClick = { viewModel.restoreIncome(inc.id) }) {
                                        Icon(Icons.Default.Restore, contentDescription = null)
                                        Spacer(modifier = Modifier.padding(2.dp))
                                        Text("Restore")
                                    }
                                }
                            }
                        }
                    }

                    if (deletedExpense.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Expense Records (${deletedExpense.size})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(deletedExpense) { exp ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(exp.title, fontWeight = FontWeight.Bold)
                                        Text("${exp.categoryName} • ${CurrencyFormatter.format(exp.amount, currency)}", fontSize = 12.sp)
                                    }
                                    Button(onClick = { viewModel.restoreExpense(exp.id) }) {
                                        Icon(Icons.Default.Restore, contentDescription = null)
                                        Spacer(modifier = Modifier.padding(2.dp))
                                        Text("Restore")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
