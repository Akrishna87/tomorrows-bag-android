package com.akrishna87.tomorrowsbag.data.prefs

import android.content.Context

class ProfilePrefs(context: Context) {
    private val prefs = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

    var kidName: String
        get() = prefs.getString(KEY_KID_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_KID_NAME, value).apply()

    var kidClass: String
        get() = prefs.getString(KEY_KID_CLASS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_KID_CLASS, value).apply()

    var parentName: String
        get() = prefs.getString(KEY_PARENT_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PARENT_NAME, value).apply()

    var schoolLocation: String
        get() = prefs.getString(KEY_SCHOOL_LOCATION, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SCHOOL_LOCATION, value).apply()

    companion object {
        private const val KEY_KID_NAME = "kid_name"
        private const val KEY_KID_CLASS = "kid_class"
        private const val KEY_PARENT_NAME = "parent_name"
        private const val KEY_SCHOOL_LOCATION = "school_location"
    }
}
