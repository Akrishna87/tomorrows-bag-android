package com.akrishna87.tomorrowsbag.data.db

import androidx.room.Entity

/** One row = "this subject is usually needed on this day of week" (Calendar.DAY_OF_WEEK, 1=Sunday..7=Saturday). */
@Entity(tableName = "weekly_plan", primaryKeys = ["dayOfWeek", "subjectId"])
data class WeeklyPlanEntity(
    val dayOfWeek: Int,
    val subjectId: String
)
