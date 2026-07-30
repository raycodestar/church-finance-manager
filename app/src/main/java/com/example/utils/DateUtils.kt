package com.example.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class PeriodFilter(val label: String) {
    THIS_MONTH("This Month"),
    CUSTOM("Custom Date Range"),
    LAST_MONTH("Last Month"),
    THIS_QUARTER("This Quarter"),
    THIS_YEAR("This Year"),
    THIS_WEEK("This Week"),
    TODAY("Today")
}

data class DateRange(val startMillis: Long, val endMillis: Long)

object DateUtils {
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun formatDate(millis: Long): String {
        return dateFormat.format(Date(millis))
    }

    fun formatDateTime(millis: Long): String {
        return dateTimeFormat.format(Date(millis))
    }

    fun formatMonthYear(millis: Long): String {
        return monthYearFormat.format(Date(millis))
    }

    fun formatIso(millis: Long): String {
        return isoFormat.format(Date(millis))
    }

    fun getDateRange(filter: PeriodFilter, customStart: Long? = null, customEnd: Long? = null): DateRange {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        return when (filter) {
            PeriodFilter.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                DateRange(start, calendar.timeInMillis)
            }
            PeriodFilter.THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                DateRange(start, now)
            }
            PeriodFilter.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                DateRange(start, now)
            }
            PeriodFilter.LAST_MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                DateRange(start, calendar.timeInMillis)
            }
            PeriodFilter.THIS_QUARTER -> {
                val currentMonth = calendar.get(Calendar.MONTH)
                val quarterStartMonth = (currentMonth / 3) * 3
                calendar.set(Calendar.MONTH, quarterStartMonth)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                DateRange(start, now)
            }
            PeriodFilter.THIS_YEAR -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val start = calendar.timeInMillis
                DateRange(start, now)
            }
            PeriodFilter.CUSTOM -> {
                val start = if (customStart != null) {
                    val cal = Calendar.getInstance().apply { timeInMillis = customStart }
                    cal.set(Calendar.HOUR_OF_DAY, 0)
                    cal.set(Calendar.MINUTE, 0)
                    cal.set(Calendar.SECOND, 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    cal.timeInMillis
                } else 0L

                val end = if (customEnd != null) {
                    val cal = Calendar.getInstance().apply { timeInMillis = customEnd }
                    cal.set(Calendar.HOUR_OF_DAY, 23)
                    cal.set(Calendar.MINUTE, 59)
                    cal.set(Calendar.SECOND, 59)
                    cal.set(Calendar.MILLISECOND, 999)
                    cal.timeInMillis
                } else now

                DateRange(start, end)
            }
        }
    }
}
