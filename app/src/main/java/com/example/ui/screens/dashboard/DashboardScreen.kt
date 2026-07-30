package com.example.ui.screens.dashboard

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.CategoryBreakdownChart
import com.example.ui.components.CategorySum
import com.example.ui.components.EmptyStateView
import com.example.ui.components.FilterChipsRow
import com.example.ui.components.GatheringCard
import com.example.ui.components.IncomeExpenseChart
import com.example.ui.components.StatCard
import com.example.ui.components.TransactionItem
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.DarkSlatePrimary
import com.example.ui.theme.DarkSlateSecondary
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.EmeraldIncomeLight
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.RoseExpenseLight
import com.example.ui.theme.SlateBorder
import com.example.utils.CurrencyFormatter

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToGatherings: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onOpenQuickActions: () -> Unit,
    onOpenGatheringDetail: (String) -> Unit
) {
    val summary by viewModel.dashboardSummary.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val customDateRange by viewModel.customDateRange.collectAsState()

    val gatherings by viewModel.allGatherings.collectAsState()
    val incomeList by viewModel.allIncome.collectAsState()
    val expenseList by viewModel.allExpense.collectAsState()

    val currency = summary.currencyCode
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Period Selector Row
            FilterChipsRow(
                selectedPeriod = selectedPeriod,
                customDateRange = customDateRange,
                onPeriodSelected = { period, start, end -> viewModel.setPeriodFilter(period, start, end) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Balance Gradient Card (Sleek Theme Hero)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(DarkSlatePrimary, DarkSlateSecondary)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            text = "CURRENT BALANCE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                fontSize = 11.sp
                            ),
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = CurrencyFormatter.format(summary.currentBalance, currency),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontStyle = FontStyle.Italic,
                                fontSize = 32.sp
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Divider line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.12f))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "TOTAL INCOME",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "+${CurrencyFormatter.format(summary.totalIncome, currency)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = Color(0xFF34D399) // Sleek Emerald
                                )
                            }

                            Column {
                                Text(
                                    text = "TOTAL EXPENSES",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "-${CurrencyFormatter.format(summary.totalExpense, currency)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = Color(0xFFFB7185) // Sleek Coral/Rose
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Financial Stats Grid
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Tithes Collection",
                            amountFormatted = CurrencyFormatter.format(summary.totalTithe, currency),
                            icon = Icons.Default.Payments,
                            iconBgColor = EmeraldIncomeLight,
                            iconTintColor = EmeraldIncome
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Offertory & Giving",
                            amountFormatted = CurrencyFormatter.format(summary.totalOffertory, currency),
                            icon = Icons.Default.CardGiftcard,
                            iconBgColor = Color(0xFFDBEAFE),
                            iconTintColor = BlueAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Income vs Expense Chart
                IncomeExpenseChart(
                    totalIncome = summary.totalIncome,
                    totalExpense = summary.totalExpense,
                    currencyCode = currency
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category Breakdowns
                val topIncomeCategories = incomeList
                    .groupBy { it.categoryName }
                    .map { (cat, list) -> CategorySum(cat, list.sumOf { it.amount }) }

                val topExpenseCategories = expenseList
                    .groupBy { it.categoryName }
                    .map { (cat, list) -> CategorySum(cat, list.sumOf { it.amount }) }

                CategoryBreakdownChart(
                    title = "Income by Category",
                    items = topIncomeCategories,
                    currencyCode = currency,
                    accentColor = EmeraldIncome
                )

                Spacer(modifier = Modifier.height(16.dp))

                CategoryBreakdownChart(
                    title = "Expense Breakdown",
                    items = topExpenseCategories,
                    currencyCode = currency,
                    accentColor = RoseExpense
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Gatherings Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Gatherings",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onNavigateToGatherings) {
                        Text(
                            text = "VIEW ALL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BlueAccent
                            )
                        )
                    }
                }

                if (gatherings.isEmpty()) {
                    EmptyStateView(
                        title = "No Gatherings Found",
                        message = "Create a Sunday Service or Midweek gathering to record collections.",
                        actionLabel = "Create Gathering",
                        onAction = onOpenQuickActions
                    )
                } else {
                    gatherings.take(3).forEach { gathering ->
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
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Transactions Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onNavigateToTransactions) {
                        Text(
                            text = "VIEW ALL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BlueAccent
                            )
                        )
                    }
                }

                if (incomeList.isEmpty() && expenseList.isEmpty()) {
                    EmptyStateView(
                        title = "No Financial Records Yet",
                        message = "Record tithe, offertory, donations, or church expenses.",
                        actionLabel = "Record Income",
                        onAction = onOpenQuickActions
                    )
                } else {
                    val combinedRecent = (
                        incomeList.map { Pair(true, it) } +
                        expenseList.map { Pair(false, it) }
                    ).sortedByDescending {
                        if (it.first) (it.second as com.example.data.model.IncomeTransaction).dateMillis
                        else (it.second as com.example.data.model.ExpenseTransaction).dateMillis
                    }.take(5)

                    combinedRecent.forEach { (isInc, tx) ->
                        if (isInc) {
                            val inc = tx as com.example.data.model.IncomeTransaction
                            TransactionItem(
                                isIncome = true,
                                title = inc.gatheringName ?: inc.categoryName,
                                categoryName = inc.categoryName,
                                amount = inc.amount,
                                currencyCode = currency,
                                dateMillis = inc.dateMillis,
                                paymentMethod = inc.paymentMethod,
                                subtitle = inc.description,
                                onEdit = { /* Handled in Transactions screen */ },
                                onDelete = { viewModel.softDeleteIncome(inc.id, inc.categoryName) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            val exp = tx as com.example.data.model.ExpenseTransaction
                            TransactionItem(
                                isIncome = false,
                                title = exp.title,
                                categoryName = exp.categoryName,
                                amount = exp.amount,
                                currencyCode = currency,
                                dateMillis = exp.dateMillis,
                                paymentMethod = exp.paymentMethod,
                                subtitle = exp.payee,
                                onEdit = { /* Handled in Transactions screen */ },
                                onDelete = { viewModel.softDeleteExpense(exp.id, exp.title) },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(90.dp))
            }
        }

        // Floating Action Button - Emerald Green Sleek FAB
        FloatingActionButton(
            onClick = onOpenQuickActions,
            containerColor = EmeraldIncome,
            contentColor = Color.White,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }
    }
}

