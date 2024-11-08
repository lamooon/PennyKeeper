package com.example.pennykeeper.data.dao

import androidx.room.*
import com.example.pennykeeper.data.model.Expense
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses")
    fun getAllExpenses(): Flow<List<Expense>>

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses WHERE id = :id")
    suspend fun getExpenseById(id: Int): Expense?

    @Query("SELECT * FROM expenses WHERE category = :category")
    fun getExpensesByCategory(category: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE isRecurring = 1")
    fun getRecurringExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE isRecurring = 1 AND nextDueDate <= :date")
    fun getDueRecurringExpenses(date: Date): Flow<List<Expense>>
}