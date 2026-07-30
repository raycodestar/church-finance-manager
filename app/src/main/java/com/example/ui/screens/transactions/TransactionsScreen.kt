package com.example.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ExpenseTransaction
import com.example.data.model.IncomeTransaction
import com.example.ui.MainViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.components.TransactionItem

import com.example.ui.components.FilterChipsRow
import com.example.utils.DateUtils

enum class TxTypeFilter { ALL, INCOME, EXPENSE }

@Composable
fun TransactionsScreen(
    viewModel: MainViewModel,
    onOpenQuickActions: () -> Unit
) {
    val incomeList by viewModel.allIncome.collectAsState()
    val expenseList by viewModel.allExpense.collectAsState()
    val churchProfile by viewModel.churchProfile.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val customDateRange by viewModel.customDateRange.collectAsState()

    val currency = churchProfile?.defaultCurrency ?: "UGX"

    var searchQuery by remember { mutableStateOf("") }
    var typeFilter by remember { mutableStateOf(TxTypeFilter.ALL) }

    val filteredList = remember(searchQuery, typeFilter, selectedPeriod, customDateRange, incomeList, expenseList) {
        val range = DateUtils.getDateRange(selectedPeriod, customDateRange?.startMillis, customDateRange?.endMillis)

        val periodFilteredIncome = incomeList.filter { it.dateMillis in range.startMillis..range.endMillis }
        val periodFilteredExpense = expenseList.filter { it.dateMillis in range.startMillis..range.endMillis }

        val allItems = mutableListOf<Pair<Boolean, Any>>()
        if (typeFilter == TxTypeFilter.ALL || typeFilter == TxTypeFilter.INCOME) {
            periodFilteredIncome.forEach { allItems.add(Pair(true, it)) }
        }
        if (typeFilter == TxTypeFilter.ALL || typeFilter == TxTypeFilter.EXPENSE) {
            periodFilteredExpense.forEach { allItems.add(Pair(false, it)) }
        }

        allItems.filter { (isInc, tx) ->
            if (searchQuery.isBlank()) true
            else if (isInc) {
                val inc = tx as IncomeTransaction
                inc.categoryName.contains(searchQuery, ignoreCase = true) ||
                    (inc.gatheringName ?: "").contains(searchQuery, ignoreCase = true) ||
                    (inc.description ?: "").contains(searchQuery, ignoreCase = true)
            } else {
                val exp = tx as ExpenseTransaction
                exp.title.contains(searchQuery, ignoreCase = true) ||
                    exp.categoryName.contains(searchQuery, ignoreCase = true) ||
                    (exp.payee ?: "").contains(searchQuery, ignoreCase = true)
            }
        }.sortedByDescending { (isInc, tx) ->
            if (isInc) (tx as IncomeTransaction).dateMillis
            else (tx as ExpenseTransaction).dateMillis
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onOpenQuickActions,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Transaction")
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
                    text = "Financial Transactions",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "View, search, and manage all income and church expenses",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by category, title, payee, or notes...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                FilterChipsRow(
                    selectedPeriod = selectedPeriod,
                    customDateRange = customDateRange,
                    onPeriodSelected = { period, start, end -> viewModel.setPeriodFilter(period, start, end) },
                    modifier = Modifier.padding(vertical = 0.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = typeFilter == TxTypeFilter.ALL,
                            onClick = { typeFilter = TxTypeFilter.ALL },
                            label = {
                                Text(
                                    text = "All Transactions",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        )
                        FilterChip(
                            selected = typeFilter == TxTypeFilter.INCOME,
                            onClick = { typeFilter = TxTypeFilter.INCOME },
                            label = {
                                Text(
                                    text = "Income Only",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    FilterChip(
                        selected = typeFilter == TxTypeFilter.EXPENSE,
                        onClick = { typeFilter = TxTypeFilter.EXPENSE },
                        label = {
                            Text(
                                text = "Expenses Only",
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    )
                }
            }

            if (filteredList.isEmpty()) {
                EmptyStateView(
                    title = "No Transactions Found",
                    message = if (searchQuery.isNotBlank()) "No records match '$searchQuery'." else "Record income or expense transactions.",
                    actionLabel = "Record Transaction",
                    onAction = onOpenQuickActions
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(filteredList) { (isInc, tx) ->
                        if (isInc) {
                            val inc = tx as IncomeTransaction
                            TransactionItem(
                                isIncome = true,
                                title = inc.gatheringName ?: inc.categoryName,
                                categoryName = inc.categoryName,
                                amount = inc.amount,
                                currencyCode = currency,
                                dateMillis = inc.dateMillis,
                                paymentMethod = inc.paymentMethod,
                                subtitle = inc.description,
                                onEdit = { },
                                onDelete = { viewModel.softDeleteIncome(inc.id, inc.categoryName) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            val exp = tx as ExpenseTransaction
                            TransactionItem(
                                isIncome = false,
                                title = exp.title,
                                categoryName = exp.categoryName,
                                amount = exp.amount,
                                currencyCode = currency,
                                dateMillis = exp.dateMillis,
                                paymentMethod = exp.paymentMethod,
                                subtitle = exp.payee,
                                onEdit = { },
                                onDelete = { viewModel.softDeleteExpense(exp.id, exp.title) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
