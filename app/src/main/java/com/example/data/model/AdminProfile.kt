package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admin_profiles")
data class AdminProfile(
    @PrimaryKey val id: String,
    val email: String,
    val fullName: String,
    val passwordHash: String = "",
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)
