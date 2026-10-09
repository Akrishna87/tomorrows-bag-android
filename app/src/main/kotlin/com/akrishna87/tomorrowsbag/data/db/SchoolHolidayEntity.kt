package com.akrishna87.tomorrowsbag.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "school_holidays")
data class SchoolHolidayEntity(
    @PrimaryKey val id: String,
    val date: String,
    val name: String
)
