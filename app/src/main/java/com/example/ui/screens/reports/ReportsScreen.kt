package com.example.ui.screens.reports

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.FilterChipsRow
import com.example.ui.components.StatCard
import com.example.utils.CsvExporter
import com.example.utils.CurrencyFormatter
import com.example.utils.DateUtils
import com.example.utils.ExcelExporter
import com.example.utils.PdfExporter
import com.example.utils.PeriodFilter

@Composable
fun ReportsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val churchProfile by viewModel.churchProfile.collectAsState()
    val adminProfile by viewModel.adminProfile.collectAsState()
    val summary by viewModel.dashboardSummary.collectAsState()

    val selectedPeriod by viewModel.selectedPeriod.collectAsState()
    val customDateRange by viewModel.customDateRange.collectAsState()
    val incomeList by viewModel.allIncome.collectAsState()
    val expenseList by viewModel.allExpense.collectAsState()

    val currency = summary.currencyCode
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val periodLabel = if (selectedPeriod == PeriodFilter.CUSTOM && customDateRange != null) {
        "${DateUtils.formatDate(customDateRange!!.startMillis)} - ${DateUtils.formatDate(customDateRange!!.endMillis)}"
    } else {
        selectedPeriod.label
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Financial Reporting & Exports",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Generate and share downloadable reports for church board, audit, or leadership",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Period Filter Selection
        Text(
            text = "Select Reporting Period",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(4.dp))

        FilterChipsRow(
            selectedPeriod = selectedPeriod,
            customDateRange = customDateRange,
            onPeriodSelected = { period, start, end -> viewModel.setPeriodFilter(period, start, end) },
            modifier = Modifier.padding(vertical = 0.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Report Preview Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Period Financial Summary ($periodLabel)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Income:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        CurrencyFormatter.format(summary.totalIncome, currency),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Expenses:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        CurrencyFormatter.format(summary.totalExpense, currency),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Remaining Balance:", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        CurrencyFormatter.format(summary.currentBalance, currency),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (summary.currentBalance >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Export Options",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Option 1: PDF
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Download PDF Financial Report", fontWeight = FontWeight.Bold)
                    Text("Clean, printable report with church header & signature lines", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                Button(onClick = {
                    val dateRange = DateUtils.getDateRange(selectedPeriod, customDateRange?.startMillis, customDateRange?.endMillis)
                    val periodFilteredInc = incomeList.filter { it.dateMillis in dateRange.startMillis..dateRange.endMillis }
                    val periodFilteredExp = expenseList.filter { it.dateMillis in dateRange.startMillis..dateRange.endMillis }

                    val pdf = PdfExporter.generateFinancialReportPdf(
                        context = context,
                        churchProfile = churchProfile,
                        reportTitle = "Church Financial Statement ($periodLabel)",
                        periodText = periodLabel,
                        incomeList = periodFilteredInc,
                        expenseList = periodFilteredExp,
                        preparedBy = adminProfile?.fullName ?: "Church Administrator"
                    )
                    val uri = PdfExporter.getShareableUri(context, pdf)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share PDF Report"))
                    statusMessage = "PDF Report ($periodLabel) generated!"
                }) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("PDF")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Option 2: Excel
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.TableChart,
                    contentDescription = null,
                    tint = Color(0xFF107C41), // Excel Green
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Export Excel Spreadsheet", fontWeight = FontWeight.Bold)
                    Text("Multi-sheet workbook for accounting and audit analysis", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                Button(onClick = {
                    val dateRange = DateUtils.getDateRange(selectedPeriod, customDateRange?.startMillis, customDateRange?.endMillis)
                    val periodFilteredInc = incomeList.filter { it.dateMillis in dateRange.startMillis..dateRange.endMillis }
                    val periodFilteredExp = expenseList.filter { it.dateMillis in dateRange.startMillis..dateRange.endMillis }

                    val xls = ExcelExporter.generateExcelSpreadsheet(
                        context = context,
                        churchProfile = churchProfile,
                        incomeList = periodFilteredInc,
                        expenseList = periodFilteredExp
                    )
                    val uri = PdfExporter.getShareableUri(context, xls)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/vnd.ms-excel"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Excel File"))
                    statusMessage = "Excel File ($periodLabel) generated!"
                }) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Excel")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Option 3: CSV
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Export CSV File", fontWeight = FontWeight.Bold)
                    Text("Raw comma-separated data table for software import", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                OutlinedButton(onClick = {
                    val dateRange = DateUtils.getDateRange(selectedPeriod, customDateRange?.startMillis, customDateRange?.endMillis)
                    val periodFilteredInc = incomeList.filter { it.dateMillis in dateRange.startMillis..dateRange.endMillis }
                    val periodFilteredExp = expenseList.filter { it.dateMillis in dateRange.startMillis..dateRange.endMillis }

                    val csv = CsvExporter.generateTransactionsCsv(
                        context = context,
                        currencyCode = currency,
                        incomeList = periodFilteredInc,
                        expenseList = periodFilteredExp
                    )
                    val uri = PdfExporter.getShareableUri(context, csv)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/csv"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share CSV File"))
                    statusMessage = "CSV File ($periodLabel) generated!"
                }) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CSV")
                }
            }
        }

        if (statusMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = statusMessage!!,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
