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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gachaproductiveapp.R
import com.example.gachaproductiveapp.ui.components.GlassCard
import com.example.gachaproductiveapp.ui.components.GradientButton
import com.example.gachaproductiveapp.ui.theme.DarkBackground
import com.example.gachaproductiveapp.ui.theme.DarkSurface
import com.example.gachaproductiveapp.ui.theme.PrimaryGold
import com.example.gachaproductiveapp.ui.theme.SecondaryPurple
import com.example.gachaproductiveapp.ui.theme.TextMuted
import com.example.gachaproductiveapp.ui.theme.TextPrimary
import com.example.gachaproductiveapp.ui.theme.TextSecondary
import com.example.gachaproductiveapp.viewmodel.MainViewModel

@Composable
fun WelcomeScreen(viewModel: MainViewModel, onEntered: () -> Unit) {
    var nameInput by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        Color(0xFF1E1B4B),
                        Color(0xFF0F172A)
                    )
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = DarkSurface.copy(alpha = 0.85f),
            borderColor = PrimaryGold.copy(alpha = 0.35f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // App Logo with Glowing Aura
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(PrimaryGold.copy(alpha = 0.3f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "TaskWish Logo",
                        modifier = Modifier.size(110.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TaskWish",
                        style = MaterialTheme.typography.headlineLarge,
                        color = PrimaryGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Turn productivity into wishes and rewards",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    label = { Text("Choose your Traveler Name") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryGold
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGold,
                        unfocusedBorderColor = TextMuted,
                        focusedLabelColor = PrimaryGold,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                GradientButton(
                    text = "Begin Journey",
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            viewModel.setUserName(nameInput.trim())
                            onEntered()
                        }
                    },
                    enabled = nameInput.isNotBlank(),
                    brush = Brush.horizontalGradient(listOf(PrimaryGold, SecondaryPurple)),
                    textColor = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
