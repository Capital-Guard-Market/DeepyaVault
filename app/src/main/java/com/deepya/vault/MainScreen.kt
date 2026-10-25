package com.deepya.vault

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.deepya.vault.viewmodel.MainViewModel

@Composable
fun MainScreenContent(
    viewModel: MainViewModel,
    onOpenTeacherCommandCenter: () -> Unit
) {
    val tokenBalance by viewModel.liveTokenBalance.collectAsState()
    val selectedRole by viewModel.selectedRole.collectAsState()
    val selectedStream by viewModel.selectedStream.collectAsState()
    val selectedCurriculum by viewModel.selectedCurriculum.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Deepya Vault: Elite Edition",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Role: $selectedRole | Stream: $selectedStream",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Curriculum: $selectedCurriculum | Tokens: $tokenBalance",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            Button(
                onClick = onOpenTeacherCommandCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "🚀 Open Teacher Command Center", fontWeight = FontWeight.Bold)
            }
        }
    }
}