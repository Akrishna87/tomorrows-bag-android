package com.akrishna87.tomorrowsbag.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyPlanDao {
    @Query("SELECT * FROM weekly_plan")
    fun observeAll(): Flow<List<WeeklyPlanEntity>>

    @Query("SELECT subjectId FROM weekly_plan WHERE dayOfWeek = :dayOfWeek")
    suspend fun subjectIdsForDay(dayOfWeek: Int): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WeeklyPlanEntity)

    @Delete
    suspend fun delete(entry: WeeklyPlanEntity)

    @Query("DELETE FROM weekly_plan WHERE subjectId = :subjectId")
    suspend fun deleteForSubject(subjectId: String)
}
