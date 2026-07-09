package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Lesson
import com.example.model.Quiz
import com.example.ui.theme.*
import com.example.viewmodel.HackingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    viewModel: HackingViewModel,
    onBackClick: () -> Unit,
    onNavigateToTerminal: () -> Unit
) {
    val lesson = viewModel.selectedLesson
    val completedLessons by viewModel.completedLessons.collectAsState()

    if (lesson == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("No lesson selected", color = CyberText)
        }
        return
    }

    val isCompleted = completedLessons.any { it.lessonId == lesson.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        lesson.title,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyberText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CyberGreenNeon
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberBlack)
            )
        },
        containerColor = CyberBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // --- Completion status banner ---
            if (isCompleted) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = CyberGreen.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, CyberGreen)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = CyberGreenNeon,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "LESSON MASTERED",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyberGreenNeon,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "You have fully completed this unit and claimed the +${lesson.xpReward} XP reward.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberText
                            )
                        }
                    }
                }
            }

            // --- Markdown-like Content Renders ---
            Text(
                text = "STUDY GUIDE",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = CyberCyan,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Parse simple markdown block lines
            val lines = lesson.contentMarkdown.split("\n")
            lines.forEach { rawLine ->
                val line = rawLine.trim()
                if (line.startsWith("###")) {
                    Text(
                        text = line.substring(3).trim(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CyberGreenNeon,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else if (line.startsWith("####")) {
                    Text(
                        text = line.substring(4).trim(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else if (line.startsWith("*") || line.startsWith("-")) {
                    Row(modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)) {
                        Text("• ", color = CyberGreen, fontWeight = FontWeight.Bold)
                        Text(
                            text = line.substring(1).trim(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = CyberText
                        )
                    }
                } else if (line.isNotEmpty()) {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyberText,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Interactive Lab Trigger Card ---
            if (lesson.requiredLabCommand != null) {
                LessonLabCard(
                    command = lesson.requiredLabCommand,
                    onNavigateToTerminal = onNavigateToTerminal
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- Course Quiz Trigger Card ---
            if (lesson.quiz != null) {
                LessonQuizSection(
                    quiz = lesson.quiz,
                    lessonId = lesson.id,
                    xpReward = lesson.xpReward,
                    viewModel = viewModel
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun LessonLabCard(
    command: String,
    onNavigateToTerminal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberGray),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CyberCyan))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Lab Task",
                    tint = CyberCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PRACTICAL SEC-LAB REQUIREMENT",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Apply your knowledge by typing the following security diagnostic command inside our Cyber Terminal:",
                style = MaterialTheme.typography.bodyMedium,
                color = CyberText
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(CyberBlack)
                    .border(1.dp, CyberCard, RoundedCornerShape(6.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = command,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = TerminalGreen,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToTerminal,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = "Launch"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Launch Sandbox Terminal",
                    fontWeight = FontWeight.Bold,
                    color = CyberBlack
                )
            }
        }
    }
}

@Composable
fun LessonQuizSection(
    quiz: Quiz,
    lessonId: String,
    xpReward: Int,
    viewModel: HackingViewModel
) {
    val currentIndex = viewModel.currentQuizQuestionIndex
    val selectedIndex = viewModel.selectedAnswerIndex
    val isChecked = viewModel.isAnswerChecked
    val isCompleted = viewModel.quizCompleted
    val score = viewModel.quizScore

    LaunchedEffect(lessonId) {
        viewModel.startQuiz(quiz)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberGray),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isCompleted) CyberGreen else CyberPurple
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.School else Icons.Default.MenuBook,
                    contentDescription = "Quiz",
                    tint = if (isCompleted) CyberGreenNeon else CyberPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCompleted) "KNOWLEDGE EVALUATION COMPLETED" else "KNOWLEDGE CHECKPOINT",
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (isCompleted) CyberGreenNeon else CyberPurple
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isCompleted) {
                val currentQuestion = quiz.questions.getOrNull(currentIndex)
                if (currentQuestion != null) {
                    Text(
                        text = "Question ${currentIndex + 1} of ${quiz.questions.size}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = CyberTextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentQuestion.question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyberText
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Options list
                    currentQuestion.options.forEachIndexed { optIndex, optionText ->
                        val isSelected = selectedIndex == optIndex
                        
                        val optionBgColor by animateColorAsState(
                            targetValue = when {
                                isChecked && optIndex == currentQuestion.correctAnswerIndex -> CyberGreen.copy(alpha = 0.2f)
                                isChecked && isSelected && optIndex != currentQuestion.correctAnswerIndex -> CyberRed.copy(alpha = 0.2f)
                                isSelected -> CyberPurple.copy(alpha = 0.15f)
                                else -> CyberCard
                            }, label = "Bg"
                        )

                        val optionBorderColor by animateColorAsState(
                            targetValue = when {
                                isChecked && optIndex == currentQuestion.correctAnswerIndex -> CyberGreen
                                isChecked && isSelected && optIndex != currentQuestion.correctAnswerIndex -> CyberRed
                                isSelected -> CyberPurple
                                else -> Color.Transparent
                            }, label = "Border"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(optionBgColor)
                                .border(1.dp, optionBorderColor, RoundedCornerShape(8.dp))
                                .clickable(enabled = !isChecked) {
                                    viewModel.selectedAnswerIndex = optIndex
                                }
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${'A' + optIndex}. ",
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected) CyberPurple else CyberTextMuted
                                )
                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CyberText
                                )
                            }
                        }
                    }

                    // Explanation & Next Actions
                    AnimatedVisibility(visible = isChecked) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth()
                        ) {
                            HorizontalDivider(color = CyberCard, modifier = Modifier.padding(vertical = 8.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Explanation",
                                    tint = CyberCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = currentQuestion.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CyberTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isChecked) {
                        Button(
                            onClick = { viewModel.submitAnswer(currentQuestion.correctAnswerIndex) },
                            enabled = selectedIndex != null,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Validate Answer", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { viewModel.nextQuizQuestion(quiz, lessonId, xpReward) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (currentIndex + 1 < quiz.questions.size) "Next Question" else "Finalize Quiz",
                                fontWeight = FontWeight.Bold,
                                color = CyberBlack
                            )
                        }
                    }
                }
            } else {
                // Completed State
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Score: $score / ${quiz.questions.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = CyberGreenNeon,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Vulnerability evaluation successfully resolved! You have unlocked mastery for this lesson unit.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = CyberText,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = { viewModel.startQuiz(quiz) },
                        border = BorderStroke(1.dp, CyberPurple)
                    ) {
                        Text("Retake Knowledge Check", color = CyberPurple)
                    }
                }
            }
        }
    }
}
