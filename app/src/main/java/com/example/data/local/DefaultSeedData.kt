package com.example.data.local

import com.example.data.model.ExpenseCategory
import com.example.data.model.GatheringType
import com.example.data.model.IncomeCategory

object DefaultSeedData {
    val defaultGatheringTypes = listOf(
        GatheringType("gt_sunday", "Sunday Service"),
        GatheringType("gt_midweek", "Midweek Service"),
        GatheringType("gt_prayer", "Prayer Meeting"),
        GatheringType("gt_biblestudy", "Bible Study"),
        GatheringType("gt_youth", "Youth Service"),
        GatheringType("gt_women", "Women's Meeting"),
        GatheringType("gt_men", "Men's Meeting"),
        GatheringType("gt_conference", "Conference"),
        GatheringType("gt_thanksgiving", "Thanksgiving Service"),
        GatheringType("gt_fundraising", "Fundraising Event"),
        GatheringType("gt_overnight", "Overnight Prayer"),
        GatheringType("gt_other", "Other")
    )

    val defaultIncomeCategories = listOf(
        IncomeCategory("inc_tithe", "Tithe", isDefault = true),
        IncomeCategory("inc_offertory", "General Offertory", isDefault = true),
        IncomeCategory("inc_thanksgiving", "Thanksgiving", isDefault = true),
        IncomeCategory("inc_building", "Building Fund", isDefault = true),
        IncomeCategory("inc_missions", "Missions", isDefault = true),
        IncomeCategory("inc_welfare", "Welfare Contribution", isDefault = true),
        IncomeCategory("inc_fundraising", "Fundraising", isDefault = true),
        IncomeCategory("inc_donation", "Donation", isDefault = true),
        IncomeCategory("inc_pledge", "Pledge Payment", isDefault = true),
        IncomeCategory("inc_department", "Department Contribution", isDefault = true),
        IncomeCategory("inc_other", "Other Income", isDefault = true)
    )

    val defaultExpenseCategories = listOf(
        ExpenseCategory("exp_rent", "Rent", isDefault = true),
        ExpenseCategory("exp_electricity", "Electricity", isDefault = true),
        ExpenseCategory("exp_water", "Water", isDefault = true),
        ExpenseCategory("exp_internet", "Internet and Communication", isDefault = true),
        ExpenseCategory("exp_transport", "Transport", isDefault = true),
        ExpenseCategory("exp_allowance", "Staff Allowances", isDefault = true),
        ExpenseCategory("exp_evangelism", "Evangelism", isDefault = true),
        ExpenseCategory("exp_welfare", "Welfare", isDefault = true),
        ExpenseCategory("exp_missions", "Missions", isDefault = true),
        ExpenseCategory("exp_repairs", "Repairs and Maintenance", isDefault = true),
        ExpenseCategory("exp_equipment", "Church Equipment", isDefault = true),
        ExpenseCategory("exp_construction", "Construction", isDefault = true),
        ExpenseCategory("exp_media", "Media", isDefault = true),
        ExpenseCategory("exp_hospitality", "Hospitality", isDefault = true),
        ExpenseCategory("exp_supplies", "Office Supplies", isDefault = true),
        ExpenseCategory("exp_events", "Events", isDefault = true),
        ExpenseCategory("exp_bank_charges", "Bank Charges", isDefault = true),
        ExpenseCategory("exp_momo_charges", "Mobile Money Charges", isDefault = true),
        ExpenseCategory("exp_other", "Other Expense", isDefault = true)
    )

    val paymentMethods = listOf(
        "Cash",
        "Mobile Money",
        "Bank Transfer",
        "Cheque",
        "Other"
    )
}
