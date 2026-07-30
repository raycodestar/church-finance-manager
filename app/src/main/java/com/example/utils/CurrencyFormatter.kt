package com.example.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyFormatter {
    fun format(amount: Long, currencyCode: String = "UGX"): String {
        val trimmedCode = currencyCode.trim().uppercase()
        val isZeroDecimal = listOf("UGX", "JPY", "RWF", "TZS", "KES").contains(trimmedCode)
        
        val format = NumberFormat.getIntegerInstance(Locale.US)
        val formattedNumber = format.format(amount)
        
        return "$trimmedCode $formattedNumber"
    }

    fun parseAmount(amountString: String): Long {
        if (amountString.isBlank()) return 0L
        val clean = amountString.replace(",", "").replace(" ", "").trim()
        return clean.toDoubleOrNull()?.toLong() ?: 0L
    }
}
