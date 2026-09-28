package com.example.gachaproductiveapp.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gachaproductiveapp.logic.GachaEngine
import com.example.gachaproductiveapp.ui.components.GlassCard
import com.example.gachaproductiveapp.ui.components.SectionHeader
import com.example.gachaproductiveapp.ui.components.StatCard
import com.example.gachaproductiveapp.ui.theme.AccentCyan
import com.example.gachaproductiveapp.ui.theme.AccentEmerald
import com.example.gachaproductiveapp.ui.theme.DarkSurface
import com.example.gachaproductiveapp.ui.theme.DarkSurfaceVariant
import com.example.gachaproductiveapp.ui.theme.PrimaryGold
import com.example.gachaproductiveapp.ui.theme.SecondaryPurple
import com.example.gachaproductiveapp.ui.theme.TextMuted
import com.example.gachaproductiveapp.ui.theme.TextPrimary
import com.example.gachaproductiveapp.ui.theme.TextSecondary
import com.example.gachaproductiveapp.viewmodel.MainViewModel

@Composable
fun CurrencyScreen(viewModel: MainViewModel) {
    val state by viewModel.userState.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    val totalFavor = state?.currency ?: 0
    val availablePulls = totalFavor / GachaEngine.PULL_COST
    val favorRemainder = totalFavor % GachaEngine.PULL_COST
    val progressToNextPull = favorRemainder.toFloat() / GachaEngine.PULL_COST.toFloat()

    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }
    val completionRate = if (totalTasks > 0) (completedTasks.toFloat() / totalTasks) * 100f else 0f
    val streak = state?.loginStreak ?: 0

    val travelerRank = when {
        completedTasks >= 50 -> "Legendary Architect"
        completedTasks >= 25 -> "Master Planner"
        completedTasks >= 10 -> "Dedicated Adventurer"
        completedTasks >= 3 -> "Promising Traveler"
        else -> "Novice Traveler"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        SectionHeader(
            title = "Productivity Dashboard",
            subtitle = "Track your Favor earnings and task progress"
        )

        // Hero Balance Banner Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = DarkSurface,
            borderColor = PrimaryGold.copy(alpha = 0.4f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF78350F).copy(alpha = 0.4f),
                                DarkSurface,
                                Color(0xFF4C1D95).copy(alpha = 0.3f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Current Balance",
                                style = MaterialTheme.typography.labelMedium,
                                color = TextSecondary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = PrimaryGold,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "$totalFavor Favor",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGold
                                )
                            }
                        }

                        // Pull Readiness Pill
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = SecondaryPurple.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryPurple.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SecondaryPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$availablePulls Wishes Ready",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // Progress to Next Pull Indicator
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progress to Next Wish",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                            Text(
                                text = "$favorRemainder / ${GachaEngine.PULL_COST} Favor",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGold
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progressToNextPull },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = PrimaryGold,
                            trackColor = DarkSurfaceVariant
                        )
                    }
                }
            }
        }

        // Rank Badge Banner
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = DarkSurfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(SecondaryPurple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = SecondaryPurple,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Traveler Title",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = travelerRank,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        // Analytics Cards Grid (2x2)
        Text(
            text = "Productivity Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Tasks",
                value = "$totalTasks",
                subtitle = "Tasks created",
                icon = Icons.Default.FormatListBulleted,
                accentColor = AccentCyan,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Completed",
                value = "$completedTasks",
                subtitle = "Tasks done",
                icon = Icons.Default.CheckCircle,
                accentColor = AccentEmerald,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Completion Rate",
                value = "%.0f%%".format(completionRate),
                subtitle = "Productivity index",
                icon = Icons.Default.TrendingUp,
                accentColor = PrimaryGold,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Daily Streak",
                value = "${streak}d",
                subtitle = "Active streak",
                icon = Icons.Default.LocalFireDepartment,
                accentColor = Color(0xFFF97316),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
