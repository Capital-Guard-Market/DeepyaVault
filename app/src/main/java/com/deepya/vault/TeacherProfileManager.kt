package com.deepya.vault

import android.content.Context
import android.content.SharedPreferences

data class TeacherProfile(
    val isConfigured: Boolean = false,
    val teacherName: String = "",
    val schoolName: String = "",
    val defaultBoard: String = "CBSE",
    val assignedClasses: String = "Grade XII",
    val assignedSubjects: Set<String> = emptySet(),
    val isTestModeAllSubjectsEnabled: Boolean = true
)

object TeacherProfileManager {
    private const val PREFS_NAME = "teacher_profile_prefs"
    private const val KEY_IS_CONFIGURED = "is_configured"
    private const val KEY_TEACHER_NAME = "teacher_name"
    private const val KEY_SCHOOL_NAME = "school_name"
    private const val KEY_BOARD = "default_board"
    private const val KEY_CLASSES = "assigned_classes"
    private const val KEY_SUBJECTS = "assigned_subjects"
    private const val KEY_TEST_MODE = "test_mode_all_subjects"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getProfile(context: Context): TeacherProfile {
        val prefs = getPrefs(context)
        return TeacherProfile(
            isConfigured = prefs.getBoolean(KEY_IS_CONFIGURED, false),
            teacherName = prefs.getString(KEY_TEACHER_NAME, "") ?: "",
            schoolName = prefs.getString(KEY_SCHOOL_NAME, "") ?: "",
            defaultBoard = prefs.getString(KEY_BOARD, "CBSE") ?: "CBSE",
            assignedClasses = prefs.getString(KEY_CLASSES, "Grade XII") ?: "Grade XII",
            assignedSubjects = prefs.getStringSet(KEY_SUBJECTS, emptySet()) ?: emptySet(),
            isTestModeAllSubjectsEnabled = prefs.getBoolean(KEY_TEST_MODE, true)
        )
    }

    fun saveProfile(context: Context, profile: TeacherProfile) {
        getPrefs(context).edit().apply {
            putBoolean(KEY_IS_CONFIGURED, profile.isConfigured)
            putString(KEY_TEACHER_NAME, profile.teacherName)
            putString(KEY_SCHOOL_NAME, profile.schoolName)
            putString(KEY_BOARD, profile.defaultBoard)
            putString(KEY_CLASSES, profile.assignedClasses)
            putStringSet(KEY_SUBJECTS, profile.assignedSubjects)
            putBoolean(KEY_TEST_MODE, profile.isTestModeAllSubjectsEnabled)
            apply()
        }
    }
}