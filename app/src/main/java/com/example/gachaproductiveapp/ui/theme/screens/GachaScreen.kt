package com.example.gachaproductiveapp.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gachaproductiveapp.data.Rarity
import com.example.gachaproductiveapp.logic.GachaEngine
import com.example.gachaproductiveapp.logic.PullResult
import com.example.gachaproductiveapp.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun GachaScreen(viewModel: MainViewModel) {
    val state by viewModel.userState.collectAsState()
    val rewards by viewModel.rewards.collectAsState()
    val lastResult by viewModel.lastPullResult.collectAsState()
    val currency = state?.currency ?: 0
    val featuredReward = rewards.find { it.rarity == Rarity.FIVE_STAR && it.isFeatured }

    var isAnimating by remember { mutableStateOf(false) }
    var animatingResults by remember { mutableStateOf<List<PullResult>?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar: Genshin Style (Title + Currency Top Right)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Wish Banner", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Daily Streak: ${state?.loginStreak ?: 0} days", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                    }
                    // Currency Badge Top Right
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD69E2E))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD69E2E), modifier = Modifier.size(18.dp))
                            Text("$currency Favor", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Featured Wish Banner Card (Genshin Style)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222831))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF4A154B), Color(0xFF6B46C1), Color(0xFFD69E2E))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Text(
                                featuredReward?.name ?: "Set a Featured 5★ in Rewards!",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                featuredReward?.description?.takeIf { it.isNotBlank() } ?: "Your ultimate self-reward milestone.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("★ Rate-up probability increased for featured reward", style = MaterialTheme.typography.labelSmall, color = Color.Yellow)
                        }
                    }
                }
            }

            // Pity Tracker & Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Pity System & Rules", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        
                        val pity5 = state?.pity5Counter ?: 0
                        val pityProgress = pity5.toFloat() / GachaEngine.HARD_PITY
                        Text("5★ Pity: $pity5 / ${GachaEngine.HARD_PITY}", color = Color.White.copy(alpha = 0.9f))
                        LinearProgressIndicator(
                            progress = { pityProgress },
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFD69E2E),
                            trackColor = Color.DarkGray
                        )

                        val guaranteed = state?.guaranteed5 == true
                        Text(
                            if (guaranteed) "Next 5★ is guaranteed to be featured" else "Current 5★ featured chance: 50%",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (guaranteed) Color(0xFFEF4444) else Color(0xFF60A5FA),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Wish Action Buttons (Genshin Bottom Bar Style)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            animatingResults = null
                            isAnimating = true
                            viewModel.pullSingle()
                        },
                        enabled = currency >= GachaEngine.PULL_COST && !isAnimating,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD69E2E))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Wish x1", fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("${GachaEngine.PULL_COST} Favor", style = MaterialTheme.typography.labelSmall, color = Color.Black.copy(alpha = 0.8f))
                        }
                    }
                    Button(
                        onClick = {
                            animatingResults = null
                            isAnimating = true
                            viewModel.pullFive()
                        },
                        enabled = currency >= GachaEngine.MULTI_PULL_COST && !isAnimating,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF805AD5))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Wish x5", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("${GachaEngine.MULTI_PULL_COST} Favor", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }

            // Developer Tools Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF374151))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Developer Testing Tools", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { viewModel.devAddFavor() }, modifier = Modifier.weight(1f)) { Text("+5k Favor") }
                            Button(onClick = { viewModel.devResetPity() }, modifier = Modifier.weight(1f)) { Text("Reset Pity") }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { animatingResults = null; isAnimating = true; viewModel.devFreePullSingle() }, enabled = !isAnimating, modifier = Modifier.weight(1f)) { Text("Free Wish x1") }
                            Button(onClick = { animatingResults = null; isAnimating = true; viewModel.devFreePullFive() }, enabled = !isAnimating, modifier = Modifier.weight(1f)) { Text("Free Wish x5 (Inf)") }
                        }
                    }
                }
            }
        }
    }

    // Trigger animation when lastResult updates
    LaunchedEffect(lastResult) {
        if (lastResult != null) {
            animatingResults = lastResult
            isAnimating = true
        }
    }

    if (isAnimating && animatingResults != null) {
        GachaAnimationOverlay(
            results = animatingResults!!,
            onFinished = {
                isAnimating = false
                animatingResults = null
            }
        )
    } else if (lastResult != null && !isAnimating) {
        SSRResultsDialog(
            results = lastResult!!,
            onDismiss = { viewModel.clearPullResult() }
        )
    }
}

@Composable
fun GachaAnimationOverlay(
    results: List<PullResult>,
    onFinished: () -> Unit
) {
    val hasFiveStar = results.any { it.rarity == Rarity.FIVE_STAR }
    val hasFourStar = results.any { it.rarity == Rarity.FOUR_STAR }

    val primaryColor = when {
        hasFiveStar -> Color(0xFFD69E2E) // Gold SSR
        hasFourStar -> Color(0xFF805AD5) // Purple SR
        else -> Color(0xFF3182CE) // Blue R
    }

    val infiniteTransition = rememberInfiniteTransition(label = "gacha")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(2200)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .clickable { onFinished() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size((180 * scale).dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(primaryColor, Color.Transparent)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(72.dp)
                )
            }
            Spacer(Modifier.height(32.dp))
            Text(
                if (hasFiveStar) "Legendary pull" else if (hasFourStar) "Epic pull" else "Summoning...",
                style = MaterialTheme.typography.headlineMedium,
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Tap anywhere to reveal",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun SSRResultsDialog(
    results: List<PullResult>,
    onDismiss: () -> Unit
) {
    val hasFiveStar = results.any { it.rarity == Rarity.FIVE_STAR }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SSR Badge (Inspired by Chinese SSR Pull Screen)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1F2937),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFD69E2E)),
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        if (hasFiveStar) "Legendary Reward Pulled" else "Pull Results",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFD69E2E),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.height(360.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(results) { r ->
                            val color = when (r.rarity) {
                                Rarity.FIVE_STAR -> Color(0xFFD69E2E)
                                Rarity.FOUR_STAR -> Color(0xFF805AD5)
                                Rarity.THREE_STAR -> Color(0xFF3182CE)
                            }
                            val stars = when (r.rarity) {
                                Rarity.FIVE_STAR -> "★★★★★"
                                Rarity.FOUR_STAR -> "★★★★☆"
                                Rarity.THREE_STAR -> "★★★☆☆"
                            }
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = color.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, color)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (r.reward?.imageUrl?.isNotBlank() == true) {
                                        AsyncImage(
                                            model = r.reward.imageUrl,
                                            contentDescription = r.reward.name,
                                            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(stars, color = color, fontWeight = FontWeight.Bold)
                                        Text(
                                            r.reward?.name ?: "(No reward configured)",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        if (r.isFeatured) {
                                            Text("Featured 5-star reward", color = Color(0xFFD69E2E), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Action Buttons (Inspired by SSR pull screen bottom action bar)
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD69E2E))
            ) {
                Text("Next / Collect", style = MaterialTheme.typography.titleMedium, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
