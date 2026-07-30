package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_transactions")
data class IncomeTransaction(
    @PrimaryKey val id: String,
    val gatheringId: String? = null,
    val gatheringName: String? = null,
    val categoryId: String,
    val categoryName: String,
    val amount: Long, // Integer amount (UGX or minor units in cents)
    val paymentMethod: String, // Cash, Mobile Money, Bank Transfer, Cheque, Other
    val dateMillis: Long,
    val description: String? = null,
    val referenceNumber: String? = null,
    val attachmentUri: String? = null,
    val isDeleted: Boolean = false,
    val deletedAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)
