package com.akrishna87.tomorrowsbag.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

object Dates {
    private val keyFormatter = DateTimeFormatter.ISO_LOCAL_DATE // yyyy-MM-dd
    private val longFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.getDefault())
    private val shortFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())

    fun today(): LocalDate = LocalDate.now()
    fun tomorrow(): LocalDate = LocalDate.now().plusDays(1)

    fun key(date: LocalDate): String = date.format(keyFormatter)
    fun parseKey(key: String): LocalDate = LocalDate.parse(key, keyFormatter)

    fun formatLong(date: LocalDate): String = date.format(longFormatter)
    fun formatShort(date: LocalDate): String {
        val dayAbbrev = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        return "${date.format(shortFormatter)} ($dayAbbrev)"
    }

    /** Calendar.DAY_OF_WEEK convention (1=Sunday..7=Saturday), to key the weekly plan. */
    fun calendarDayOfWeek(date: LocalDate): Int {
        return when (date.dayOfWeek) {
            DayOfWeek.SUNDAY -> Calendar.SUNDAY
            DayOfWeek.MONDAY -> Calendar.MONDAY
            DayOfWeek.TUESDAY -> Calendar.TUESDAY
            DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
            DayOfWeek.THURSDAY -> Calendar.THURSDAY
            DayOfWeek.FRIDAY -> Calendar.FRIDAY
            DayOfWeek.SATURDAY -> Calendar.SATURDAY
        }
    }

    val weekdayShortNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    /** Index 0..6 matching [weekdayShortNames], for the given Calendar.DAY_OF_WEEK value. */
    fun weekdayIndex(calendarDayOfWeek: Int): Int = calendarDayOfWeek - 1
}
