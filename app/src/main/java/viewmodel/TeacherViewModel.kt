package com.deepya.vault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepya.vault.data.entity.CachedQuestionEntity
import com.deepya.vault.data.model.QuestionSlotConfig
import com.deepya.vault.data.repository.QuestionRepository
import com.deepya.vault.data.repository.VaultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeacherViewModel(
    private val repository: VaultRepository,
    private val questionRepository: QuestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeacherUiState>(TeacherUiState.Idle)
    val uiState: StateFlow<TeacherUiState> = _uiState.asStateFlow()

    private val _generatedPaper = MutableStateFlow<List<CachedQuestionEntity>>(emptyList())
    val generatedPaper: StateFlow<List<CachedQuestionEntity>> = _generatedPaper.asStateFlow()

    // --- Standard Paper Generation ---
    fun generateCustomPaper(curriculum: String, subject: String, grade: Int, limit: Int) {
        viewModelScope.launch {
            _uiState.value = TeacherUiState.Loading
            try {
                val questions = repository.generatePaperQuestions(curriculum, subject, grade, limit)
                if (questions.isNotEmpty()) {
                    _generatedPaper.value = questions
                    _uiState.value = TeacherUiState.Success("Paper successfully generated with anti-repetition tracking!")
                } else {
                    _uiState.value = TeacherUiState.Error("No questions found matching the criteria.")
                }
            } catch (e: Exception) {
                _uiState.value = TeacherUiState.Error(e.localizedMessage ?: "Failed to generate paper due to an unexpected error.")
            }
        }
    }

    // --- HOT Questions Loader ---
    fun loadHotQuestions(curriculum: String, subject: String, limit: Int) {
        viewModelScope.launch {
            _uiState.value = TeacherUiState.Loading
            try {
                val hotQuestions = repository.getHotQuestions(curriculum, subject, limit)
                if (hotQuestions.isNotEmpty()) {
                    _generatedPaper.value = hotQuestions
                    _uiState.value = TeacherUiState.Success("Loaded HOT standard questions successfully.")
                } else {
                    _uiState.value = TeacherUiState.Error("No HOT questions available for this subject.")
                }
            } catch (e: Exception) {
                _uiState.value = TeacherUiState.Error(e.localizedMessage ?: "Failed to load HOT questions.")
            }
        }
    }

    // --- Robust Itemized Surgical Slot Generation Engine ---
    fun generateItemizedPaper(
        curriculum: String,
        subject: String,
        slotConfigs: List<QuestionSlotConfig>
    ) {
        viewModelScope.launch {
            if (slotConfigs.isEmpty()) {
                _uiState.value = TeacherUiState.Error("Cannot generate a paper with zero slots configured.")
                return@launch
            }

            _uiState.value = TeacherUiState.Loading
            try {
                val assembledQuestions = mutableListOf<CachedQuestionEntity>()

                for (slot in slotConfigs) {
                    val question = questionRepository.fetchQuestionForSlot(
                        curriculum = curriculum,
                        subject = subject,
                        chapterId = slot.chapterId,
                        difficulty = slot.difficulty,
                        marks = slot.marks,
                        sourceType = slot.sourceType
                    )

                    if (question != null) {
                        assembledQuestions.add(question)
                    }
                }

                if (assembledQuestions.isNotEmpty()) {
                    _generatedPaper.value = assembledQuestions
                    _uiState.value = TeacherUiState.Success("Surgical paper generated with ${assembledQuestions.size} itemized slots!")
                } else {
                    _uiState.value = TeacherUiState.Error("Could not assemble questions for the selected slot criteria.")
                }
            } catch (e: Exception) {
                _uiState.value = TeacherUiState.Error("Generation failed: ${e.localizedMessage ?: "Unknown repository exception"}")
            }
        }
    }

    // --- Safe Side Swap Option for Preview Screen ---
    fun swapQuestionSlot(
        index: Int,
        curriculum: String,
        subject: String,
        slotConfig: QuestionSlotConfig
    ) {
        viewModelScope.launch {
            val currentList = _generatedPaper.value.toMutableList()
            if (index in currentList.indices) {
                try {
                    val freshQuestion = questionRepository.fetchQuestionForSlot(
                        curriculum = curriculum,
                        subject = subject,
                        chapterId = slotConfig.chapterId,
                        difficulty = slotConfig.difficulty,
                        marks = slotConfig.marks,
                        sourceType = "ON_THE_FLY"
                    )
                    if (freshQuestion != null) {
                        currentList[index] = freshQuestion
                        _generatedPaper.value = currentList
                    }
                } catch (e: Exception) {
                    // Fail gracefully on slot swap without crashing UI state
                }
            }
        }
    }
}

sealed class TeacherUiState {
    object Idle : TeacherUiState()
    object Loading : TeacherUiState()
    data class Success(val message: String) : TeacherUiState()
    data class Error(val error: String) : TeacherUiState()
}