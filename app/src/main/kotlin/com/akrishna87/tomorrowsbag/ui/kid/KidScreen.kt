package com.akrishna87.tomorrowsbag.ui.kid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akrishna87.tomorrowsbag.ui.TomorrowsBagViewModel
import com.akrishna87.tomorrowsbag.ui.components.SectionCard
import com.akrishna87.tomorrowsbag.ui.components.SubjectTile
import com.akrishna87.tomorrowsbag.util.Dates

private val tileTints = listOf(
    Color(0xFFFFE3EC), Color(0xFFE3F7FF), Color(0xFFFFF4D6), Color(0xFFE8FFE3),
    Color(0xFFEFE3FF), Color(0xFFFFE9E0), Color(0xFFE3FFF6), Color(0xFFFDEAFF)
)

@Composable
fun KidScreen(viewModel: TomorrowsBagViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val profile by viewModel.profile.collectAsState()

    LaunchedEffect(uiState.tomorrowPackedSubjectIds, uiState.weeklyPlan) {
        if (!viewModel.isEditingSavedPlan) {
            viewModel.ensureDraftInitialized(uiState)
        }
    }

    val alreadySaved = uiState.tomorrowPackedSubjectIds != null
    val showConfirmation = alreadySaved && !viewModel.isEditingSavedPlan

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Today: ${Dates.formatLong(Dates.today())}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        val holidayTomorrow = uiState.schoolHolidays.firstOrNull { it.date == Dates.key(Dates.tomorrow()) }
        if (holidayTomorrow != null) {
            SectionCard {
                Text("🏫 No school tomorrow: ${holidayTomorrow.name}", fontWeight = FontWeight.Bold)
            }
        }

        if (showConfirmation) {
            ConfirmationView(viewModel, uiState)
        } else {
            val greeting = if (profile.kidName.isNotBlank()) "👋 Hi, ${profile.kidName}!" else "🌟 What are you bringing tomorrow?"
            Text(greeting, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            val subLine = Dates.formatLong(Dates.tomorrow()) + if (profile.kidClass.isNotBlank()) " · 🏫 ${profile.kidClass}" else ""
            Text(subLine, color = MaterialTheme.colorScheme.onSurfaceVariant)

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(uiState.subjects.size) { index ->
                    val subject = uiState.subjects[index]
                    val selected = viewModel.draftSelection?.contains(subject.id) == true
                    SubjectTile(
                        subject = subject,
                        selected = selected,
                        tint = tileTints[index % tileTints.size],
                        onClick = { viewModel.toggleDraftSubject(subject.id) }
                    )
                }
            }

            OutlinedTextField(
                value = viewModel.draftNote ?: "",
                onValueChange = { viewModel.updateDraftNote(it) },
                label = { Text("📝 Anything else to remember?") },
                placeholder = { Text("like PE shoes or a water bottle") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { viewModel.saveTomorrowPlan() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    if (alreadySaved) "🎒 Save my changes!" else "🎒 All Packed!",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ConfirmationView(viewModel: TomorrowsBagViewModel, uiState: com.akrishna87.tomorrowsbag.ui.UiState) {
    val packedIds = uiState.tomorrowPackedSubjectIds.orEmpty().toSet()
    val packedSubjects = uiState.subjects.filter { it.id in packedIds }

    Text("🎉🎒✨", fontSize = 32.sp)
    Text("All packed for tomorrow!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text(Dates.formatLong(Dates.tomorrow()))

    if (packedSubjects.isEmpty()) {
        Text("Nothing selected", color = MaterialTheme.colorScheme.onSurfaceVariant)
    } else {
        Text(packedSubjects.joinToString("   ") { "${it.emoji} ${it.name}" })
    }

    if (uiState.tomorrowNote.isNotBlank()) {
        Text("📝 ${uiState.tomorrowNote}", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }

    OutlinedButton(
        onClick = { viewModel.startEditingSavedPlan(uiState) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("✏️ Change my answer")
    }
}
