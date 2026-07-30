package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "church_profiles")
data class ChurchProfile(
    @PrimaryKey val id: String,
    val name: String,
    val logoUri: String? = null,
    val location: String,
    val contactPhone: String,
    val contactEmail: String,
    val defaultCurrency: String = "UGX",
    val financialYearStartMonth: Int = 1, // 1 = January
    val adminFullName: String = "",
    val isInitialized: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)
