package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.CompletedLesson
import com.example.data.HackingRepository
import com.example.data.UserStats
import com.example.model.CourseData
import com.example.model.Lesson
import com.example.model.Quiz
import com.example.network.Content
import com.example.network.GenerationConfig
import com.example.network.GeminiRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TerminalLine(
    val text: String,
    val type: LineType
) {
    enum class LineType { INPUT, OUTPUT, ERROR, SUCCESS }
}

data class ChatMessage(
    val sender: String, // "user" or "aegis"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HackingViewModel(
    application: Application,
    private val repository: HackingRepository
) : AndroidViewModel(application) {

    // --- Database State ---
    val completedLessons: StateFlow<List<CompletedLesson>> = repository.completedLessons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userStats: StateFlow<UserStats?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserStats())

    // --- Active Course Selection ---
    var selectedLesson by mutableStateOf<Lesson?>(null)
        private set

    fun selectLesson(lesson: Lesson?) {
        selectedLesson = lesson
        // If the lesson has a terminal lab, seed the terminal with a friendly hint
        if (lesson != null && lesson.requiredLabCommand != null) {
            appendTerminalOutput("\n[SYSTEM] LAB ACCESS GRANTED: ${lesson.title}")
            appendTerminalOutput("Task: Type and execute: \"${lesson.requiredLabCommand}\" to complete the lab exercise.\n")
        }
    }

    // --- Interactive Quiz State ---
    var currentQuizQuestionIndex by mutableStateOf(0)
    var selectedAnswerIndex by mutableStateOf<Int?>(null)
    var quizCompleted by mutableStateOf(false)
    var quizScore by mutableStateOf(0)
    var isAnswerChecked by mutableStateOf(false)

    fun startQuiz(quiz: Quiz) {
        currentQuizQuestionIndex = 0
        selectedAnswerIndex = null
        quizCompleted = false
        quizScore = 0
        isAnswerChecked = false
    }

    fun submitAnswer(correctIndex: Int) {
        if (selectedAnswerIndex == null) return
        isAnswerChecked = true
        if (selectedAnswerIndex == correctIndex) {
            quizScore++
        }
    }

    fun nextQuizQuestion(quiz: Quiz, lessonId: String, xpReward: Int) {
        if (currentQuizQuestionIndex + 1 < quiz.questions.size) {
            currentQuizQuestionIndex++
            selectedAnswerIndex = null
            isAnswerChecked = false
        } else {
            quizCompleted = true
            // If the quiz score is passing (e.g. completed), mark the lesson as completed!
            viewModelScope.launch {
                val leveledUp = repository.markLessonCompleted(lessonId, xpReward)
                if (leveledUp) {
                    appendTerminalOutput("\n⭐ LEVEL UP! Check your Profile for unlocked badges. ⭐\n")
                }
            }
        }
    }

    // --- Interactive Terminal Lab State ---
    private val _terminalLogs = MutableStateFlow<List<TerminalLine>>(
        listOf(
            TerminalLine("=========================================", TerminalLine.LineType.OUTPUT),
            TerminalLine("     A E G I S   C Y B E R   L A B S     ", TerminalLine.LineType.OUTPUT),
            TerminalLine("=========================================", TerminalLine.LineType.OUTPUT),
            TerminalLine("System: Online. Sandbox environment ready.", TerminalLine.LineType.SUCCESS),
            TerminalLine("Type 'help' to see list of available toolsets.", TerminalLine.LineType.OUTPUT),
            TerminalLine("", TerminalLine.LineType.OUTPUT)
        )
    )
    val terminalLogs: StateFlow<List<TerminalLine>> = _terminalLogs.asStateFlow()

    fun executeTerminalCommand(rawInput: String) {
        val trimmedInput = rawInput.trim()
        if (trimmedInput.isEmpty()) return

        // 1. Log the user input line
        _terminalLogs.value = _terminalLogs.value + TerminalLine("root@kali:~# $trimmedInput", TerminalLine.LineType.INPUT)

        // 2. Process command
        val parts = trimmedInput.split(" ")
        val command = parts[0].lowercase()

        when (command) {
            "help" -> {
                appendTerminalOutput("""
                    Available commands in the Aegis security lab:
                      help                             - Display this command dictionary
                      clear                            - Flush terminal logs
                      pwd                              - Print working directory
                      ls                               - List directory contents
                      cat [filename]                   - Read/inspect contents of a file
                      whois [domain]                   - Query registry details
                      ping [host]                      - Send ICMP echo requests
                      nmap [flags] [target]            - Perform active network scans
                      hashcat [flags] [hash] [dict]    - Launch high-speed hash crack job
                """.trimIndent())
            }
            "clear" -> {
                _terminalLogs.value = emptyList()
            }
            "pwd" -> {
                appendTerminalOutput("/root/hacking_sandbox")
            }
            "ls" -> {
                appendTerminalOutput("secret_hash.txt\nconfig.json\nnmap_scan.xml")
            }
            "cat" -> {
                val arg = parts.getOrNull(1)
                if (arg == null) {
                    appendTerminalError("Usage: cat [filename]")
                } else {
                    when (arg) {
                        "secret_hash.txt" -> appendTerminalOutput("3f7041a7df756b1f9b882f093a206b1e")
                        "config.json" -> appendTerminalOutput("{\n  \"env\": \"sandbox\",\n  \"auth_enabled\": false,\n  \"db_version\": 1.4\n}")
                        "nmap_scan.xml" -> appendTerminalOutput("<nmaprun scanner=\"nmap\" args=\"nmap -sV target.local\" start=\"1688847060\">\n  <host><address addr=\"192.168.1.45\"/></host>\n</nmaprun>")
                        else -> appendTerminalError("cat: $arg: No such file or directory")
                    }
                }
            }
            "whois" -> {
                val arg = parts.getOrNull(1)
                if (arg == null) {
                    appendTerminalError("Usage: whois [domain]")
                } else {
                    appendTerminalOutput("""
                        Domain: ${arg.uppercase()}
                        Registrar: Aegis Security Registry LLC
                        Creation Date: 2012-04-18T10:00:00Z
                        Status: ok
                        Admin Contact: admin@$arg
                    """.trimIndent())
                }
            }
            "ping" -> {
                val arg = parts.getOrNull(1)
                if (arg == null) {
                    appendTerminalError("Usage: ping [host]")
                } else {
                    appendTerminalOutput("""
                        PING $arg (8.8.8.8) 56(84) bytes of data.
                        64 bytes from 8.8.8.8: icmp_seq=1 ttl=118 time=14.2 ms
                        64 bytes from 8.8.8.8: icmp_seq=2 ttl=118 time=15.1 ms
                        --- $arg ping statistics ---
                        2 packets transmitted, 2 received, 0% packet loss, time 1002ms
                    """.trimIndent())
                }
            }
            "nmap" -> {
                if (parts.size < 2) {
                    appendTerminalError("Usage: nmap [flags] [target]")
                } else {
                    val fullArgs = parts.subList(1, parts.size).joinToString(" ")
                    if (fullArgs.contains("-sV") && fullArgs.contains("target.local")) {
                        appendTerminalOutput("""
                            Starting Nmap 7.92 ( https://nmap.org ) at 2026-07-08 20:11 PDT
                            Nmap scan report for target.local (192.168.1.45)
                            Host is up (0.0042s latency).
                            Not shown: 996 closed tcp ports (reset)
                            PORT     STATE SERVICE VERSION
                            22/tcp   open  ssh     OpenSSH 8.2p1 (Ubuntu)
                            80/tcp   open  http    Apache httpd 2.4.41
                            443/tcp  open  ssl/http Apache httpd 2.4.41
                            8080/tcp open  http    Tomcat 9.0.31
                            
                            Service Info: OS: Linux; CPE: cpe:/o:linux:linux_kernel
                        """.trimIndent())
                    } else {
                        appendTerminalOutput("""
                            Starting Nmap 7.92 ( https://nmap.org )
                            Nmap scan report for ${parts.last()}
                            Host is up (0.015s latency).
                            All 1000 scanned ports on ${parts.last()} are in ignored states.
                            Not shown: 1000 closed tcp ports (reset)
                        """.trimIndent())
                    }
                }
            }
            "hashcat" -> {
                if (parts.size < 4) {
                    appendTerminalError("Usage: hashcat -m [type] [hashfile] [wordlist]")
                } else {
                    val mIndex = parts.indexOf("-m")
                    val type = if (mIndex != -1) parts.getOrNull(mIndex + 1) else null
                    if (type == "0" && trimmedInput.contains("hash.txt") && trimmedInput.contains("rockyou.txt")) {
                        appendTerminalOutput("""
                            hashcat (v6.2.5) starting in autodetect mode...
                            
                            3f7041a7df756b1f9b882f093a206b1e:password123
                            
                            Session..........: hashcat
                            Status...........: Cracked
                            Hash.Mode........: 0 (MD5)
                            Hash.Target......: 3f7041a7df756b1f9b882f093a206b1e
                            Time.Started.....: Wed Jul  8 20:11:15 2026 (0 secs)
                            Time.Estimated...: Wed Jul  8 20:11:15 2026 (0 secs)
                            Speed.#1.........: 14.5 MH/s (34.22ms)
                            Recovered........: 1/1 (100.00%) Digests
                        """.trimIndent())
                    } else {
                        appendTerminalError("hashcat error: Incorrect mode or files. Tip: Use \"hashcat -m 0 hash.txt rockyou.txt\" to crack MD5 hash.")
                    }
                }
            }
            else -> {
                appendTerminalError("bash: $command: command not found")
            }
        }

        // Check if this input completes the current lesson's lab requirement
        val activeLesson = selectedLesson
        if (activeLesson != null && activeLesson.requiredLabCommand != null) {
            if (trimmedInput.replace(" ", "").lowercase() == activeLesson.requiredLabCommand.replace(" ", "").lowercase()) {
                _terminalLogs.value = _terminalLogs.value + TerminalLine(
                    "🎉 LAB SUCCESS! Task complete: \"${activeLesson.requiredLabCommand}\".",
                    TerminalLine.LineType.SUCCESS
                )
                if (activeLesson.labSuccessOutput != null) {
                    appendTerminalOutput(activeLesson.labSuccessOutput)
                }

                // Complete the lesson and grant XP
                viewModelScope.launch {
                    val leveledUp = repository.markLessonCompleted(activeLesson.id, activeLesson.xpReward)
                    if (leveledUp) {
                        appendTerminalOutput("\n⭐ LEVEL UP! Check your Profile for unlocked badges. ⭐\n")
                    }
                }
            }
        }
    }

    private fun appendTerminalOutput(text: String) {
        _terminalLogs.value = _terminalLogs.value + TerminalLine(text, TerminalLine.LineType.OUTPUT)
    }

    private fun appendTerminalError(text: String) {
        _terminalLogs.value = _terminalLogs.value + TerminalLine(text, TerminalLine.LineType.ERROR)
    }

    // --- AI Mentor Chat State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("aegis", "Greetings! I am Aegis AI, your security mentor. Ask me any conceptual, cryptographic, or technical questions about ethical hacking. Let's make you an elite cybersecurity defender!")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    var isAiLoading by mutableStateOf(false)
        private set

    fun sendMessageToAi(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) return

        // 1. Add user message
        _chatMessages.value = _chatMessages.value + ChatMessage("user", trimmed)

        // 2. Request from Gemini
        isAiLoading = true
        viewModelScope.launch {
            try {
                val apiKey = BuildConfig.GEMINI_API_KEY
                
                // Formulate system prompt & context
                val systemPrompt = """
                    You are Aegis AI, a premium, elite ethical hacking mentor.
                    Your core purpose is to teach the user safe, authorized, and strictly legal ethical hacking and penetration testing techniques.
                    UNDER NO CIRCUMSTANCES should you provide executable exploit code targeting real-world live systems, or assist in illegal hacking operations. 
                    If a prompt requests illegal help, politely refuse and redirect them to defensive and legal alternatives.
                    Write response in concise, readable bullet points, using a clean technical style. Be encouraging, professional, and maintain a hacker aesthetic. Keep explanations easy to digest.
                """.trimIndent()

                // Compile history
                val contents = _chatMessages.value.map { msg ->
                    Content(parts = listOf(Part(text = "${if (msg.sender == "user") "User" else "Aegis"}: ${msg.text}")))
                }

                val request = GeminiRequest(
                    contents = contents,
                    systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
                    generationConfig = GenerationConfig(temperature = 0.7f)
                )

                val response = RetrofitClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                    ?: "Connection timed out. Please check your credentials or try again."

                _chatMessages.value = _chatMessages.value + ChatMessage("aegis", responseText)
            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    "aegis", 
                    "Error connecting to Aegis Core: ${e.localizedMessage ?: "Check your GEMINI_API_KEY in the Secrets panel."}"
                )
            } finally {
                isAiLoading = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage("aegis", "Session reset. Ask me anything to begin our next cybersecurity lab briefings!")
        )
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            repository.resetProgress()
            _terminalLogs.value = listOf(
                TerminalLine("=========================================", TerminalLine.LineType.OUTPUT),
                TerminalLine("     A E G I S   C Y B E R   L A B S     ", TerminalLine.LineType.OUTPUT),
                TerminalLine("=========================================", TerminalLine.LineType.OUTPUT),
                TerminalLine("System: Online. Sandbox environment ready.", TerminalLine.LineType.SUCCESS),
                TerminalLine("Type 'help' to see list of available toolsets.", TerminalLine.LineType.OUTPUT)
            )
            clearChat()
        }
    }

    init {
        viewModelScope.launch {
            repository.initializeDefaultStats()
        }
    }
}

class HackingViewModelFactory(
    private val application: Application,
    private val repository: HackingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HackingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HackingViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
