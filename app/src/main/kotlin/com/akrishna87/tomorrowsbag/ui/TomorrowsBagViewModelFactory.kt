package com.akrishna87.tomorrowsbag.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akrishna87.tomorrowsbag.data.prefs.ProfilePrefs
import com.akrishna87.tomorrowsbag.data.repository.TomorrowsBagRepository

class TomorrowsBagViewModelFactory(
    private val repository: TomorrowsBagRepository,
    private val profilePrefs: ProfilePrefs
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TomorrowsBagViewModel::class.java)) {
            return TomorrowsBagViewModel(repository, profilePrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}
