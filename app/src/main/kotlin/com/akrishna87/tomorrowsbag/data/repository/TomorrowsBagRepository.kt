package com.akrishna87.tomorrowsbag.data.repository

import com.akrishna87.tomorrowsbag.data.db.DailyPlanDao
import com.akrishna87.tomorrowsbag.data.db.DailyPlanEntity
import com.akrishna87.tomorrowsbag.data.db.PackedSubjectDao
import com.akrishna87.tomorrowsbag.data.db.SchoolHolidayDao
import com.akrishna87.tomorrowsbag.data.db.SchoolHolidayEntity
import com.akrishna87.tomorrowsbag.data.db.SubjectDao
import com.akrishna87.tomorrowsbag.data.db.SubjectEntity
import com.akrishna87.tomorrowsbag.data.db.WeeklyPlanDao
import com.akrishna87.tomorrowsbag.data.db.WeeklyPlanEntity
import java.util.UUID
import kotlinx.coroutines.flow.Flow

class TomorrowsBagRepository(
    private val subjectDao: SubjectDao,
    private val weeklyPlanDao: WeeklyPlanDao,
    private val dailyPlanDao: DailyPlanDao,
    private val packedSubjectDao: PackedSubjectDao,
    private val schoolHolidayDao: SchoolHolidayDao
) {
    fun observeSubjects(): Flow<List<SubjectEntity>> = subjectDao.observeAll()

    fun observeWeeklyPlan(): Flow<List<WeeklyPlanEntity>> = weeklyPlanDao.observeAll()

    suspend fun subjectIdsForDay(dayOfWeek: Int): List<String> = weeklyPlanDao.subjectIdsForDay(dayOfWeek)

    fun observeDailyPlan(dateKey: String): Flow<DailyPlanEntity?> = dailyPlanDao.observeForDate(dateKey)

    fun observeAllDailyPlansDesc(): Flow<List<DailyPlanEntity>> = dailyPlanDao.observeAllDesc()

    fun observePackedSubjects(dateKey: String) = packedSubjectDao.observeForDate(dateKey)

    fun observeAllPackedSubjects() = packedSubjectDao.observeAll()

    fun observeSchoolHolidays(): Flow<List<SchoolHolidayEntity>> = schoolHolidayDao.observeAll()

    suspend fun saveDailyPlan(dateKey: String, subjectIds: List<String>, note: String) {
        dailyPlanDao.upsert(DailyPlanEntity(dateKey, note, System.currentTimeMillis()))
        packedSubjectDao.replaceForDate(dateKey, subjectIds)
    }

    suspend fun addSubject(name: String, emoji: String, sortOrder: Int) {
        subjectDao.upsert(SubjectEntity(id = UUID.randomUUID().toString(), name = name, emoji = emoji, sortOrder = sortOrder))
    }

    suspend fun renameSubject(subject: SubjectEntity, name: String, emoji: String) {
        subjectDao.update(subject.copy(name = name, emoji = emoji))
    }

    suspend fun removeSubject(subject: SubjectEntity) {
        subjectDao.delete(subject)
        weeklyPlanDao.deleteForSubject(subject.id)
        packedSubjectDao.deleteForSubject(subject.id)
    }

    suspend fun toggleWeeklyPlanSubject(dayOfWeek: Int, subjectId: String, currentlyOn: Boolean) {
        if (currentlyOn) {
            weeklyPlanDao.delete(WeeklyPlanEntity(dayOfWeek, subjectId))
        } else {
            weeklyPlanDao.insert(WeeklyPlanEntity(dayOfWeek, subjectId))
        }
    }

    suspend fun addSchoolHoliday(date: String, name: String) {
        schoolHolidayDao.insert(SchoolHolidayEntity(id = UUID.randomUUID().toString(), date = date, name = name))
    }

    suspend fun removeSchoolHoliday(holiday: SchoolHolidayEntity) {
        schoolHolidayDao.delete(holiday)
    }
}
