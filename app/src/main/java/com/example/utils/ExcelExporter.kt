package com.example.utils

import android.content.Context
import com.example.data.model.ChurchProfile
import com.example.data.model.ExpenseTransaction
import com.example.data.model.IncomeTransaction
import java.io.File
import java.io.FileOutputStream

object ExcelExporter {
    fun generateExcelSpreadsheet(
        context: Context,
        churchProfile: ChurchProfile?,
        incomeList: List<IncomeTransaction>,
        expenseList: List<ExpenseTransaction>
    ): File {
        val currency = churchProfile?.defaultCurrency ?: "UGX"
        val totalIncome = incomeList.sumOf { it.amount }
        val totalExpense = expenseList.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        val xmlSb = StringBuilder()
        xmlSb.append("<?xml version=\"1.0\"?>\n")
        xmlSb.append("<?mso-application progid=\"Excel.Sheet\"?>\n")
        xmlSb.append("<Workbook xmlns=\"urn:schemas-microsoft-com:office:spreadsheet\"\n")
        xmlSb.append(" xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n")
        xmlSb.append(" xmlns:x=\"urn:schemas-microsoft-com:office:excel\"\n")
        xmlSb.append(" xmlns:ss=\"urn:schemas-microsoft-com:office:spreadsheet\">\n")

        xmlSb.append("<Worksheet ss:Name=\"Financial Summary\">\n")
        xmlSb.append("<Table>\n")
        xmlSb.append("<Row><Cell><Data ss:Type=\"String\">${churchProfile?.name ?: "Church Finance Manager"}</Data></Cell></Row>\n")
        xmlSb.append("<Row><Cell><Data ss:Type=\"String\">Financial Report Export</Data></Cell></Row>\n")
        xmlSb.append("<Row><Cell><Data ss:Type=\"String\">Generated: ${DateUtils.formatDateTime(System.currentTimeMillis())}</Data></Cell></Row>\n")
        xmlSb.append("<Row></Row>\n")
        xmlSb.append("<Row><Cell><Data ss:Type=\"String\">Metric</Data></Cell><Cell><Data ss:Type=\"String\">Amount ($currency)</Data></Cell></Row>\n")
        xmlSb.append("<Row><Cell><Data ss:Type=\"String\">Total Income</Data></Cell><Cell><Data ss:Type=\"Number\">$totalIncome</Data></Cell></Row>\n")
        xmlSb.append("<Row><Cell><Data ss:Type=\"String\">Total Expenses</Data></Cell><Cell><Data ss:Type=\"Number\">$totalExpense</Data></Cell></Row>\n")
        xmlSb.append("<Row><Cell><Data ss:Type=\"String\">Remaining Balance</Data></Cell><Cell><Data ss:Type=\"Number\">$balance</Data></Cell></Row>\n")
        xmlSb.append("</Table>\n</Worksheet>\n")

        // Income Sheet
        xmlSb.append("<Worksheet ss:Name=\"Income Transactions\">\n")
        xmlSb.append("<Table>\n")
        xmlSb.append("<Row><Cell><Data ss:Type=\"String\">Date</Data></Cell><Cell><Data ss:Type=\"String\">Category</Data></Cell><Cell><Data ss:Type=\"String\">Amount ($currency)</Data></Cell><Cell><Data ss:Type=\"String\">Payment Method</Data></Cell><Cell><Data ss:Type=\"String\">Gathering</Data></Cell><Cell><Data ss:Type=\"String\">Notes</Data></Cell></Row>\n")
        for (inc in incomeList) {
            xmlSb.append("<Row>")
            xmlSb.append("<Cell><Data ss:Type=\"String\">${DateUtils.formatIso(inc.dateMillis)}</Data></Cell>")
            xmlSb.append("<Cell><Data ss:Type=\"String\">${inc.categoryName}</Data></Cell>")
            xmlSb.append("<Cell><Data ss:Type=\"Number\">${inc.amount}</Data></Cell>")
            xmlSb.append("<Cell><Data ss:Type=\"String\">${inc.paymentMethod}</Data></Cell>")
            xmlSb.append("<Cell><Data ss:Type=\"String\">${inc.gatheringName ?: ""}</Data></Cell>")
            xmlSb.append("<Cell><Data ss:Type=\"String\">${inc.description ?: ""}</Data></Cell>")
            xmlSb.append("</Row>\n")
        }
        xmlSb.append("</Table>\n</Worksheet>\n")

        // Expense Sheet
        xmlSb.append("<Worksheet ss:Name=\"Expense Transactions\">\n")
        xmlSb.append("<Table>\n")
        xmlSb.append("<Row><Cell><Data ss:Type=\"String\">Date</Data></Cell><Cell><Data ss:Type=\"String\">Title</Data></Cell><Cell><Data ss:Type=\"String\">Category</Data></Cell><Cell><Data ss:Type=\"String\">Amount ($currency)</Data></Cell><Cell><Data ss:Type=\"String\">Payment Method</Data></Cell><Cell><Data ss:Type=\"String\">Payee</Data></Cell><Cell><Data ss:Type=\"String\">Reference</Data></Cell></Row>\n")
        for (exp in expenseList) {
            xmlSb.append("<Row>")
            xmlSb.append("<Cell><Data ss:Type=\"String\">${DateUtils.formatIso(exp.dateMillis)}</Data></Cell>")
            xmlSb.append("<Cell><Data ss:Type=\"String\">${exp.title}</Data></Cell>")
            xmlSb.append("<Cell><Data ss:Type=\"String\">${exp.categoryName}</Data></Cell>")
            xmlSb.append("<Cell><Data ss:Type=\"Number\">${exp.amount}</Data></Cell>")
            xmlSb.append("<Cell><Data ss:Type=\"String\">${exp.paymentMethod}</Data></Cell>")
            xmlSb.append("<Cell><Data ss:Type=\"String\">${exp.payee ?: ""}</Data></Cell>")
            xmlSb.append("<Cell><Data ss:Type=\"String\">${exp.referenceNumber ?: ""}</Data></Cell>")
            xmlSb.append("</Row>\n")
        }
        xmlSb.append("</Table>\n</Worksheet>\n")

        xmlSb.append("</Workbook>")

        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()

        val file = File(reportsDir, "Church_Report_${System.currentTimeMillis()}.xls")
        FileOutputStream(file).use { out ->
            out.write(xmlSb.toString().toByteArray())
        }
        return file
    }
}
