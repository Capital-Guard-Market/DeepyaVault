package com.deepya.vault.data.entity

data class TeacherProfileEntity(
    val teacherId: String = "teacher_primary",
    val teacherName: String = "Teacher",
    val schoolName: String = "",
    val walletPoints: Long = 20000L,
    val referralCode: String = "TEACH50",
    val referredBy: String? = null,
    val isAccountActive: Boolean = true
)