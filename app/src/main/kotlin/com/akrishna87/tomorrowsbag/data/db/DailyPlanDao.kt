package com.akrishna87.tomorrowsbag.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyPlanDao {
    @Query("SELECT * FROM daily_plans WHERE dateKey = :dateKey")
    fun observeForDate(dateKey: String): Flow<DailyPlanEntity?>

    @Query("SELECT * FROM daily_plans ORDER BY dateKey DESC")
    fun observeAllDesc(): Flow<List<DailyPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(plan: DailyPlanEntity)
}
