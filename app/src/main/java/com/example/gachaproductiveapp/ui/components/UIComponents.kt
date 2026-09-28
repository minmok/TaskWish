package com.example.gachaproductiveapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.gachaproductiveapp.data.Rarity
import com.example.gachaproductiveapp.data.TaskTier
import com.example.gachaproductiveapp.ui.theme.DarkSurface
import com.example.gachaproductiveapp.ui.theme.DarkSurfaceVariant
import com.example.gachaproductiveapp.ui.theme.GlassBorderColor
import com.example.gachaproductiveapp.ui.theme.PrimaryGold
import com.example.gachaproductiveapp.ui.theme.Rarity3Star
import com.example.gachaproductiveapp.ui.theme.Rarity4Star
import com.example.gachaproductiveapp.ui.theme.Rarity5Star
import com.example.gachaproductiveapp.ui.theme.TextMuted
import com.example.gachaproductiveapp.ui.theme.TextPrimary
import com.example.gachaproductiveapp.ui.theme.TextSecondary
import com.example.gachaproductiveapp.ui.theme.TierEasyColor
import com.example.gachaproductiveapp.ui.theme.TierHardColor
import com.example.gachaproductiveapp.ui.theme.TierMediumColor

/**
 * Modern Glassmorphism Styled Card with subtle translucent border and elevation.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    borderColor: Color = GlassBorderColor,
    backgroundColor: Color = DarkSurface,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val cardShape = RoundedCornerShape(cornerRadius)
    Card(
        modifier = modifier
            .clip(cardShape)
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        content()
    }
}

/**
 * Rarity Badge displaying star rating and custom background glow for SSR/SR/R.
 */
@Composable
fun RarityBadge(rarity: Rarity, modifier: Modifier = Modifier) {
    val (color, label) = when (rarity) {
        Rarity.FIVE_STAR -> Rarity5Star to "5★ SSR"
        Rarity.FOUR_STAR -> Rarity4Star to "4★ SR"
        Rarity.THREE_STAR -> Rarity3Star to "3★ R"
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = color,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Difficulty Tier Badge for tasks (EASY, MEDIUM, HARD).
 */
@Composable
fun TierBadge(tier: TaskTier, modifier: Modifier = Modifier) {
    val color = when (tier) {
        TaskTier.EASY -> TierEasyColor
        TaskTier.MEDIUM -> TierMediumColor
        TaskTier.HARD -> TierHardColor
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Text(
            text = "${tier.label} · +${tier.currencyReward} Favor",
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Reusable Stat Box for dashboard and analytics views.
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    accentColor: Color = PrimaryGold,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        backgroundColor = DarkSurfaceVariant,
        borderColor = accentColor.copy(alpha = 0.25f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}

/**
 * Primary Gradient Action Button.
 */
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    brush: Brush = Brush.horizontalGradient(listOf(PrimaryGold, Color(0xFFF59E0B))),
    textColor: Color = Color.Black
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(
                    if (enabled) brush else Brush.linearGradient(listOf(Color.DarkGray, Color.Gray)),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = if (enabled) textColor else Color.White.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Standard Header for screen sections.
 */
@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
        if (action != null) {
            action()
        }
    }
}

/**
 * Clean Empty State graphics view when list content is empty.
 */
@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = DarkSurfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryGold.copy(alpha = 0.1f))
                    .border(1.dp, PrimaryGold.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryGold,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            if (actionButtonText != null && onActionClick != null) {
                Spacer(Modifier.height(20.dp))
                GradientButton(
                    text = actionButtonText,
                    onClick = onActionClick,
                    modifier = Modifier.width(180.dp)
                )
            }
        }
    }
}
