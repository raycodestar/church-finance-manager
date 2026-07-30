package com.example.repository

import com.example.data.local.AppDatabase
import com.example.data.local.DefaultSeedData
import com.example.data.model.ActivityLog
import com.example.data.model.AdminProfile
import com.example.data.model.ChurchProfile
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseTransaction
import com.example.data.model.Gathering
import com.example.data.model.GatheringType
import com.example.data.model.IncomeCategory
import com.example.data.model.IncomeTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class ChurchRepository(private val db: AppDatabase) {
    private val churchDao = db.churchDao()
    private val gatheringDao = db.gatheringDao()
    private val transactionDao = db.transactionDao()
    private val categoryDao = db.categoryDao()
    private val activityDao = db.activityDao()

    // Profile & Setup
    val churchProfile: Flow<ChurchProfile?> = churchDao.getChurchProfile()
    val adminProfile: Flow<AdminProfile?> = churchDao.getAdminProfile()

    suspend fun getChurchProfileDirect(): ChurchProfile? = churchDao.getChurchProfileDirect()
    suspend fun getAdminProfileDirect(): AdminProfile? = churchDao.getAdminProfileDirect()

    suspend fun initializeChurchAndAdmin(
        adminEmail: String,
        adminFullName: String,
        passwordHash: String,
        churchName: String,
        location: String,
        contactPhone: String,
        contactEmail: String,
        defaultCurrency: String = "UGX",
        financialYearStartMonth: Int = 1,
        logoUri: String? = null
    ) {
        val adminId = UUID.randomUUID().toString()
        val admin = AdminProfile(
            id = adminId,
            email = adminEmail,
            fullName = adminFullName,
            passwordHash = passwordHash
        )
        churchDao.insertAdminProfile(admin)

        val churchId = UUID.randomUUID().toString()
        val church = ChurchProfile(
            id = churchId,
            name = churchName,
            logoUri = logoUri,
            location = location,
            contactPhone = contactPhone,
            contactEmail = contactEmail,
            defaultCurrency = defaultCurrency,
            financialYearStartMonth = financialYearStartMonth,
            adminFullName = adminFullName,
            isInitialized = true
        )
        churchDao.insertChurchProfile(church)

        // Seed default categories and types
        categoryDao.insertIncomeCategories(DefaultSeedData.defaultIncomeCategories)
        categoryDao.insertExpenseCategories(DefaultSeedData.defaultExpenseCategories)
        gatheringDao.insertGatheringTypes(DefaultSeedData.defaultGatheringTypes)

        logActivity("CHURCH_INITIALIZED", "ChurchProfile", churchId, "Church '$churchName' initialized by $adminFullName")
    }

    suspend fun updateChurchProfile(profile: ChurchProfile) {
        churchDao.updateChurchProfile(profile)
        logActivity("CHURCH_PROFILE_UPDATED", "ChurchProfile", profile.id, "Updated church profile details")
    }

    suspend fun updateAdminProfile(profile: AdminProfile) {
        churchDao.updateAdminProfile(profile)
        logActivity("ADMIN_PROFILE_UPDATED", "AdminProfile", profile.id, "Updated admin account profile")
    }

    // Gatherings
    val allGatherings: Flow<List<Gathering>> = gatheringDao.getAllGatherings()
    val deletedGatherings: Flow<List<Gathering>> = gatheringDao.getDeletedGatherings()
    val gatheringTypes: Flow<List<GatheringType>> = gatheringDao.getAllGatheringTypes()

    suspend fun getGatheringById(id: String) = gatheringDao.getGatheringById(id)

    suspend fun createGathering(
        name: String,
        typeId: String,
        typeName: String,
        dateMillis: Long,
        startTime: String? = null,
        description: String? = null
    ): String {
        val id = UUID.randomUUID().toString()
        val g = Gathering(
            id = id,
            name = name,
            gatheringTypeId = typeId,
            gatheringTypeName = typeName,
            dateMillis = dateMillis,
            startTime = startTime,
            description = description
        )
        gatheringDao.insertGathering(g)
        logActivity("GATHERING_CREATED", "Gathering", id, "Created gathering '$name' ($typeName)")
        return id
    }

    suspend fun updateGathering(gathering: Gathering) {
        gatheringDao.updateGathering(gathering)
        logActivity("GATHERING_EDITED", "Gathering", gathering.id, "Updated gathering '${gathering.name}'")
    }

    suspend fun softDeleteGathering(id: String, name: String) {
        gatheringDao.softDeleteGathering(id)
        logActivity("GATHERING_DELETED", "Gathering", id, "Soft deleted gathering '$name'")
    }

    suspend fun restoreGathering(id: String, name: String) {
        gatheringDao.restoreGathering(id)
        logActivity("GATHERING_RESTORED", "Gathering", id, "Restored gathering '$name'")
    }

    suspend fun addCustomGatheringType(name: String) {
        val id = "gt_custom_${UUID.randomUUID().toString().take(8)}"
        gatheringDao.insertGatheringType(GatheringType(id = id, name = name, isCustom = true))
    }

    // Income Transactions
    val allIncome: Flow<List<IncomeTransaction>> = transactionDao.getAllIncome()
    val deletedIncome: Flow<List<IncomeTransaction>> = transactionDao.getDeletedIncome()

    fun getIncomeForGathering(gatheringId: String) = transactionDao.getIncomeForGathering(gatheringId)

    suspend fun recordIncome(
        gatheringId: String? = null,
        gatheringName: String? = null,
        categoryId: String,
        categoryName: String,
        amount: Long,
        paymentMethod: String,
        dateMillis: Long,
        description: String? = null,
        referenceNumber: String? = null,
        attachmentUri: String? = null
    ): String {
        val id = UUID.randomUUID().toString()
        val inc = IncomeTransaction(
            id = id,
            gatheringId = gatheringId,
            gatheringName = gatheringName,
            categoryId = categoryId,
            categoryName = categoryName,
            amount = amount,
            paymentMethod = paymentMethod,
            dateMillis = dateMillis,
            description = description,
            referenceNumber = referenceNumber,
            attachmentUri = attachmentUri
        )
        transactionDao.insertIncome(inc)
        logActivity("INCOME_RECORDED", "IncomeTransaction", id, "Recorded $categoryName income: $amount ($paymentMethod)")
        return id
    }

    suspend fun updateIncome(income: IncomeTransaction) {
        transactionDao.updateIncome(income)
        logActivity("INCOME_EDITED", "IncomeTransaction", income.id, "Updated income record #${income.id.take(6)}")
    }

    suspend fun softDeleteIncome(id: String, categoryName: String) {
        transactionDao.softDeleteIncome(id)
        logActivity("INCOME_DELETED", "IncomeTransaction", id, "Soft deleted income record ($categoryName)")
    }

    suspend fun restoreIncome(id: String) {
        transactionDao.restoreIncome(id)
        logActivity("INCOME_RESTORED", "IncomeTransaction", id, "Restored income record")
    }

    // Expense Transactions
    val allExpense: Flow<List<ExpenseTransaction>> = transactionDao.getAllExpense()
    val deletedExpense: Flow<List<ExpenseTransaction>> = transactionDao.getDeletedExpense()

    fun getExpenseForGathering(gatheringId: String) = transactionDao.getExpenseForGathering(gatheringId)

    suspend fun recordExpense(
        title: String,
        categoryId: String,
        categoryName: String,
        amount: Long,
        dateMillis: Long,
        paymentMethod: String,
        payee: String? = null,
        description: String? = null,
        attachmentUri: String? = null,
        referenceNumber: String? = null,
        gatheringId: String? = null,
        gatheringName: String? = null
    ): String {
        val id = UUID.randomUUID().toString()
        val exp = ExpenseTransaction(
            id = id,
            title = title,
            categoryId = categoryId,
            categoryName = categoryName,
            amount = amount,
            dateMillis = dateMillis,
            paymentMethod = paymentMethod,
            payee = payee,
            description = description,
            attachmentUri = attachmentUri,
            referenceNumber = referenceNumber,
            gatheringId = gatheringId,
            gatheringName = gatheringName
        )
        transactionDao.insertExpense(exp)
        logActivity("EXPENSE_RECORDED", "ExpenseTransaction", id, "Recorded expense '$title': $amount ($paymentMethod)")
        return id
    }

    suspend fun updateExpense(expense: ExpenseTransaction) {
        transactionDao.updateExpense(expense)
        logActivity("EXPENSE_EDITED", "ExpenseTransaction", expense.id, "Updated expense '${expense.title}'")
    }

    suspend fun softDeleteExpense(id: String, title: String) {
        transactionDao.softDeleteExpense(id)
        logActivity("EXPENSE_DELETED", "ExpenseTransaction", id, "Soft deleted expense '$title'")
    }

    suspend fun restoreExpense(id: String) {
        transactionDao.restoreExpense(id)
        logActivity("EXPENSE_RESTORED", "ExpenseTransaction", id, "Restored expense record")
    }

    // Categories
    val incomeCategories: Flow<List<IncomeCategory>> = categoryDao.getAllIncomeCategories()
    val expenseCategories: Flow<List<ExpenseCategory>> = categoryDao.getAllExpenseCategories()

    suspend fun createIncomeCategory(name: String) {
        val id = "inc_cat_${UUID.randomUUID().toString().take(8)}"
        categoryDao.insertIncomeCategory(IncomeCategory(id = id, name = name))
        logActivity("CATEGORY_CREATED", "IncomeCategory", id, "Created income category '$name'")
    }

    suspend fun updateIncomeCategory(category: IncomeCategory) {
        categoryDao.updateIncomeCategory(category)
        logActivity("CATEGORY_EDITED", "IncomeCategory", category.id, "Renamed income category to '${category.name}'")
    }

    suspend fun setIncomeCategoryDisabled(id: String, disabled: Boolean) {
        categoryDao.setIncomeCategoryDisabled(id, disabled)
    }

    suspend fun createExpenseCategory(name: String) {
        val id = "exp_cat_${UUID.randomUUID().toString().take(8)}"
        categoryDao.insertExpenseCategory(ExpenseCategory(id = id, name = name))
        logActivity("CATEGORY_CREATED", "ExpenseCategory", id, "Created expense category '$name'")
    }

    suspend fun updateExpenseCategory(category: ExpenseCategory) {
        categoryDao.updateExpenseCategory(category)
        logActivity("CATEGORY_EDITED", "ExpenseCategory", category.id, "Renamed expense category to '${category.name}'")
    }

    suspend fun setExpenseCategoryDisabled(id: String, disabled: Boolean) {
        categoryDao.setExpenseCategoryDisabled(id, disabled)
    }

    // Activity History
    val activityLogs: Flow<List<ActivityLog>> = activityDao.getAllActivityLogs()

    suspend fun logActivity(actionType: String, recordType: String, recordId: String, description: String) {
        val log = ActivityLog(
            id = UUID.randomUUID().toString(),
            actionType = actionType,
            recordType = recordType,
            recordId = recordId,
            description = description
        )
        activityDao.insertActivityLog(log)
    }
}
