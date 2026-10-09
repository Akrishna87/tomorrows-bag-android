package com.akrishna87.tomorrowsbag.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolHolidayDao {
    @Query("SELECT * FROM school_holidays ORDER BY date ASC")
    fun observeAll(): Flow<List<SchoolHolidayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(holiday: SchoolHolidayEntity)

    @Delete
    suspend fun delete(holiday: SchoolHolidayEntity)
}
