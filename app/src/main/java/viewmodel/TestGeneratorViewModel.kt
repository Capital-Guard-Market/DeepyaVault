package com.deepya.vault.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepya.vault.data.dao.CachedQuestionDao
import com.deepya.vault.engine.TestBlueprint
import com.deepya.vault.engine.TestCompiler
import com.deepya.vault.engine.CompiledTestPaper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface TestGenUiState {
    object Idle : TestGenUiState
    object Loading : TestGenUiState
    data class Success(val paper: CompiledTestPaper) : TestGenUiState
    data class Error(val message: String) : TestGenUiState
}

class TestGeneratorViewModel(
    private val cachedQuestionDao: CachedQuestionDao
) : ViewModel() {

    private val compiler = TestCompiler(cachedQuestionDao)

    private val _uiState = MutableStateFlow<TestGenUiState>(TestGenUiState.Idle)
    val uiState: StateFlow<TestGenUiState> = _uiState.asStateFlow()

    fun generateTestPaper(blueprint: TestBlueprint) {
        viewModelScope.launch {
            _uiState.value = TestGenUiState.Loading

            val result = compiler.compileTest(blueprint)

            _uiState.value = result.fold(
                onSuccess = { paper -> TestGenUiState.Success(paper) },
                onFailure = { error -> TestGenUiState.Error(error.localizedMessage ?: "Unknown compilation error") }
            )
        }
    }

    fun resetState() {
        _uiState.value = TestGenUiState.Idle
    }
}