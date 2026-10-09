package com.akrishna87.tomorrowsbag.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [
        SubjectEntity::class,
        WeeklyPlanEntity::class,
        DailyPlanEntity::class,
        PackedSubjectEntity::class,
        SchoolHolidayEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun weeklyPlanDao(): WeeklyPlanDao
    abstract fun dailyPlanDao(): DailyPlanDao
    abstract fun packedSubjectDao(): PackedSubjectDao
    abstract fun schoolHolidayDao(): SchoolHolidayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: build(context).also { INSTANCE = it }
            }
        }

        private fun build(context: Context): AppDatabase {
            val seedScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "tomorrows-bag.db"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // onCreate only fires on first real table access, which happens
                        // after getInstance() has already assigned INSTANCE below, so
                        // this coroutine (scheduled to run later, not inline) always
                        // sees a non-null INSTANCE by the time it actually executes.
                        seedScope.launch {
                            INSTANCE?.subjectDao()?.insertAll(DefaultSubjects.seed)
                        }
                    }
                })
                .build()
        }
    }
}
