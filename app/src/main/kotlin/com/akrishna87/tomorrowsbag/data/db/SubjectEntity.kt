package com.akrishna87.tomorrowsbag.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val emoji: String,
    val sortOrder: Int
)
