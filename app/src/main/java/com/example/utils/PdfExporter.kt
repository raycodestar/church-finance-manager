package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.ChurchProfile
import com.example.data.model.ExpenseTransaction
import com.example.data.model.IncomeTransaction
import java.io.File
import java.io.FileOutputStream

object PdfExporter {
    fun generateFinancialReportPdf(
        context: Context,
        churchProfile: ChurchProfile?,
        reportTitle: String,
        periodText: String,
        incomeList: List<IncomeTransaction>,
        expenseList: List<ExpenseTransaction>,
        preparedBy: String = "Church Administrator"
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size @ 72 dpi
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val paint = Paint()
        val currency = churchProfile?.defaultCurrency ?: "UGX"

        val totalIncome = incomeList.sumOf { it.amount }
        val totalExpense = expenseList.sumOf { it.amount }
        val netBalance = totalIncome - totalExpense

        // Background
        canvas.drawColor(Color.WHITE)

        var yPos = 40f

        // Church Name Header
        paint.color = Color.parseColor("#0F172A") // Deep Navy
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(churchProfile?.name ?: "Church Finance Manager", 40f, yPos, paint)

        yPos += 18f
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.parseColor("#475569")
        val contactLine = "${churchProfile?.location ?: "Church Address"} | ${churchProfile?.contactPhone ?: ""} | ${churchProfile?.contactEmail ?: ""}"
        canvas.drawText(contactLine, 40f, yPos, paint)

        yPos += 15f
        // Divider line
        paint.strokeWidth = 1f
        paint.color = Color.parseColor("#CBD5E1")
        canvas.drawLine(40f, yPos, 555f, yPos, paint)

        // Report Title & Period
        yPos += 25f
        paint.color = Color.parseColor("#1E293B")
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(reportTitle, 40f, yPos, paint)

        yPos += 16f
        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.parseColor("#64748B")
        canvas.drawText("Reporting Period: $periodText", 40f, yPos, paint)
        canvas.drawText("Generated: ${DateUtils.formatDateTime(System.currentTimeMillis())}", 380f, yPos, paint)

        // Summary Cards Box
        yPos += 25f
        paint.color = Color.parseColor("#F1F5F9")
        canvas.drawRect(40f, yPos, 555f, yPos + 65f, paint)

        // Total Income
        paint.color = Color.parseColor("#16A34A") // Green
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("TOTAL INCOME", 55f, yPos + 22f, paint)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(CurrencyFormatter.format(totalIncome, currency), 55f, yPos + 44f, paint)

        // Total Expense
        paint.color = Color.parseColor("#DC2626") // Red
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("TOTAL EXPENSE", 230f, yPos + 22f, paint)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(CurrencyFormatter.format(totalExpense, currency), 230f, yPos + 44f, paint)

        // Net Balance
        paint.color = if (netBalance >= 0) Color.parseColor("#2563EB") else Color.parseColor("#DC2626")
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("NET BALANCE", 410f, yPos + 22f, paint)
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(CurrencyFormatter.format(netBalance, currency), 410f, yPos + 44f, paint)

        yPos += 85f

        // Section Title: Income Breakdown
        if (incomeList.isNotEmpty()) {
            paint.color = Color.parseColor("#0F172A")
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Income Records (${incomeList.size})", 40f, yPos, paint)

            yPos += 15f
            // Table Header
            paint.color = Color.parseColor("#E2E8F0")
            canvas.drawRect(40f, yPos, 555f, yPos + 20f, paint)

            paint.color = Color.parseColor("#334155")
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Date", 45f, yPos + 14f, paint)
            canvas.drawText("Category", 120f, yPos + 14f, paint)
            canvas.drawText("Method", 260f, yPos + 14f, paint)
            canvas.drawText("Gathering / Notes", 340f, yPos + 14f, paint)
            canvas.drawText("Amount", 480f, yPos + 14f, paint)

            yPos += 20f
            paint.typeface = Typeface.DEFAULT

            val maxShowIncome = incomeList.take(15)
            for (inc in maxShowIncome) {
                if (yPos > 720f) break
                paint.color = Color.parseColor("#1E293B")
                canvas.drawText(DateUtils.formatIso(inc.dateMillis), 45f, yPos + 12f, paint)
                canvas.drawText(inc.categoryName.take(20), 120f, yPos + 12f, paint)
                canvas.drawText(inc.paymentMethod.take(12), 260f, yPos + 12f, paint)
                val note = inc.gatheringName ?: inc.description ?: "-"
                canvas.drawText(note.take(20), 340f, yPos + 12f, paint)

                paint.color = Color.parseColor("#16A34A")
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(CurrencyFormatter.format(inc.amount, currency), 470f, yPos + 12f, paint)
                paint.typeface = Typeface.DEFAULT

                yPos += 16f
                paint.color = Color.parseColor("#F1F5F9")
                canvas.drawLine(40f, yPos, 555f, yPos, paint)
            }
            if (incomeList.size > 15) {
                paint.color = Color.parseColor("#64748B")
                paint.textSize = 8f
                canvas.drawText("... and ${incomeList.size - 15} more income records", 45f, yPos + 12f, paint)
                yPos += 15f
            }
        }

        yPos += 15f
        // Section Title: Expense Records
        if (expenseList.isNotEmpty() && yPos < 700f) {
            paint.color = Color.parseColor("#0F172A")
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Expense Records (${expenseList.size})", 40f, yPos, paint)

            yPos += 15f
            paint.color = Color.parseColor("#E2E8F0")
            canvas.drawRect(40f, yPos, 555f, yPos + 20f, paint)

            paint.color = Color.parseColor("#334155")
            paint.textSize = 9f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Date", 45f, yPos + 14f, paint)
            canvas.drawText("Title / Category", 120f, yPos + 14f, paint)
            canvas.drawText("Payee / Method", 260f, yPos + 14f, paint)
            canvas.drawText("Ref / Notes", 360f, yPos + 14f, paint)
            canvas.drawText("Amount", 480f, yPos + 14f, paint)

            yPos += 20f
            paint.typeface = Typeface.DEFAULT

            val maxShowExpense = expenseList.take(12)
            for (exp in maxShowExpense) {
                if (yPos > 720f) break
                paint.color = Color.parseColor("#1E293B")
                canvas.drawText(DateUtils.formatIso(exp.dateMillis), 45f, yPos + 12f, paint)
                val catText = "${exp.title.take(12)} (${exp.categoryName.take(10)})"
                canvas.drawText(catText, 120f, yPos + 12f, paint)
                val payeeText = "${exp.payee ?: "-"} / ${exp.paymentMethod}"
                canvas.drawText(payeeText.take(18), 260f, yPos + 12f, paint)
                val refText = exp.referenceNumber ?: exp.description ?: "-"
                canvas.drawText(refText.take(15), 360f, yPos + 12f, paint)

                paint.color = Color.parseColor("#DC2626")
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                canvas.drawText(CurrencyFormatter.format(exp.amount, currency), 470f, yPos + 12f, paint)
                paint.typeface = Typeface.DEFAULT

                yPos += 16f
                paint.color = Color.parseColor("#F1F5F9")
                canvas.drawLine(40f, yPos, 555f, yPos, paint)
            }
        }

        // Signature & Footer Block at bottom
        val footerY = 760f
        paint.strokeWidth = 1f
        paint.color = Color.parseColor("#94A3B8")
        canvas.drawLine(40f, footerY, 200f, footerY, paint)
        canvas.drawLine(350f, footerY, 555f, footerY, paint)

        paint.color = Color.parseColor("#475569")
        paint.textSize = 9f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Prepared by: $preparedBy", 40f, footerY + 14f, paint)
        canvas.drawText("Authorizing Officer / Pastor Signature", 350f, footerY + 14f, paint)

        pdfDocument.finishPage(page)

        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) reportsDir.mkdirs()

        val fileName = "Church_Report_${System.currentTimeMillis()}.pdf"
        val outputFile = File(reportsDir, fileName)

        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }

    fun getShareableUri(context: Context, file: File) = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}
