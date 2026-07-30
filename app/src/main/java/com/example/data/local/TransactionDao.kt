package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ExpenseTransaction
import com.example.data.model.IncomeTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // Income Transactions
    @Query("SELECT * FROM income_transactions WHERE isDeleted = 0 ORDER BY dateMillis DESC, createdAtMillis DESC")
    fun getAllIncome(): Flow<List<IncomeTransaction>>

    @Query("SELECT * FROM income_transactions WHERE isDeleted = 1 ORDER BY deletedAtMillis DESC")
    fun getDeletedIncome(): Flow<List<IncomeTransaction>>

    @Query("SELECT * FROM income_transactions WHERE isDeleted = 0 AND gatheringId = :gatheringId ORDER BY dateMillis DESC")
    fun getIncomeForGathering(gatheringId: String): Flow<List<IncomeTransaction>>

    @Query("SELECT * FROM income_transactions WHERE id = :id LIMIT 1")
    suspend fun getIncomeById(id: String): IncomeTransaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeTransaction)

    @Update
    suspend fun updateIncome(income: IncomeTransaction)

    @Query("UPDATE income_transactions SET isDeleted = 1, deletedAtMillis = :deletedAt WHERE id = :id")
    suspend fun softDeleteIncome(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE income_transactions SET isDeleted = 0, deletedAtMillis = NULL WHERE id = :id")
    suspend fun restoreIncome(id: String)

    @Query("DELETE FROM income_transactions WHERE id = :id")
    suspend fun permanentlyDeleteIncome(id: String)

    // Expense Transactions
    @Query("SELECT * FROM expense_transactions WHERE isDeleted = 0 ORDER BY dateMillis DESC, createdAtMillis DESC")
    fun getAllExpense(): Flow<List<ExpenseTransaction>>

    @Query("SELECT * FROM expense_transactions WHERE isDeleted = 1 ORDER BY deletedAtMillis DESC")
    fun getDeletedExpense(): Flow<List<ExpenseTransaction>>

    @Query("SELECT * FROM expense_transactions WHERE isDeleted = 0 AND gatheringId = :gatheringId ORDER BY dateMillis DESC")
    fun getExpenseForGathering(gatheringId: String): Flow<List<ExpenseTransaction>>

    @Query("SELECT * FROM expense_transactions WHERE id = :id LIMIT 1")
    suspend fun getExpenseById(id: String): ExpenseTransaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseTransaction)

    @Update
    suspend fun updateExpense(expense: ExpenseTransaction)

    @Query("UPDATE expense_transactions SET isDeleted = 1, deletedAtMillis = :deletedAt WHERE id = :id")
    suspend fun softDeleteExpense(id: String, deletedAt: Long = System.currentTimeMillis())

    @Query("UPDATE expense_transactions SET isDeleted = 0, deletedAtMillis = NULL WHERE id = :id")
    suspend fun restoreExpense(id: String)

    @Query("DELETE FROM expense_transactions WHERE id = :id")
    suspend fun permanentlyDeleteExpense(id: String)
}
