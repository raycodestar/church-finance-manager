package com.example.utils

import android.content.Context
import com.example.data.model.ExpenseTransaction
import com.example.data.model.IncomeTransaction
import java.io.File
import java.io.FileOutputStream

object CsvExporter {
    fun generateTransactionsCsv(
        context: Context,
        currencyCode: String,
        incomeList: List<IncomeTransaction>,
        expenseList: List<ExpenseTransaction>
    ): File {
        val sb = StringBuilder()
        sb.append("Type,ID,Date,Category,Title/Notes,Gathering,Payment Method,Payee/Ref,Amount ($currencyCode)\n")

        for (inc in incomeList) {
            val dateStr = DateUtils.formatIso(inc.dateMillis)
            val notes = (inc.description ?: "").replace(",", ";")
            val gathering = (inc.gatheringName ?: "").replace(",", ";")
            val ref = (inc.referenceNumber ?: "").replace(",", ";")
            val category = inc.categoryName.replace(",", ";")

            sb.append("Income,${inc.id},$dateStr,$category,$notes,$gathering,${inc.paymentMethod},$ref,${inc.amount}\n")
        }

        for (exp in expenseList) {
            val dateStr = DateUtils.formatIso(exp.dateMillis)
            val title = exp.title.replace(",", ";")
            val gathering = (exp.gatheringName ?: "").replace(",", ";")
            val payee = "${exp.payee ?: ""} ${exp.referenceNumber ?: ""}".trim().replace(",", ";")
            val category = exp.categoryName.replace(",", ";")

            sb.append("Expense,${exp.id},$dateStr,$category,$title,$gathering,${exp.paymentMethod},$payee,${exp.amount}\n")
        }

        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()

        val file = File(reportsDir, "Church_Financial_Data_${System.currentTimeMillis()}.csv")
        FileOutputStream(file).use { out ->
            out.write(sb.toString().toByteArray())
        }
        return file
    }
}
