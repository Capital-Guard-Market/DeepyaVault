package com.deepya.vault.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deepya.vault.data.model.AppGatewayMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    onGatewaySelected: (AppGatewayMode) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Teacher Command Center", fontWeight = FontWeight.Bold)
                        Text(text = "Welcome back, Deekshit", style = MaterialTheme.typography.bodySmall)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select Workspace Mode",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Gateway Card 1: Question Bank Vault
            GatewayCard(
                title = "📂 Downloadable Question Banks",
                subtitle = "Browse chapter-wise high-volume question packs (80–300+ Qs), lazy download offline, and share instantly via PDF or WhatsApp.",
                onClick = { onGatewaySelected(AppGatewayMode.QUESTION_BANKS) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Gateway Card 2: Elite Assessment & Test Builder
            GatewayCard(
                title = "📝 Elite Assessment & Test Builder",
                subtitle = "Configure custom board or competitive tests using strict syllabus isolation, official mark blueprints, and source engines.",
                onClick = { onGatewaySelected(AppGatewayMode.TESTS) }
            )
        }
    }
}

@Composable
fun GatewayCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}