package com.example

import com.example.utils.DateUtils
import com.example.utils.PeriodFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    @Test
    fun customDateRange_calculatesStartAndEndOfDayCorrectly() {
        // Given 1 Jan 2026 and 5 May 2026
        val calStart = Calendar.getInstance().apply {
            set(2026, Calendar.JANUARY, 1, 14, 30, 0)
        }
        val calEnd = Calendar.getInstance().apply {
            set(2026, Calendar.MAY, 5, 10, 15, 0)
        }

        val range = DateUtils.getDateRange(
            filter = PeriodFilter.CUSTOM,
            customStart = calStart.timeInMillis,
            customEnd = calEnd.timeInMillis
        )

        // Check start is at 00:00:00.000
        val checkStart = Calendar.getInstance().apply { timeInMillis = range.startMillis }
        assertEquals(2026, checkStart.get(Calendar.YEAR))
        assertEquals(Calendar.JANUARY, checkStart.get(Calendar.MONTH))
        assertEquals(1, checkStart.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, checkStart.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, checkStart.get(Calendar.MINUTE))
        assertEquals(0, checkStart.get(Calendar.SECOND))

        // Check end is at 23:59:59.999
        val checkEnd = Calendar.getInstance().apply { timeInMillis = range.endMillis }
        assertEquals(2026, checkEnd.get(Calendar.YEAR))
        assertEquals(Calendar.MAY, checkEnd.get(Calendar.MONTH))
        assertEquals(5, checkEnd.get(Calendar.DAY_OF_MONTH))
        assertEquals(23, checkEnd.get(Calendar.HOUR_OF_DAY))
        assertEquals(59, checkEnd.get(Calendar.MINUTE))
        assertEquals(59, checkEnd.get(Calendar.SECOND))
    }

    @Test
    fun standardPeriodFilters_returnValidNonEmptyRanges() {
        PeriodFilter.entries.forEach { filter ->
            val range = DateUtils.getDateRange(filter)
            if (filter != PeriodFilter.CUSTOM) {
                assertTrue("Start millis should be <= end millis for $filter", range.startMillis <= range.endMillis)
            }
        }
    }
}
