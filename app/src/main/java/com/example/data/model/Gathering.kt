package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gatherings")
data class Gathering(
    @PrimaryKey val id: String,
    val name: String,
    val gatheringTypeId: String,
    val gatheringTypeName: String,
    val dateMillis: Long,
    val startTime: String? = null,
    val description: String? = null,
    val isDeleted: Boolean = false,
    val deletedAtMillis: Long? = null,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)
