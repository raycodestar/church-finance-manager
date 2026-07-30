package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ActivityLog
import com.example.data.model.AdminProfile
import com.example.data.model.ChurchProfile
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseTransaction
import com.example.data.model.Gathering
import com.example.data.model.GatheringType
import com.example.data.model.IncomeCategory
import com.example.data.model.IncomeTransaction
import com.example.data.model.ReportExport

@Database(
    entities = [
        AdminProfile::class,
        ChurchProfile::class,
        GatheringType::class,
        Gathering::class,
        IncomeCategory::class,
        ExpenseCategory::class,
        IncomeTransaction::class,
        ExpenseTransaction::class,
        ActivityLog::class,
        ReportExport::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun churchDao(): ChurchDao
    abstract fun gatheringDao(): GatheringDao
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "church_finance_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
