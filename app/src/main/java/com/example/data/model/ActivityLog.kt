package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey val id: String,
    val actionType: String,
    val recordType: String,
    val recordId: String,
    val description: String,
    val timestampMillis: Long = System.currentTimeMillis()
)
