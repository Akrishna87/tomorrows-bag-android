package com.akrishna87.tomorrowsbag.ui.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akrishna87.tomorrowsbag.data.db.SubjectEntity
import com.akrishna87.tomorrowsbag.ui.TomorrowsBagViewModel
import com.akrishna87.tomorrowsbag.ui.components.RemovableChipFlow
import com.akrishna87.tomorrowsbag.ui.components.SectionCard
import com.akrishna87.tomorrowsbag.util.Dates

@Composable
fun ParentScreen(viewModel: TomorrowsBagViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val profile by viewModel.profile.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Today: ${Dates.formatLong(Dates.today())}",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        NamesAndClassSection(profile, viewModel)
        TomorrowSummarySection(uiState)
        SchoolHolidaysSection(uiState, viewModel)
        WeeklyPlanSection(uiState, viewModel)
        ManageSubjectsSection(uiState, viewModel)
        HistorySection(uiState)
    }
}

@Composable
private fun NamesAndClassSection(
    profile: com.akrishna87.tomorrowsbag.ui.ProfileState,
    viewModel: TomorrowsBagViewModel
) {
    SectionCard(title = "👤 Names & class") {
        OutlinedTextField(
            value = profile.kidName,
            onValueChange = { viewModel.updateProfile(kidName = it) },
            label = { Text("Kid's name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = profile.kidClass,
            onValueChange = { viewModel.updateProfile(kidClass = it) },
            label = { Text("Kid's class / grade") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = profile.parentName,
            onValueChange = { viewModel.updateProfile(parentName = it) },
            label = { Text("Parent's name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = profile.schoolLocation,
            onValueChange = { viewModel.updateProfile(schoolLocation = it) },
            label = { Text("School location") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TomorrowSummarySection(uiState: com.akrishna87.tomorrowsbag.ui.UiState) {
    SectionCard(title = "🎒 Tomorrow — ${Dates.formatLong(Dates.tomorrow())}") {
        val packedIds = uiState.tomorrowPackedSubjectIds
        when {
            packedIds == null -> Text(
                "Not filled in yet — ask your kid to open Kid mode!",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            packedIds.isEmpty() -> Text(
                "Nothing selected yet — nudge your little one!",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            else -> {
                val names = uiState.subjects.filter { it.id in packedIds }.joinToString("   ") { "${it.emoji} ${it.name}" }
                Text(names)
                if (uiState.tomorrowNote.isNotBlank()) {
                    Text("📝 ${uiState.tomorrowNote}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun SchoolHolidaysSection(uiState: com.akrishna87.tomorrowsbag.ui.UiState, viewModel: TomorrowsBagViewModel) {
    SectionCard(title = "🏫 School holidays") {
        Text(
            "Add the exact dates school is closed — these take priority over any guessing.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        val todayKey = Dates.key(Dates.today())
        val upcoming = uiState.schoolHolidays.filter { it.date >= todayKey }.sortedBy { it.date }
        if (upcoming.isEmpty()) {
            Text("No upcoming dates added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            RemovableChipFlow(
                items = upcoming.map { holiday ->
                    "${holiday.date} — ${holiday.name}" to { viewModel.removeSchoolHoliday(holiday) }
                }
            )
        }

        var dateText by remember { mutableStateOf("") }
        var nameText by remember { mutableStateOf("") }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = { Text("YYYY-MM-DD") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = nameText,
                onValueChange = { nameText = it },
                label = { Text("Occasion") },
                modifier = Modifier.weight(1f)
            )
        }
        Button(onClick = {
            if (dateText.isNotBlank() && nameText.isNotBlank()) {
                viewModel.addSchoolHoliday(dateText.trim(), nameText.trim())
                dateText = ""
                nameText = ""
            }
        }) { Text("Add") }
    }
}

@Composable
private fun WeeklyPlanSection(uiState: com.akrishna87.tomorrowsbag.ui.UiState, viewModel: TomorrowsBagViewModel) {
    SectionCard(title = "🗓️ Usual weekly plan") {
        Text(
            "Set what's usually needed each day — it'll be suggested automatically.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        var selectedDay by remember { mutableStateOf(viewModel.tomorrowDayOfWeek) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Dates.weekdayShortNames.forEachIndexed { index, label ->
                val dayOfWeek = index + 1
                FilterChip(
                    selected = dayOfWeek == selectedDay,
                    onClick = { selectedDay = dayOfWeek },
                    label = { Text(label) }
                )
            }
        }

        val dayPlan = uiState.weeklyPlan[selectedDay].orEmpty().toSet()
        RemovableChipFlow(
            items = uiState.subjects.map { subject ->
                val label = (if (subject.id in dayPlan) "✅ " else "") + "${subject.emoji} ${subject.name}"
                label to { viewModel.toggleWeeklyPlanSubject(selectedDay, subject.id, subject.id in dayPlan) }
            }
        )
    }
}

@Composable
private fun ManageSubjectsSection(uiState: com.akrishna87.tomorrowsbag.ui.UiState, viewModel: TomorrowsBagViewModel) {
    SectionCard(title = "📓 Manage notebook list") {
        var editingSubject by remember { mutableStateOf<SubjectEntity?>(null) }

        if (editingSubject != null) {
            val subject = editingSubject!!
            var emoji by remember(subject.id) { mutableStateOf(subject.emoji) }
            var name by remember(subject.id) { mutableStateOf(subject.name) }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = emoji, onValueChange = { emoji = it }, modifier = Modifier.weight(0.3f), label = { Text("Icon") })
                OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.weight(0.7f), label = { Text("Name") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    viewModel.renameSubject(subject, name.trim(), emoji.trim())
                    editingSubject = null
                }) { Text("Save") }
                OutlinedButton(onClick = { editingSubject = null }) { Text("Cancel") }
            }
        }

        RemovableChipFlow(
            items = uiState.subjects.map { subject ->
                "${subject.emoji} ${subject.name} ✏️" to { editingSubject = subject }
            }
        )
        Text("Tap ✏️ to rename a subject or change its icon.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

        var newEmoji by remember { mutableStateOf("⭐") }
        var newName by remember { mutableStateOf("") }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = newEmoji, onValueChange = { newEmoji = it }, modifier = Modifier.weight(0.3f), label = { Text("Icon") })
            OutlinedTextField(value = newName, onValueChange = { newName = it }, modifier = Modifier.weight(0.7f), label = { Text("New subject name") })
        }
        Button(onClick = {
            if (newName.isNotBlank()) {
                viewModel.addSubject(newName.trim(), newEmoji.trim().ifBlank { "⭐" })
                newName = ""
                newEmoji = "⭐"
            }
        }) { Text("Add") }
    }
}

@Composable
private fun HistorySection(uiState: com.akrishna87.tomorrowsbag.ui.UiState) {
    SectionCard(title = "📜 History") {
        if (uiState.history.isEmpty()) {
            Text("No history yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            uiState.history.take(21).forEach { entry ->
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Text(Dates.formatShort(Dates.parseKey(entry.dateKey)), fontWeight = FontWeight.Bold)
                    val names = entry.subjects.joinToString("   ") { "${it.emoji} ${it.name}" }
                    Text(names.ifBlank { "Nothing selected" })
                    if (entry.note.isNotBlank()) {
                        Text("📝 ${entry.note}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
