package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CyberColorScheme = darkColorScheme(
    primary = CyberGreen,
    secondary = CyberCyan,
    tertiary = CyberPurple,
    background = CyberBlack,
    surface = CyberGray,
    onPrimary = CyberBlack,
    onSecondary = CyberBlack,
    onTertiary = CyberText,
    onBackground = CyberText,
    onSurface = CyberText,
    error = CyberRed,
    errorContainer = CyberCard,
    onErrorContainer = CyberRed
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // Force dark cybersecurity theme by default for a immersive ethical hacking experience
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
