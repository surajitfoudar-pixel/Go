package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.data.HackingDatabase
import com.example.data.HackingRepository
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LessonDetailScreen
import com.example.ui.screens.MentorScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TerminalScreen
import com.example.ui.theme.CyberBlack
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreenNeon
import com.example.ui.theme.CyberGray
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.HackingViewModel
import com.example.viewmodel.HackingViewModelFactory

enum class Screen {
    Dashboard,
    LessonDetail,
    Terminal,
    Mentor,
    Profile
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database & Repository
        val database = HackingDatabase.getDatabase(this)
        val repository = HackingRepository(database)

        // Initialize ViewModel
        val viewModelFactory = HackingViewModelFactory(application, repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[HackingViewModel::class.java]

        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf(Screen.Dashboard) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = CyberBlack,
                    bottomBar = {
                        // Display bottom navigation only on root level screens (hide on Lesson Detail)
                        if (currentScreen != Screen.LessonDetail) {
                            NavigationBar(
                                containerColor = CyberGray,
                                tonalElevation = NavigationBarDefaults.Elevation
                            ) {
                                NavigationBarItem(
                                    selected = currentScreen == Screen.Dashboard,
                                    onClick = { currentScreen = Screen.Dashboard },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.School,
                                            contentDescription = "Curriculum"
                                        )
                                    },
                                    label = { Text("Curriculum") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CyberGreenNeon,
                                        selectedTextColor = CyberGreenNeon,
                                        indicatorColor = CyberCard,
                                        unselectedIconColor = CyberTextMuted,
                                        unselectedTextColor = CyberTextMuted
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentScreen == Screen.Terminal,
                                    onClick = { currentScreen = Screen.Terminal },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Terminal,
                                            contentDescription = "Terminal Lab"
                                        )
                                    },
                                    label = { Text("Lab Shell") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CyberGreenNeon,
                                        selectedTextColor = CyberGreenNeon,
                                        indicatorColor = CyberCard,
                                        unselectedIconColor = CyberTextMuted,
                                        unselectedTextColor = CyberTextMuted
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentScreen == Screen.Mentor,
                                    onClick = { currentScreen = Screen.Mentor },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.SupportAgent,
                                            contentDescription = "AI Mentor"
                                        )
                                    },
                                    label = { Text("Aegis AI") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CyberCyan,
                                        selectedTextColor = CyberCyan,
                                        indicatorColor = CyberCard,
                                        unselectedIconColor = CyberTextMuted,
                                        unselectedTextColor = CyberTextMuted
                                    )
                                )

                                NavigationBarItem(
                                    selected = currentScreen == Screen.Profile,
                                    onClick = { currentScreen = Screen.Profile },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = "Profile"
                                        )
                                    },
                                    label = { Text("Credentials") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = CyberCyan,
                                        selectedTextColor = CyberCyan,
                                        indicatorColor = CyberCard,
                                        unselectedIconColor = CyberTextMuted,
                                        unselectedTextColor = CyberTextMuted
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            Screen.Dashboard -> {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onLessonClick = { lesson ->
                                        viewModel.selectLesson(lesson)
                                        currentScreen = Screen.LessonDetail
                                    }
                                )
                            }
                            Screen.LessonDetail -> {
                                LessonDetailScreen(
                                    viewModel = viewModel,
                                    onBackClick = { currentScreen = Screen.Dashboard },
                                    onNavigateToTerminal = { currentScreen = Screen.Terminal }
                                )
                            }
                            Screen.Terminal -> {
                                TerminalScreen(viewModel = viewModel)
                            }
                            Screen.Mentor -> {
                                MentorScreen(viewModel = viewModel)
                            }
                            Screen.Profile -> {
                                ProfileScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
