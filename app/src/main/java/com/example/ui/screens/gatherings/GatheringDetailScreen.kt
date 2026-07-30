package com.example.ui.screens.gatherings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.StatCard
import com.example.ui.components.TransactionItem
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.utils.CurrencyFormatter
import com.example.utils.DateUtils
import com.example.utils.PdfExporter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GatheringDetailScreen(
    gatheringId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onRecordIncomeForGathering: (String, String) -> Unit,
    onRecordExpenseForGathering: (String, String) -> Unit
) {
    val context = LocalContext.current
    val gatherings by viewModel.allGatherings.collectAsState()
    val gathering = gatherings.find { it.id == gatheringId }

    val allIncome by viewModel.allIncome.collectAsState()
    val allExpense by viewModel.allExpense.collectAsState()
    val churchProfile by viewModel.churchProfile.collectAsState()

    val currency = churchProfile?.defaultCurrency ?: "UGX"

    val gatheringIncome = allIncome.filter { it.gatheringId == gatheringId }
    val gatheringExpenses = allExpense.filter { it.gatheringId == gatheringId }

    val totalTithe = gatheringIncome.filter { it.categoryName.equals("Tithe", ignoreCase = true) }.sumOf { it.amount }
    val totalOffertory = gatheringIncome.filter { it.categoryName.contains("Offertory", ignoreCase = true) }.sumOf { it.amount }
    val totalOtherIncome = gatheringIncome.filter {
        !it.categoryName.equals("Tithe", ignoreCase = true) && !it.categoryName.contains("Offertory", ignoreCase = true)
    }.sumOf { it.amount }

    val totalCollected = gatheringIncome.sumOf { it.amount }
    val totalExpenseAmount = gatheringExpenses.sumOf { it.amount }
    val netBalance = totalCollected - totalExpenseAmount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(gathering?.name ?: "Gathering Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val pdfFile = PdfExporter.generateFinancialReportPdf(
                            context = context,
                            churchProfile = churchProfile,
                            reportTitle = "Gathering Report - ${gathering?.name}",
                            periodText = DateUtils.formatDate(gathering?.dateMillis ?: System.currentTimeMillis()),
                            incomeList = gatheringIncome,
                            expenseList = gatheringExpenses
                        )
                        val uri = PdfExporter.getShareableUri(context, pdfFile)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Gathering Report"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share PDF Report")
                    }
                }
            )
        }
    ) { padding ->
        if (gathering == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Gathering not found.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${gathering.gatheringTypeName} • ${DateUtils.formatDate(gathering.dateMillis)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (!gathering.description.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = gathering.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Total Collected",
                            amountFormatted = CurrencyFormatter.format(totalCollected, currency),
                            icon = Icons.Default.ArrowUpward,
                            iconBgColor = IncomeGreenLight,
                            iconTintColor = IncomeGreen
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Related Expenses",
                            amountFormatted = CurrencyFormatter.format(totalExpenseAmount, currency),
                            icon = Icons.Default.ArrowDownward,
                            iconBgColor = ExpenseRedLight,
                            iconTintColor = ExpenseRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                StatCard(
                    title = "Net Gathering Balance",
                    amountFormatted = CurrencyFormatter.format(netBalance, currency),
                    icon = Icons.Default.Event,
                    iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                    iconTintColor = MaterialTheme.colorScheme.primary,
                    subtitle = "Income minus Gathering Expenses"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onRecordIncomeForGathering(gathering.id, gathering.name) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Income")
                    }

                    OutlinedButton(
                        onClick = { onRecordExpenseForGathering(gathering.id, gathering.name) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Expense")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Breakdown list
                Text(
                    text = "Collections Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Tithe:", style = MaterialTheme.typography.bodyMedium)
                            Text(CurrencyFormatter.format(totalTithe, currency), fontWeight = FontWeight.Bold, color = IncomeGreen)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Offertory:", style = MaterialTheme.typography.bodyMedium)
                            Text(CurrencyFormatter.format(totalOffertory, currency), fontWeight = FontWeight.Bold, color = IncomeGreen)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Other Collections:", style = MaterialTheme.typography.bodyMedium)
                            Text(CurrencyFormatter.format(totalOtherIncome, currency), fontWeight = FontWeight.Bold, color = IncomeGreen)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Income Transactions (${gatheringIncome.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (gatheringIncome.isEmpty()) {
                    Text("No income recorded for this gathering yet.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                } else {
                    gatheringIncome.forEach { inc ->
                        TransactionItem(
                            isIncome = true,
                            title = inc.categoryName,
                            categoryName = inc.categoryName,
                            amount = inc.amount,
                            currencyCode = currency,
                            dateMillis = inc.dateMillis,
                            paymentMethod = inc.paymentMethod,
                            subtitle = inc.description,
                            onEdit = {},
                            onDelete = { viewModel.softDeleteIncome(inc.id, inc.categoryName) },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Related Expenses (${gatheringExpenses.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (gatheringExpenses.isEmpty()) {
                    Text("No expenses recorded for this gathering.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                } else {
                    gatheringExpenses.forEach { exp ->
                        TransactionItem(
                            isIncome = false,
                            title = exp.title,
                            categoryName = exp.categoryName,
                            amount = exp.amount,
                            currencyCode = currency,
                            dateMillis = exp.dateMillis,
                            paymentMethod = exp.paymentMethod,
                            subtitle = exp.payee,
                            onEdit = {},
                            onDelete = { viewModel.softDeleteExpense(exp.id, exp.title) },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
