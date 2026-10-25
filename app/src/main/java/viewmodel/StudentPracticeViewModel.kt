package com.deepya.vault.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepya.vault.data.entity.MistakeEntity
import com.deepya.vault.data.repository.VaultRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentPracticeViewModel(
    private val repository: VaultRepository,
    private val studentId: String
) : ViewModel() {

    // Reactive stream for unresolved mistakes driving the spaced-repetition remediation loop
    val unresolvedMistakes = repository.getUnresolvedMistakesStream(studentId)

    private val _dueReviewQuestions = MutableStateFlow<List<MistakeEntity>>(emptyList())
    val dueReviewQuestions: StateFlow<List<MistakeEntity>> = _dueReviewQuestions.asStateFlow()

    init {
        fetchDueMistakesForReview()
    }

    fun fetchDueMistakesForReview() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val dueMistakes = repository.fetchDueMistakes(studentId, currentTime)
            _dueReviewQuestions.value = dueMistakes
        }
    }

    fun recordIncorrectAnswer(questionId: Long, stem: String, correct: String, studentAns: String) {
        viewModelScope.launch {
            // 3 days out in milliseconds (3 * 24 * 60 * 60 * 1000)
            val threeDaysInMillis = 259200000L
            val nextReview = System.currentTimeMillis() + threeDaysInMillis

            val mistake = MistakeEntity(
                studentId = studentId,
                questionId = questionId,
                questionStem = stem,
                correctAnswer = correct,
                studentAnswer = studentAns,
                isResolved = false,
                reviewCount = 1,
                nextReviewTimestamp = nextReview
            )
            repository.recordStudentMistake(mistake)
        }
    }

    fun markMistakeResolved(mistakeId: Long) {
        viewModelScope.launch {
            repository.resolveMistakeItem(mistakeId)
        }
    }

    fun handleIncorrectOnReview(mistakeId: Long) {
        viewModelScope.launch {
            // Push review 5 days further out upon repeated failure
            val fiveDaysInMillis = 432000000L
            val newTimestamp = System.currentTimeMillis() + fiveDaysInMillis
            repository.rescheduleMistake(mistakeId, newTimestamp)
        }
    }
}