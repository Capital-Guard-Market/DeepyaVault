package com.deepya.vault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.deepya.vault.data.model.AppGatewayMode
import com.deepya.vault.data.model.CurriculumTrack
import com.deepya.vault.data.repository.QuestionRepository
import com.deepya.vault.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val questionRepository = remember { QuestionRepository() }

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var selectedRoleString by remember { mutableStateOf<String?>(null) }
                    var currentGateway by remember { mutableStateOf<AppGatewayMode?>(null) }
                    var selectedTrack by remember { mutableStateOf<CurriculumTrack?>(null) }
                    var showPaperConfig by remember { mutableStateOf(false) }

                    when {
                        // 1. Initial Role Entry Point (Teacher vs Student)
                        selectedRoleString == null -> {
                            RoleSelectionScreen(
                                onTeacherSelected = {
                                    selectedRoleString = "TEACHER"
                                },
                                onStudentSelected = {
                                    selectedRoleString = "STUDENT"
                                }
                            )
                        }

                        // 2. Student Portal view
                        selectedRoleString == "STUDENT" -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Student Portal: Ready for shared question papers & practice packs.")
                            }
                        }

                        // 3. Teacher Flow: Question Bank Vault Chapter List
                        currentGateway == AppGatewayMode.QUESTION_BANKS && selectedTrack != null -> {
                            QuestionBankListScreen(
                                track = selectedTrack!!,
                                context = applicationContext,
                                repository = questionRepository,
                                onBack = { selectedTrack = null },
                                onViewChapter = { _ -> },
                                onShareChapter = { _ -> },
                                onDownloadChapter = { _ -> }
                            )
                        }

                        // 4. Teacher Flow: Paper Config Screen
                        currentGateway == AppGatewayMode.TESTS && selectedTrack != null && showPaperConfig -> {
                            PaperConfigScreen(
                                track = selectedTrack!!,
                                repository = questionRepository,
                                onGenerateClicked = { _, _, _, _, _, _ ->
                                    showPaperConfig = false
                                },
                                onBack = {
                                    showPaperConfig = false
                                    selectedTrack = null
                                }
                            )
                        }

                        // 5. Teacher Flow: Test Builder Slot Builder Screen
                        currentGateway == AppGatewayMode.TESTS && selectedTrack != null && !showPaperConfig -> {
                            TestSlotBuilderScreen(
                                track = selectedTrack!!,
                                onBack = { 
                                    showPaperConfig = true 
                                },
                                onGeneratePaper = { slots ->
                                    // Compilation engine trigger for generated test slots
                                }
                            )
                        }

                        // 6. Teacher Flow: Curriculum & Track Selector
                        currentGateway != null -> {
                            CurriculumSelectorScreen(
                                gatewayMode = currentGateway!!,
                                onTrackSelected = { track ->
                                    selectedTrack = track
                                    if (currentGateway == AppGatewayMode.TESTS) {
                                        showPaperConfig = true
                                    }
                                },
                                onBack = { currentGateway = null }
                            )
                        }

                        // 7. Teacher Flow: Main Dashboard Landing Page
                        else -> {
                            MainDashboardScreen(
                                onGatewaySelected = { mode ->
                                    currentGateway = mode
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}