package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AdminProfile
import com.example.data.model.ChurchProfile
import com.example.data.model.ExpenseCategory
import com.example.data.model.ExpenseTransaction
import com.example.data.model.Gathering
import com.example.data.model.GatheringType
import com.example.data.model.IncomeCategory
import com.example.data.model.IncomeTransaction
import com.example.repository.ChurchRepository
import com.example.utils.DateRange
import com.example.utils.DateUtils
import com.example.utils.PeriodFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardSummary(
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val currentBalance: Long = 0L,
    val totalTithe: Long = 0L,
    val totalOffertory: Long = 0L,
    val currencyCode: String = "UGX"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChurchRepository(AppDatabase.getInstance(application))

    val churchProfile: StateFlow<ChurchProfile?> = repository.churchProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val adminProfile: StateFlow<AdminProfile?> = repository.adminProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val allGatherings: StateFlow<List<Gathering>> = repository.allGatherings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val deletedGatherings: StateFlow<List<Gathering>> = repository.deletedGatherings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val gatheringTypes: StateFlow<List<GatheringType>> = repository.gatheringTypes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allIncome: StateFlow<List<IncomeTransaction>> = repository.allIncome.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val deletedIncome: StateFlow<List<IncomeTransaction>> = repository.deletedIncome.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allExpense: StateFlow<List<ExpenseTransaction>> = repository.allExpense.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val deletedExpense: StateFlow<List<ExpenseTransaction>> = repository.deletedExpense.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val incomeCategories: StateFlow<List<IncomeCategory>> = repository.incomeCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val expenseCategories: StateFlow<List<ExpenseCategory>> = repository.expenseCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val activityLogs = repository.activityLogs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Period Filter State
    private val _selectedPeriod = MutableStateFlow(PeriodFilter.THIS_MONTH)
    val selectedPeriod: StateFlow<PeriodFilter> = _selectedPeriod.asStateFlow()

    private val _customDateRange = MutableStateFlow<DateRange?>(null)
    val customDateRange: StateFlow<DateRange?> = _customDateRange.asStateFlow()

    // Filtered Summary State
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        allIncome,
        allExpense,
        churchProfile,
        selectedPeriod,
        customDateRange
    ) { incomeList, expenseList, profile, period, customRange ->
        val range = DateUtils.getDateRange(period, customRange?.startMillis, customRange?.endMillis)

        val filteredIncome = incomeList.filter { it.dateMillis in range.startMillis..range.endMillis }
        val filteredExpense = expenseList.filter { it.dateMillis in range.startMillis..range.endMillis }

        val incTotal = filteredIncome.sumOf { it.amount }
        val expTotal = filteredExpense.sumOf { it.amount }
        val balance = incTotal - expTotal

        val titheTotal = filteredIncome.filter { it.categoryName.equals("Tithe", ignoreCase = true) }.sumOf { it.amount }
        val offertoryTotal = filteredIncome.filter { it.categoryName.contains("Offertory", ignoreCase = true) }.sumOf { it.amount }

        DashboardSummary(
            totalIncome = incTotal,
            totalExpense = expTotal,
            currentBalance = balance,
            totalTithe = titheTotal,
            totalOffertory = offertoryTotal,
            currencyCode = profile?.defaultCurrency ?: "UGX"
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardSummary()
    )

    fun setPeriodFilter(period: PeriodFilter, customStart: Long? = null, customEnd: Long? = null) {
        _selectedPeriod.value = period
        if (period == PeriodFilter.CUSTOM && customStart != null && customEnd != null) {
            _customDateRange.value = DateRange(customStart, customEnd)
        }
    }

    // Actions
    fun initializeChurch(
        adminEmail: String,
        adminName: String,
        passwordHash: String,
        churchName: String,
        location: String,
        phone: String,
        email: String,
        currency: String
    ) {
        viewModelScope.launch {
            repository.initializeChurchAndAdmin(
                adminEmail = adminEmail,
                adminFullName = adminName,
                passwordHash = passwordHash,
                churchName = churchName,
                location = location,
                contactPhone = phone,
                contactEmail = email,
                defaultCurrency = currency
            )
        }
    }

    fun updateChurchProfile(churchProfile: ChurchProfile) {
        viewModelScope.launch {
            repository.updateChurchProfile(churchProfile)
        }
    }

    fun createGathering(
        name: String,
        typeId: String,
        typeName: String,
        dateMillis: Long,
        startTime: String?,
        description: String?
    ) {
        viewModelScope.launch {
            repository.createGathering(name, typeId, typeName, dateMillis, startTime, description)
        }
    }

    fun updateGathering(gathering: Gathering) {
        viewModelScope.launch {
            repository.updateGathering(gathering)
        }
    }

    fun softDeleteGathering(id: String, name: String) {
        viewModelScope.launch {
            repository.softDeleteGathering(id, name)
        }
    }

    fun restoreGathering(id: String, name: String) {
        viewModelScope.launch {
            repository.restoreGathering(id, name)
        }
    }

    fun recordIncome(
        gatheringId: String?,
        gatheringName: String?,
        categoryId: String,
        categoryName: String,
        amount: Long,
        paymentMethod: String,
        dateMillis: Long,
        description: String?,
        referenceNumber: String?,
        attachmentUri: String?
    ) {
        viewModelScope.launch {
            repository.recordIncome(
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
        }
    }

    fun updateIncome(income: IncomeTransaction) {
        viewModelScope.launch {
            repository.updateIncome(income)
        }
    }

    fun softDeleteIncome(id: String, categoryName: String) {
        viewModelScope.launch {
            repository.softDeleteIncome(id, categoryName)
        }
    }

    fun restoreIncome(id: String) {
        viewModelScope.launch {
            repository.restoreIncome(id)
        }
    }

    fun recordExpense(
        title: String,
        categoryId: String,
        categoryName: String,
        amount: Long,
        dateMillis: Long,
        paymentMethod: String,
        payee: String?,
        description: String?,
        attachmentUri: String?,
        referenceNumber: String?,
        gatheringId: String?,
        gatheringName: String?
    ) {
        viewModelScope.launch {
            repository.recordExpense(
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
        }
    }

    fun updateExpense(expense: ExpenseTransaction) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun softDeleteExpense(id: String, title: String) {
        viewModelScope.launch {
            repository.softDeleteExpense(id, title)
        }
    }

    fun restoreExpense(id: String) {
        viewModelScope.launch {
            repository.restoreExpense(id)
        }
    }

    fun addIncomeCategory(name: String) {
        viewModelScope.launch {
            repository.createIncomeCategory(name)
        }
    }

    fun addExpenseCategory(name: String) {
        viewModelScope.launch {
            repository.createExpenseCategory(name)
        }
    }
}
