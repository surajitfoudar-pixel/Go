package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.HackingViewModel
import com.example.viewmodel.TerminalLine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerminalScreen(viewModel: HackingViewModel) {
    val logs by viewModel.terminalLogs.collectAsState()
    var currentInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll terminal to the bottom whenever logs change
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Kali Lab",
                            tint = TerminalGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "KALI SEC-SANDBOX",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TerminalGreen
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
            // --- Info header / Active Lesson Lab Task ---
            viewModel.selectedLesson?.let { lesson ->
                if (lesson.requiredLabCommand != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = CyberGray),
                        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "ACTIVE OBJECTIVE:",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Execute: \"${lesson.requiredLabCommand}\" to claim unit XP.",
                                style = MaterialTheme.typography.bodySmall,
                                color = CyberText
                            )
                        }
                    }
                }
            }

            // --- Terminal Shell Area ---
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black, RoundedCornerShape(12.dp))
                    .border(1.dp, CyberCard, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(logs) { line ->
                        val textColor = when (line.type) {
                            TerminalLine.LineType.INPUT -> CyberCyan
                            TerminalLine.LineType.OUTPUT -> CyberText
                            TerminalLine.LineType.SUCCESS -> TerminalGreen
                            TerminalLine.LineType.ERROR -> CyberRed
                        }

                        Text(
                            text = line.text,
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                color = textColor
                            ),
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- Shell Input Row ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "root@kali:~# ",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TerminalGreen
                        )
                    )

                    BasicTextField(
                        value = currentInput,
                        onValueChange = { currentInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = CyberText
                        ),
                        cursorBrush = SolidColor(TerminalGreen),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (currentInput.isNotEmpty()) {
                                    viewModel.executeTerminalCommand(currentInput)
                                    currentInput = ""
                                }
                            }
                        )
                    )
                }
            }
        }
    }
}
