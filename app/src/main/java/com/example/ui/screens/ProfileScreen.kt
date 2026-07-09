package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Badge
import com.example.model.CourseData
import com.example.ui.theme.*
import com.example.viewmodel.HackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: HackingViewModel) {
    val userStats by viewModel.userStats.collectAsState()
    val completedLessons by viewModel.completedLessons.collectAsState()

    val level = userStats?.level ?: 1
    val xp = userStats?.xp ?: 0
    val unlockedBadgesStr = userStats?.unlockedBadges ?: "script_kiddie"
    val unlockedBadgesList = unlockedBadgesStr.split(",").filter { it.isNotEmpty() }

    // Next Level XP calculation (e.g., Level up occurs every 300 XP)
    val nextLevelXp = level * 300
    val prevLevelXp = (level - 1) * 300
    val levelProgress = (xp - prevLevelXp).toFloat() / 300f

    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = CyberPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "OPERATOR COGNIZANCE",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyberText
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = CyberBlack)
            )
        },
        containerColor = CyberBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Stats summary card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberGray),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "COGNITIVE LEVEL",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = CyberTextMuted
                            )
                            Text(
                                text = "Lvl $level Ethical Hacker",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = CyberGreenNeon,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = CyberGreenNeon,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "XP progress to Rank ${level + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = CyberTextMuted
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { levelProgress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = CyberCyan,
                        trackColor = CyberCard
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$xp / $nextLevelXp XP",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = CyberText
                        )
                        Text(
                            text = "${completedLessons.size} units complete",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = CyberCyan
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Badges Section
            Text(
                text = "SECURITY CREDENTIALS & BADGES",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = CyberPurple,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(CourseData.badges) { badge ->
                    val isUnlocked = unlockedBadgesList.contains(badge.id)
                    BadgeCard(badge = badge, isUnlocked = isUnlocked)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reset progress button
            OutlinedButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, CyberRed.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberRed)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Reset",
                    tint = CyberRed
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Factory Reset Course Progress", fontFamily = FontFamily.Monospace)
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    "HARD FACTORY RESET?",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberRed
                )
            },
            text = {
                Text(
                    "This operation will wipe your level, XP milestones, completed units, database progress, and unlocked credentials. This is irreversible.",
                    color = CyberText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAllProgress()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberRed)
                ) {
                    Text("Proceed Wipe", color = CyberBlack)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = CyberTextMuted)
                }
            },
            containerColor = CyberGray
        )
    }
}

@Composable
fun BadgeCard(badge: Badge, isUnlocked: Boolean) {
    val borderBrush = if (isUnlocked) {
        Brush.linearGradient(listOf(CyberCyan, CyberPurple))
    } else {
        Brush.linearGradient(listOf(CyberCard, CyberCard))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CyberGray)
            .border(BorderStroke(1.dp, borderBrush), RoundedCornerShape(8.dp))
            .padding(12.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isUnlocked) CyberCard else CyberCard.copy(alpha = 0.5f)
                ) {
                    Box(
                        modifier = Modifier.size(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isUnlocked) {
                                CourseData.getChapterIcon(badge.iconName)
                            } else {
                                Icons.Default.Lock
                            },
                            contentDescription = badge.name,
                            tint = if (isUnlocked) CyberGreenNeon else CyberTextMuted.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (isUnlocked) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = "Active credential",
                        tint = CyberGreenNeon,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column {
                Text(
                    text = badge.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) CyberText else CyberTextMuted.copy(alpha = 0.5f)
                )
                Text(
                    text = if (isUnlocked) badge.description else "Requires ${badge.requiredXp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUnlocked) CyberTextMuted else CyberTextMuted.copy(alpha = 0.4f),
                    maxLines = 2
                )
            }
        }
    }
}
