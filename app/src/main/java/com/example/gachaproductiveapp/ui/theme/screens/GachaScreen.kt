package com.example.gachaproductiveapp.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.gachaproductiveapp.ui.components.GlassCard
import com.example.gachaproductiveapp.ui.components.GradientButton
import com.example.gachaproductiveapp.ui.components.RarityBadge
import com.example.gachaproductiveapp.ui.components.SectionHeader
import com.example.gachaproductiveapp.ui.theme.DarkBackground
import com.example.gachaproductiveapp.ui.theme.DarkSurface
import com.example.gachaproductiveapp.ui.theme.DarkSurfaceVariant
import com.example.gachaproductiveapp.ui.theme.PrimaryGold
import com.example.gachaproductiveapp.ui.theme.Rarity3Star
import com.example.gachaproductiveapp.ui.theme.Rarity4Star
import com.example.gachaproductiveapp.ui.theme.Rarity5Star
import com.example.gachaproductiveapp.ui.theme.SecondaryPurple
import com.example.gachaproductiveapp.ui.theme.TextMuted
import com.example.gachaproductiveapp.ui.theme.TextPrimary
import com.example.gachaproductiveapp.ui.theme.TextSecondary
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
            .background(DarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                SectionHeader(
                    title = "Event Wish Banner",
                    subtitle = "Summon featured rewards using Favor"
                )
            }

            // Featured Wish Banner Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DarkSurface,
                    borderColor = PrimaryGold.copy(alpha = 0.5f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF3B0764),
                                        Color(0xFF6B21A8),
                                        Color(0xFF78350F)
                                    )
                                )
                            )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = PrimaryGold.copy(alpha = 0.25f),
                                    border = BorderStroke(1.dp, PrimaryGold)
                                ) {
                                    Text(
                                        text = "★ Rate-UP Event Banner",
                                        color = PrimaryGold,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                RarityBadge(rarity = Rarity.FIVE_STAR)
                            }

                            if (featuredReward?.imageUrl?.isNotBlank() == true) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                ) {
                                    AsyncImage(
                                        model = featuredReward.imageUrl,
                                        contentDescription = featuredReward.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Text(
                                text = featuredReward?.name ?: "Configure a Featured 5★ Reward in Setup!",
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = featuredReward?.description?.takeIf { it.isNotBlank() }
                                    ?: "Earn Favor by finishing tasks and wish for your ultimate milestone reward.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            // Pity Tracker & Status Card
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DarkSurfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pity System Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            val pity5 = state?.pity5Counter ?: 0
                            Text(
                                text = "$pity5 / ${GachaEngine.HARD_PITY}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGold
                            )
                        }

                        val pity5 = state?.pity5Counter ?: 0
                        val pityProgress = pity5.toFloat() / GachaEngine.HARD_PITY.toFloat()

                        LinearProgressIndicator(
                            progress = { pityProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = PrimaryGold,
                            trackColor = DarkBackground
                        )

                        val guaranteed = state?.guaranteed5 == true
                        Text(
                            text = if (guaranteed) "★ Next 5★ is GUARANTEED to be the featured reward!" else "Current 5★ featured rate: 50%",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (guaranteed) PrimaryGold else Color(0xFF60A5FA),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Wish Action Buttons Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    GradientButton(
                        text = "Wish x1 (${GachaEngine.PULL_COST} Favor)",
                        onClick = {
                            animatingResults = null
                            isAnimating = true
                            viewModel.pullSingle()
                        },
                        enabled = currency >= GachaEngine.PULL_COST && !isAnimating,
                        brush = Brush.horizontalGradient(listOf(PrimaryGold, Color(0xFFD97706))),
                        modifier = Modifier.weight(1f)
                    )

                    GradientButton(
                        text = "Wish x5 (${GachaEngine.MULTI_PULL_COST} Favor)",
                        onClick = {
                            animatingResults = null
                            isAnimating = true
                            viewModel.pullFive()
                        },
                        enabled = currency >= GachaEngine.MULTI_PULL_COST && !isAnimating,
                        brush = Brush.horizontalGradient(listOf(SecondaryPurple, Color(0xFF6D28D9))),
                        textColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Developer Testing Tools Section
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DarkSurfaceVariant
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Developer Testing Shortcuts",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.devAddFavor() },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                                modifier = Modifier.weight(1f)
                            ) { Text("+5k Favor", color = TextPrimary) }

                            Button(
                                onClick = { viewModel.devResetPity() },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                                modifier = Modifier.weight(1f)
                            ) { Text("Reset Pity", color = TextPrimary) }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { animatingResults = null; isAnimating = true; viewModel.devFreePullSingle() },
                                enabled = !isAnimating,
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                                modifier = Modifier.weight(1f)
                            ) { Text("Free Wish x1", color = PrimaryGold) }

                            Button(
                                onClick = { animatingResults = null; isAnimating = true; viewModel.devFreePullFive() },
                                enabled = !isAnimating,
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                                modifier = Modifier.weight(1f)
                            ) { Text("Free Wish x5", color = SecondaryPurple) }
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
        hasFiveStar -> Rarity5Star
        hasFourStar -> Rarity4Star
        else -> Rarity3Star
    }

    val infiniteTransition = rememberInfiniteTransition(label = "gacha_overlay")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.30f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
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
            .background(Color.Black.copy(alpha = 0.94f))
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
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = when {
                    hasFiveStar -> "✨ LEGENDARY 5★ PULL! ✨"
                    hasFourStar -> "★ EPIC 4★ PULL!"
                    else -> "Summoning Wish..."
                },
                style = MaterialTheme.typography.headlineMedium,
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Tap anywhere to reveal rewards",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
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
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurfaceVariant,
                borderColor = if (hasFiveStar) Rarity5Star else SecondaryPurple
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (hasFiveStar) "✨ Legendary Wish Unlocked! ✨" else "Wish Results",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (hasFiveStar) Rarity5Star else TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(14.dp))

                    LazyColumn(
                        modifier = Modifier.height(360.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(results) { r ->
                            val color = when (r.rarity) {
                                Rarity.FIVE_STAR -> Rarity5Star
                                Rarity.FOUR_STAR -> Rarity4Star
                                Rarity.THREE_STAR -> Rarity3Star
                            }

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = color.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
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
                                            modifier = Modifier
                                                .size(60.dp)
                                                .clip(RoundedCornerShape(10.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        RarityBadge(rarity = r.rarity)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = r.reward?.name ?: "(No reward configured)",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (r.isFeatured) {
                                            Text(
                                                text = "Featured Rate-UP Reward!",
                                                color = Rarity5Star,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            GradientButton(
                text = "Collect Rewards",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(52.dp)
            )
        }
    }
}
