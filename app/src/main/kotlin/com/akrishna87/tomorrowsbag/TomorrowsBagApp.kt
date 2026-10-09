package com.akrishna87.tomorrowsbag

import android.app.Application
import com.akrishna87.tomorrowsbag.data.db.AppDatabase
import com.akrishna87.tomorrowsbag.data.prefs.ProfilePrefs
import com.akrishna87.tomorrowsbag.data.repository.TomorrowsBagRepository

class TomorrowsBagApp : Application() {

    lateinit var repository: TomorrowsBagRepository
        private set
    lateinit var profilePrefs: ProfilePrefs
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = TomorrowsBagRepository(
            database.subjectDao(),
            database.weeklyPlanDao(),
            database.dailyPlanDao(),
            database.packedSubjectDao(),
            database.schoolHolidayDao()
        )
        profilePrefs = ProfilePrefs(this)
    }
}
