package com.akrishna87.tomorrowsbag.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akrishna87.tomorrowsbag.data.db.DailyPlanEntity
import com.akrishna87.tomorrowsbag.data.db.PackedSubjectEntity
import com.akrishna87.tomorrowsbag.data.db.SchoolHolidayEntity
import com.akrishna87.tomorrowsbag.data.db.SubjectEntity
import com.akrishna87.tomorrowsbag.data.db.WeeklyPlanEntity
import com.akrishna87.tomorrowsbag.data.prefs.ProfilePrefs
import com.akrishna87.tomorrowsbag.data.repository.TomorrowsBagRepository
import com.akrishna87.tomorrowsbag.util.Dates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryEntry(
    val dateKey: String,
    val subjects: List<SubjectEntity>,
    val note: String
)

data class ProfileState(
    val kidName: String = "",
    val kidClass: String = "",
    val parentName: String = "",
    val schoolLocation: String = ""
)

data class UiState(
    val subjects: List<SubjectEntity> = emptyList(),
    val weeklyPlan: Map<Int, List<String>> = emptyMap(),
    val schoolHolidays: List<SchoolHolidayEntity> = emptyList(),
    val tomorrowPackedSubjectIds: List<String>? = null,
    val tomorrowNote: String = "",
    val history: List<HistoryEntry> = emptyList()
)

class TomorrowsBagViewModel(
    private val repository: TomorrowsBagRepository,
    private val profilePrefs: ProfilePrefs
) : ViewModel() {

    private val todayKey = Dates.key(Dates.today())
    private val tomorrowKey = Dates.key(Dates.tomorrow())
    val tomorrowDayOfWeek = Dates.calendarDayOfWeek(Dates.tomorrow())

    private data class TomorrowData(
        val subjects: List<SubjectEntity>,
        val weeklyPlanRows: List<WeeklyPlanEntity>,
        val holidays: List<SchoolHolidayEntity>,
        val tomorrowPlan: DailyPlanEntity?,
        val tomorrowPacked: List<PackedSubjectEntity>
    )

    private data class HistoryData(
        val allPlans: List<DailyPlanEntity>,
        val allPacked: List<PackedSubjectEntity>
    )

    private val tomorrowDataFlow = combine(
        repository.observeSubjects(),
        repository.observeWeeklyPlan(),
        repository.observeSchoolHolidays(),
        repository.observeDailyPlan(tomorrowKey),
        repository.observePackedSubjects(tomorrowKey)
    ) { subjects, weeklyPlanRows, holidays, tomorrowPlan, tomorrowPacked ->
        TomorrowData(subjects, weeklyPlanRows, holidays, tomorrowPlan, tomorrowPacked)
    }

    private val historyDataFlow = combine(
        repository.observeAllDailyPlansDesc(),
        repository.observeAllPackedSubjects()
    ) { allPlans, allPacked -> HistoryData(allPlans, allPacked) }

    val uiState: StateFlow<UiState> = combine(tomorrowDataFlow, historyDataFlow) { tomorrow, historyData ->
        val subjectsById = tomorrow.subjects.associateBy { it.id }
        val weeklyPlan = tomorrow.weeklyPlanRows.groupBy({ it.dayOfWeek }, { it.subjectId })
        val packedByDate = historyData.allPacked.groupBy({ it.dateKey }, { it.subjectId })
        val history = historyData.allPlans
            .filter { it.dateKey != tomorrowKey && it.dateKey <= todayKey }
            .map { plan ->
                val ids = packedByDate[plan.dateKey].orEmpty()
                HistoryEntry(plan.dateKey, ids.mapNotNull { subjectsById[it] }, plan.note)
            }

        UiState(
            subjects = tomorrow.subjects,
            weeklyPlan = weeklyPlan,
            schoolHolidays = tomorrow.holidays,
            tomorrowPackedSubjectIds = if (tomorrow.tomorrowPlan == null) null else tomorrow.tomorrowPacked.map { it.subjectId },
            tomorrowNote = tomorrow.tomorrowPlan?.note.orEmpty(),
            history = history
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState())

    private val _profile = MutableStateFlow(
        ProfileState(
            kidName = profilePrefs.kidName,
            kidClass = profilePrefs.kidClass,
            parentName = profilePrefs.parentName,
            schoolLocation = profilePrefs.schoolLocation
        )
    )
    val profile: StateFlow<ProfileState> = _profile.asStateFlow()

    // Draft selection for tomorrow, held only in memory until "All Packed!" is
    // tapped. Null means "not yet initialized from the saved/suggested state."
    var draftSelection by mutableStateOf<Set<String>?>(null)
        private set
    var draftNote by mutableStateOf<String?>(null)
        private set
    var isEditingSavedPlan by mutableStateOf(false)
        private set

    fun ensureDraftInitialized(state: UiState) {
        if (draftSelection == null) {
            draftSelection = state.tomorrowPackedSubjectIds?.toSet()
                ?: state.weeklyPlan[tomorrowDayOfWeek]?.toSet()
                ?: emptySet()
        }
        if (draftNote == null) {
            draftNote = state.tomorrowNote
        }
    }

    fun toggleDraftSubject(subjectId: String) {
        val current = draftSelection ?: emptySet()
        draftSelection = if (subjectId in current) current - subjectId else current + subjectId
    }

    fun updateDraftNote(note: String) {
        draftNote = note
    }

    fun saveTomorrowPlan() {
        val selection = draftSelection ?: emptySet()
        val note = draftNote.orEmpty()
        viewModelScope.launch {
            repository.saveDailyPlan(tomorrowKey, selection.toList(), note)
        }
        isEditingSavedPlan = false
    }

    fun startEditingSavedPlan(state: UiState) {
        draftSelection = state.tomorrowPackedSubjectIds?.toSet() ?: emptySet()
        draftNote = state.tomorrowNote
        isEditingSavedPlan = true
    }

    fun updateProfile(
        kidName: String = _profile.value.kidName,
        kidClass: String = _profile.value.kidClass,
        parentName: String = _profile.value.parentName,
        schoolLocation: String = _profile.value.schoolLocation
    ) {
        profilePrefs.kidName = kidName
        profilePrefs.kidClass = kidClass
        profilePrefs.parentName = parentName
        profilePrefs.schoolLocation = schoolLocation
        _profile.value = ProfileState(kidName, kidClass, parentName, schoolLocation)
    }

    fun addSubject(name: String, emoji: String) {
        val nextOrder = uiState.value.subjects.size
        viewModelScope.launch { repository.addSubject(name, emoji, nextOrder) }
    }

    fun renameSubject(subject: SubjectEntity, name: String, emoji: String) {
        viewModelScope.launch { repository.renameSubject(subject, name, emoji) }
    }

    fun removeSubject(subject: SubjectEntity) {
        viewModelScope.launch { repository.removeSubject(subject) }
    }

    fun toggleWeeklyPlanSubject(dayOfWeek: Int, subjectId: String, currentlyOn: Boolean) {
        viewModelScope.launch { repository.toggleWeeklyPlanSubject(dayOfWeek, subjectId, currentlyOn) }
    }

    fun addSchoolHoliday(date: String, name: String) {
        viewModelScope.launch { repository.addSchoolHoliday(date, name) }
    }

    fun removeSchoolHoliday(holiday: SchoolHolidayEntity) {
        viewModelScope.launch { repository.removeSchoolHoliday(holiday) }
    }
}
