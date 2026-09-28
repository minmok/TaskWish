package com.example.gachaproductiveapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gachaproductiveapp.R
import com.example.gachaproductiveapp.viewmodel.MainViewModel

@Composable
fun WelcomeScreen(viewModel: MainViewModel, onEntered: () -> Unit) {
    var nameInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Logo Splash Image
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "TaskWish Logo",
                modifier = Modifier.size(130.dp)
            )

            Spacer(Modifier.height(4.dp))

            Text(
                "✨ TaskWish ✨",
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFFD69E2E),
                fontWeight = FontWeight.Bold
            )
            Text(
                "Gacha Productivity Manager",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Enter your Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (nameInput.isNotBlank()) {
                        viewModel.setUserName(nameInput.trim())
                        onEntered()
                    }
                },
                enabled = nameInput.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD69E2E))
            ) {
                Text("Start Your Journey", style = MaterialTheme.typography.titleMedium, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
