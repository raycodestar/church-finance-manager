package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_transactions")
data class ExpenseTransaction(
    @PrimaryKey val id: String,
    val title: String,
    val categoryId: String,
    val categoryName: String,
    val amount: Long, // Integer amount (UGX or minor units in cents)
    val dateMillis: Long,
    val paymentMethod: String, // Cash, Mobile Money, Bank Transfer, Cheque, Other
    val payee: String? = null,
    val description: String? = null,
    val attachmentUri: String? = null,
    val referenceNumber: String? = null,
    val gatheringId: String? = null,
    val gatheringName: String? = null,
    val isDeleted: Boolean = false,
    val deletedAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)
