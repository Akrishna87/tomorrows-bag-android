package com.akrishna87.tomorrowsbag.data.db

import androidx.room.Entity

/** One row = "this subject was packed on this date." */
@Entity(tableName = "packed_subjects", primaryKeys = ["dateKey", "subjectId"])
data class PackedSubjectEntity(
    val dateKey: String,
    val subjectId: String
)
