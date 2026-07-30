package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ActivityLog
import com.example.data.model.ReportExport
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestampMillis DESC")
    fun getAllActivityLogs(): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLog)

    @Query("SELECT * FROM report_exports ORDER BY createdAtMillis DESC")
    fun getAllReportExports(): Flow<List<ReportExport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReportExport(report: ReportExport)
}
