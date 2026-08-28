package com.example.data

/**
 * Diagnostic Archetypes generated after completing the onboarding questionnaire
 */
enum class UserArchetype(
    val title: String,
    val subtitle: String,
    val badge: String,
    val interventionLevel: String,
    val description: String,
    val primaryFocus: String,
    val quranVerse: String,
    val quranRef: String,
    val actionItems: List<String>
) {
    SHIELD_SEEKER(
        title = "The Shield Seeker",
        subtitle = "Digital Guard & Spiritual Fortress",
        badge = "🛡️ HIGH INTERVENTION",
        interventionLevel = "Strict",
        description = "Your biggest vulnerability is private isolation and late-night digital triggers. In Islam, guarding the gaze and fleeing temptation before it takes root is the highest form of Mujahadah (inner struggle).",
        primaryFocus = "Night-time App Lockdown & Clean Streak Multipliers",
        quranVerse = "Say, 'O My servants who have transgressed against themselves, do not despair of the mercy of Allah. Indeed, Allah forgives all sins.'",
        quranRef = "Surah Az-Zumar [39:53]",
        actionItems = listOf(
            "Activate Night Shield (Automatic lockdown between 10:30 PM - 5:00 AM)",
            "3x Bonus Points awarded for consecutive 'Clean Days' without trigger apps",
            "Emergency Panic Button with soothing Dhikr & Ayatul Kursi audio ready",
            "Focus on the Prophet's du'a: 'O Turner of hearts, keep my heart firm upon Your Deen'"
        )
    ),
    ANCHORLESS(
        title = "The Anchorless",
        subtitle = "Foundation of Salah First",
        badge = "⚓ FOUNDATION FIRST",
        interventionLevel = "Gentle & Focused",
        description = "When the five daily prayers slip, the soul loses its compass and feelings of guilt can become overwhelming. We strip away complex demands and focus entirely on your core lifeline: establishing Salah.",
        primaryFocus = "5 Daily Prayers with 20pt Fajr Boost",
        quranVerse = "Indeed, prayer prohibits immorality and wrongdoing, and the remembrance of Allah is greater.",
        quranRef = "Surah Al-Ankabut [29:45]",
        actionItems = listOf(
            "Fajr Recovery Mode: Double XP & 20 points for logging dawn prayer",
            "Non-overwhelming, minimalist quest dashboard",
            "Compassionate prayer time notification nudges 10 mins before Adhan",
            "Dhikr streak counter to build gentle daily momentum"
        )
    ),
    DISTRACTED(
        title = "The Distracted",
        subtitle = "Screen Time Mastery & Focus",
        badge = "⚡ MOMENTUM & FOCUS",
        interventionLevel = "Balanced",
        description = "Time slips away through mindless digital consumption and fluctuating consistency. You have the desire, but need environmental friction and gamified discipline to stay on the straight path.",
        primaryFocus = "Strict Daily App Time Limits & Islamic Daily Quests",
        quranVerse = "By time, indeed, mankind is in loss, except for those who have believed and done righteous deeds and advised each other to truth and patience.",
        quranRef = "Surah Al-Asr [103:1-3]",
        actionItems = listOf(
            "Enforce daily 45-minute limit on Social Media & Entertainment",
            "Earn XP by completing daily Adhkar, Quran reading & Sunnah deeds",
            "Leaderboard progress tracking to keep motivation vibrant",
            "Focused 25-minute Pomodoro sessions with Barakah timer"
        )
    ),
    IHSAN_STRIVER(
        title = "The Ihsan Striver",
        subtitle = "Path to Spiritual Excellence",
        badge = "✨ HIGHER EXCELLENCE",
        interventionLevel = "Advanced",
        description = "You have established your spiritual baseline and are seeking to elevate your worship to Ihsan—worshipping Allah as though you see Him, and knowing that even if you see Him not, He sees you.",
        primaryFocus = "Tahajjud, Zakat/Sadaqah & Deep Quranic Reflection",
        quranVerse = "And whoever fears Allah - He will make for him a way out and will provide for him from where he does not expect.",
        quranRef = "Surah At-Talaq [65:2-3]",
        actionItems = listOf(
            "Tahajjud & Duha prayer trackers with bonus rewards",
            "Zakat & Sadaqah direct impact logger with multiplier points",
            "Daily Quran Surah reflection challenges",
            "Community good deeds and mentorship coaching"
        )
    )
}

/**
 * Diagnostic Questionnaire Item
 */
data class DiagnosticQuestion(
    val id: Int,
    val phaseNumber: Int,
    val phaseTitle: String,
    val questionText: String,
    val subtitle: String,
    val options: List<DiagnosticOption>
)

data class DiagnosticOption(
    val key: String, // "A", "B", "C", "D", "E"
    val label: String,
    val description: String = "",
    val isCustomInput: Boolean = false
)

/**
 * Complete Diagnostic Questionnaire structure matching user specification
 */
object DiagnosticQuestionnaireData {
    val questions = listOf(
        // ==========================================
        // PHASE 1: THE SPIRITUAL BASELINE
        // ==========================================
        DiagnosticQuestion(
            id = 1,
            phaseNumber = 1,
            phaseTitle = "The Spiritual Baseline",
            questionText = "How would you describe your current connection with Allah?",
            subtitle = "Establishing your honest baseline without judgment",
            options = listOf(
                DiagnosticOption("A", "Strong, but I want to reach a higher level of Ihsan (excellence).", "Seeking deeper devotion, Tahajjud, and spiritual mastery"),
                DiagnosticOption("B", "I am trying, but my consistency fluctuates wildly.", "Good days and bad days; struggling to stay regular"),
                DiagnosticOption("C", "I feel disconnected and overwhelmed by guilt.", "Feeling burdened by past mistakes and seeking a fresh start"),
                DiagnosticOption("D", "I am starting from zero and need basic guidance.", "Rebuilding faith step-by-step from the foundation"),
                DiagnosticOption("E", "Something else...", "Type your own personal situation", isCustomInput = true)
            )
        ),
        DiagnosticQuestion(
            id = 2,
            phaseNumber = 1,
            phaseTitle = "The Spiritual Baseline",
            questionText = "What does your daily Salah (prayer) look like right now?",
            subtitle = "Salah is your direct lifeline to Allah",
            options = listOf(
                DiagnosticOption("A", "I pray all five on time.", "Consistent in congregation or at home"),
                DiagnosticOption("B", "I pray, but often delay them or miss Fajr.", "Struggling with waking up for dawn or evening delays"),
                DiagnosticOption("C", "I only pray occasionally (like Jummah).", "Praying sporadically or during Friday prayers"),
                DiagnosticOption("D", "I struggle to pray at all right now.", "Difficulty finding the motivation to stand in prayer"),
                DiagnosticOption("E", "Something else...", "Type your prayer routine or struggle", isCustomInput = true)
            )
        ),

        // ==========================================
        // PHASE 2: THE BATTLEGROUND (Triggers & Sins)
        // ==========================================
        DiagnosticQuestion(
            id = 3,
            phaseNumber = 2,
            phaseTitle = "The Battleground (Triggers & Habits)",
            questionText = "When it comes to your digital life and screen time, what is your biggest struggle?",
            subtitle = "Pinpointing digital pathways respecting Sitr (concealment)",
            options = listOf(
                DiagnosticOption("A", "Wasting too much time on useless entertainment.", "Doomscrolling, endless reels, and unproductive hours"),
                DiagnosticOption("B", "Struggling to lower my gaze when inappropriate content appears.", "Accidental or algorithm-fed triggers on social feeds"),
                DiagnosticOption("C", "Falling into private, hidden sins when I am alone with my devices.", "Triggers the need for strict app blocker & night shield"),
                DiagnosticOption("D", "I have good control over my digital environment.", "Looking to optimize focus and productivity further"),
                DiagnosticOption("E", "Something else...", "Type what challenges you digitally", isCustomInput = true)
            )
        ),
        DiagnosticQuestion(
            id = 4,
            phaseNumber = 2,
            phaseTitle = "The Battleground (Triggers & Habits)",
            questionText = "When do you find yourself most likely to slip into bad habits or sins?",
            subtitle = "Identifying the environmental and emotional triggers",
            options = listOf(
                DiagnosticOption("A", "Late at night when I am alone in my room.", "Isolation and fatigue weakening willpower"),
                DiagnosticOption("B", "When I am feeling stressed, anxious, or sad.", "Using dopamine and screens as emotional escape"),
                DiagnosticOption("C", "When I am around certain friends or environments.", "Social pressure or unhelpful surroundings"),
                DiagnosticOption("D", "When I let my daily prayers slip, everything else falls apart.", "Spiritual vulnerability following missed prayers"),
                DiagnosticOption("E", "Something else...", "Type when you feel most vulnerable", isCustomInput = true)
            )
        ),
        DiagnosticQuestion(
            id = 5,
            phaseNumber = 2,
            phaseTitle = "The Battleground (Triggers & Habits)",
            questionText = "What is the main thing holding you back from the person you want to become?",
            subtitle = "Understanding your primary internal or external obstacle",
            options = listOf(
                DiagnosticOption("A", "My environment and the people around me.", "Negative influences or lack of supportive Muslim peers"),
                DiagnosticOption("B", "My own desires and lack of self-control.", "Impulses overcoming logical and spiritual intentions"),
                DiagnosticOption("C", "A lack of Islamic knowledge and understanding.", "Wanting to learn the wisdom behind rulings and habits"),
                DiagnosticOption("D", "I just keep forgetting my goals and losing motivation.", "Inconsistent discipline needing daily reminders"),
                DiagnosticOption("E", "Something else...", "Type what is holding you back", isCustomInput = true)
            )
        ),

        // ==========================================
        // PHASE 3: THE VISION
        // ==========================================
        DiagnosticQuestion(
            id = 6,
            phaseNumber = 3,
            phaseTitle = "The Vision & Transformation",
            questionText = "If this app could guarantee you one change in 30 days, what would you want it to be?",
            subtitle = "Defining your primary measurable transformation milestone",
            options = listOf(
                DiagnosticOption("A", "Unbreakable consistency in my five daily prayers.", "Never missing a prayer and experiencing Khushu"),
                DiagnosticOption("B", "Finally breaking free from a destructive hidden habit.", "Attaining pure clean streaks and peace of heart"),
                DiagnosticOption("C", "Gaining a deeper, peaceful understanding of Islam.", "Connecting knowledge with daily character and calmness"),
                DiagnosticOption("D", "Building a lifestyle of constant good deeds and charity.", "Active Sadaqah, helping others, and barakah"),
                DiagnosticOption("E", "Something else...", "Type your dream 30-day spiritual goal", isCustomInput = true)
            )
        )
    )
}
