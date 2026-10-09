package com.akrishna87.tomorrowsbag.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** The note + save timestamp for one date; which subjects were packed lives in [PackedSubjectEntity]. */
@Entity(tableName = "daily_plans")
data class DailyPlanEntity(
    @PrimaryKey val dateKey: String,
    val note: String,
    val savedAt: Long
)
