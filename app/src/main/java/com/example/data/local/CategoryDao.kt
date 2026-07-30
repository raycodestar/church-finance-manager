package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ExpenseCategory
import com.example.data.model.IncomeCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    // Income Categories
    @Query("SELECT * FROM income_categories WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllIncomeCategories(): Flow<List<IncomeCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncomeCategory(category: IncomeCategory)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIncomeCategories(categories: List<IncomeCategory>)

    @Update
    suspend fun updateIncomeCategory(category: IncomeCategory)

    @Query("UPDATE income_categories SET isDisabled = :disabled WHERE id = :id")
    suspend fun setIncomeCategoryDisabled(id: String, disabled: Boolean)

    // Expense Categories
    @Query("SELECT * FROM expense_categories WHERE isDeleted = 0 ORDER BY name ASC")
    fun getAllExpenseCategories(): Flow<List<ExpenseCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseCategory(category: ExpenseCategory)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExpenseCategories(categories: List<ExpenseCategory>)

    @Update
    suspend fun updateExpenseCategory(category: ExpenseCategory)

    @Query("UPDATE expense_categories SET isDisabled = :disabled WHERE id = :id")
    suspend fun setExpenseCategoryDisabled(id: String, disabled: Boolean)
}
