package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gathering_types")
data class GatheringType(
    @PrimaryKey val id: String,
    val name: String,
    val isCustom: Boolean = false,
    val isDeleted: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)
