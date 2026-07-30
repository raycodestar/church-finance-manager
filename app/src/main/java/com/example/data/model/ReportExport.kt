package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "report_exports")
data class ReportExport(
    @PrimaryKey val id: String,
    val reportTitle: String,
    val fileFormat: String, // PDF, XLSX, CSV
    val fileUri: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)
