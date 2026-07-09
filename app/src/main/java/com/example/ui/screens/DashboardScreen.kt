package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CompletedLesson
import com.example.model.Chapter
import com.example.model.CourseData
import com.example.model.CoursePhase
import com.example.model.Lesson
import com.example.ui.theme.*
import com.example.viewmodel.HackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HackingViewModel,
    onLessonClick: (Lesson) -> Unit
) {
    val completedLessons by viewModel.completedLessons.collectAsState()
    val userStats by viewModel.userStats.collectAsState()

    val totalLessons = CourseData.phases.flatMap { it.chapters.flatMap { it.lessons } }.size
    val completedCount = completedLessons.size
    val progressPercent = if (totalLessons > 0) completedCount.toFloat() / totalLessons else 0f

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "AEGIS CYBER ACADEMY",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = CyberGreenNeon,
                        letterSpacing = 2.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = CyberBlack
                )
            )
        },
        containerColor = CyberBlack
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Premium Header Banner ---
            item {
                HackingProgressBanner(
                    level = userStats?.level ?: 1,
                    xp = userStats?.xp ?: 0,
                    completedCount = completedCount,
                    totalLessons = totalLessons,
                    progressPercent = progressPercent
                )
            }

            // --- Phase Sections ---
            items(CourseData.phases) { phase ->
                PhaseSectionCard(
                    phase = phase,
                    completedLessons = completedLessons,
                    onLessonClick = onLessonClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HackingProgressBanner(
    level: Int,
    xp: Int,
    completedCount: Int,
    totalLessons: Int,
    progressPercent: Float
) {
    val animatedProgress by animateFloatAsState(targetValue = progressPercent, label = "Progress")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberGray),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(CyberGreen, CyberCyan))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OPERATOR RANK",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberTextMuted,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "LEVEL $level",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = CyberGreenNeon,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyberCard,
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(CyberCyan, CyberPurple)))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "XP",
                            tint = CyberCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$xp XP",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyberText,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Academy Progress",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CyberText
                )
                Text(
                    text = "$completedCount/$totalLessons Completed",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = CyberGreenNeon,
                trackColor = CyberCard
            )
        }
    }
}

@Composable
fun PhaseSectionCard(
    phase: CoursePhase,
    completedLessons: List<CompletedLesson>,
    onLessonClick: (Lesson) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = phase.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = CyberCyan,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        phase.chapters.forEach { chapter ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .animateContentSize(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CyberGray),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column {
                    // Chapter Header row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = CyberCard
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = CourseData.getChapterIcon(chapter.iconName),
                                    contentDescription = chapter.title,
                                    tint = CyberGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = CyberText
                            )
                            Text(
                                text = chapter.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberTextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = CyberTextMuted
                        )
                    }

                    // Chapter lessons list (under chapter header when expanded)
                    AnimatedVisibility(visible = expanded) {
                        Column(
                            modifier = Modifier
                                .background(CyberCard)
                                .padding(vertical = 8.dp)
                        ) {
                            HorizontalDivider(color = CyberGray)
                            chapter.lessons.forEach { lesson ->
                                val isCompleted = completedLessons.any { it.lessonId == lesson.id }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onLessonClick(lesson) }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Status",
                                        tint = if (isCompleted) CyberGreenNeon else CyberTextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = lesson.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CyberText
                                        )
                                        Text(
                                            text = lesson.summary,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyberTextMuted
                                        )
                                    }

                                    Text(
                                        text = "+${lesson.xpReward} XP",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = CyberGreen,
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
}
