package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "income_categories")
data class IncomeCategory(
    @PrimaryKey val id: String,
    val name: String,
    val isDefault: Boolean = false,
    val isDisabled: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)
