package com.akrishna87.tomorrowsbag.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PackedSubjectDao {
    @Query("SELECT * FROM packed_subjects WHERE dateKey = :dateKey")
    fun observeForDate(dateKey: String): Flow<List<PackedSubjectEntity>>

    @Query("SELECT * FROM packed_subjects")
    fun observeAll(): Flow<List<PackedSubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<PackedSubjectEntity>)

    @Query("DELETE FROM packed_subjects WHERE dateKey = :dateKey")
    suspend fun deleteForDate(dateKey: String)

    @Query("DELETE FROM packed_subjects WHERE subjectId = :subjectId")
    suspend fun deleteForSubject(subjectId: String)

    @Transaction
    suspend fun replaceForDate(dateKey: String, subjectIds: List<String>) {
        deleteForDate(dateKey)
        insertAll(subjectIds.map { PackedSubjectEntity(dateKey, it) })
    }
}
