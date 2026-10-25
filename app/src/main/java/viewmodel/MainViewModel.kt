package com.deepya.vault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.deepya.vault.data.repository.QuestionRepository
import com.deepya.vault.data.repository.TokenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole { TEACHER, STUDENT }
enum class ExamStream { BOARD, COMPETITIVE }
enum class AcademicBranch { ALL, COMMERCE, SCIENCE, HUMANITIES }

data class SubjectItem(
    val id: String,
    val title: String,
    val curriculum: String, // CBSE, ISC, CAMBRIDGE, CUET, etc.
    val branch: AcademicBranch,
    val questionCount: Int,
    val isUnlocked: Boolean
)

class MainViewModel(
    private val tokenRepository: TokenRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    val liveTokenBalance: StateFlow<Int> = tokenRepository.liveBalance
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 140
        )

    private val _selectedRole = MutableStateFlow(UserRole.TEACHER)
    val selectedRole: StateFlow<UserRole> = _selectedRole.asStateFlow()

    private val _selectedStream = MutableStateFlow(ExamStream.BOARD)
    val selectedStream: StateFlow<ExamStream> = _selectedStream.asStateFlow()

    private val _selectedBranch = MutableStateFlow(AcademicBranch.ALL)
    val selectedBranch: StateFlow<AcademicBranch> = _selectedBranch.asStateFlow()

    private val _selectedCurriculum = MutableStateFlow("CBSE")
    val selectedCurriculum: StateFlow<String> = _selectedCurriculum.asStateFlow()

    fun setRole(role: UserRole) { _selectedRole.value = role }
    fun setStream(stream: ExamStream) {
        _selectedStream.value = stream
        _selectedBranch.value = AcademicBranch.ALL
    }
    fun setBranch(branch: AcademicBranch) { _selectedBranch.value = branch }
    fun setCurriculum(curriculum: String) { _selectedCurriculum.value = curriculum }

    fun unlockChapter(chapterId: String, cost: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = tokenRepository.unlockChapter(chapterId, cost)
            result.onSuccess {
                onResult(true, "Chapter unlocked successfully!")
            }.onFailure { error ->
                onResult(false, error.message ?: "Failed to unlock")
            }
        }
    }
}

class MainViewModelFactory(
    private val tokenRepository: TokenRepository,
    private val questionRepository: QuestionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(tokenRepository, questionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}