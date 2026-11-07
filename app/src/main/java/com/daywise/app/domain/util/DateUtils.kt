package com.daywise.app.domain.util

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Utility functions for Date calculations, ranges, and user-friendly labels.
 */
object DateUtils {

    private val zoneId: ZoneId = ZoneId.systemDefault()

    private val displayDateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.US)
    private val shortDateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.US)
    private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)
    private val yearMonthKeyFormatter = DateTimeFormatter.ofPattern("yyyy-MM", Locale.US)
    private val csvDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.US)

    fun todayMillis(): Long {
        return LocalDate.now().atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    fun toLocalDate(epochMillis: Long): LocalDate {
        return Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
    }

    fun toEpochMillis(date: LocalDate): Long {
        return date.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    fun getStartOfDayMillis(epochMillis: Long): Long {
        val date = toLocalDate(epochMillis)
        return date.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    fun getEndOfDayMillis(epochMillis: Long): Long {
        val date = toLocalDate(epochMillis)
        return date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
    }

    fun getStartOfMonthMillis(yearMonth: YearMonth): Long {
        return yearMonth.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    fun getEndOfMonthMillis(yearMonth: YearMonth): Long {
        return yearMonth.atEndOfMonth().plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
    }

    fun formatYearMonthKey(yearMonth: YearMonth): String {
        return yearMonth.format(yearMonthKeyFormatter)
    }

    fun parseYearMonthKey(key: String): YearMonth {
        return YearMonth.parse(key, yearMonthKeyFormatter)
    }

    fun formatMonthYear(yearMonth: YearMonth): String {
        return yearMonth.format(monthYearFormatter)
    }

    fun formatDisplayDate(epochMillis: Long): String {
        val date = toLocalDate(epochMillis)
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        return when (date) {
            today -> "Today (${date.format(shortDateFormatter)})"
            yesterday -> "Yesterday (${date.format(shortDateFormatter)})"
            else -> date.format(displayDateFormatter)
        }
    }

    fun formatExactDate(epochMillis: Long): String {
        return toLocalDate(epochMillis).format(displayDateFormatter)
    }

    fun formatCsvDate(epochMillis: Long): String {
        return toLocalDate(epochMillis).format(csvDateFormatter)
    }

    fun formatShortDate(epochMillis: Long): String {
        return toLocalDate(epochMillis).format(shortDateFormatter)
    }

    /**
     * Calculates days elapsed in a given month.
     * If the month is the current ongoing month, returns the day of month today.
     * If in the past, returns total days in that month.
     * If in the future, returns 1 to prevent division by zero.
     */
    fun getElapsedDaysInMonth(yearMonth: YearMonth): Int {
        val currentYearMonth = YearMonth.now()
        return when {
            yearMonth == currentYearMonth -> LocalDate.now().dayOfMonth.coerceAtLeast(1)
            yearMonth.isBefore(currentYearMonth) -> yearMonth.lengthOfMonth()
            else -> 1
        }
    }
}
