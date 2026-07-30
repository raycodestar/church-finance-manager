package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_categories")
data class ExpenseCategory(
    @PrimaryKey val id: String,
    val name: String,
    val isDefault: Boolean = false,
    val isDisabled: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)
