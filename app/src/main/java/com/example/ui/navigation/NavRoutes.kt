package com.example.ui.navigation

object NavRoutes {
    const val AUTH = "auth"
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
    const val GATHERINGS = "gatherings"
    const val GATHERING_DETAIL = "gathering_detail/{gatheringId}"
    const val TRANSACTIONS = "transactions"
    const val RECORD_INCOME = "record_income?gatheringId={gatheringId}&gatheringName={gatheringName}"
    const val RECORD_EXPENSE = "record_expense?gatheringId={gatheringId}&gatheringName={gatheringName}"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
    const val CHURCH_PROFILE = "church_profile"
    const val CATEGORIES = "categories"
    const val ACTIVITY_HISTORY = "activity_history"
    const val RECENTLY_DELETED = "recently_deleted"
    const val SUPABASE_SQL = "supabase_sql"

    fun gatheringDetailRoute(gatheringId: String) = "gathering_detail/$gatheringId"
    fun recordIncomeRoute(gatheringId: String? = null, gatheringName: String? = null) =
        "record_income?gatheringId=${gatheringId ?: ""}&gatheringName=${gatheringName ?: ""}"
    fun recordExpenseRoute(gatheringId: String? = null, gatheringName: String? = null) =
        "record_expense?gatheringId=${gatheringId ?: ""}&gatheringName=${gatheringName ?: ""}"
}
