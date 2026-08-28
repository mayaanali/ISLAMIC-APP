package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PointLedgerEntry
import com.example.data.SettingsFolder
import com.example.data.UserArchetype
import com.example.ui.theme.AlabasterSand
import com.example.ui.theme.DesertGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RubyRed
import com.example.ui.theme.SlateBlue
import com.example.ui.viewmodel.MainViewModel
import com.example.utils.PermissionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToOnboarding: () -> Unit = {},
    onNavigateToApps: () -> Unit = {},
    onNavigateToPermissionsWizard: () -> Unit = {}
) {
    val context = LocalContext.current
    val isDark by viewModel.isDarkMode.collectAsState()

    // Profile States
    val userName by viewModel.userName.collectAsState()
    val userHandle by viewModel.userHandle.collectAsState()
    val userBio by viewModel.userBio.collectAsState()
    val userArchetype by viewModel.userArchetype.collectAsState()
    val coins by viewModel.coins.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val purityIndex by viewModel.purityIndex.collectAsState()
    val permissions by viewModel.permissions.collectAsState()

    // Preferences & Settings States
    val journeyPersonaMode by viewModel.journeyPersonaMode.collectAsState()
    val calculationMethod by viewModel.calculationMethodName.collectAsState()
    val asrSchool by viewModel.asrJuristicSchool.collectAsState()
    val prayerSound by viewModel.prayerNotificationSound.collectAsState()
    val prePrayerMins by viewModel.prePrayerReminderMinutes.collectAsState()
    val isQiblaCalibrated by viewModel.isQiblaCalibrated.collectAsState()

    val frictionRule by viewModel.frictionCooldownRule.collectAsState()
    val discreteSitrMode by viewModel.discreteSitrMode.collectAsState()
    val nightShieldActive by viewModel.nightShieldActive.collectAsState()

    val isAnonMode by viewModel.leaderboardAnonymousMode.collectAsState()
    val leaderAlias by viewModel.leaderboardAlias.collectAsState()
    val streakFreezeCount by viewModel.streakFreezeCount.collectAsState()
    val streakFreezeActive by viewModel.streakFreezeActive.collectAsState()
    val pointLedger by viewModel.pointLedger.collectAsState()

    val themeAccent by viewModel.themeAccent.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val defaultReciter by viewModel.defaultReciter.collectAsState()
    val hapticEnabled by viewModel.hapticFeedbackEnabled.collectAsState()
    val soundEffectsEnabled by viewModel.soundEffectsEnabled.collectAsState()

    // Dialog & Sheet States
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showLedgerSheet by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showClearLogsDialog by remember { mutableStateOf(false) }

    // Folder Expansion States
    var expandedFolder by remember { mutableStateOf<SettingsFolder?>(SettingsFolder.PERMISSIONS) }

    val bgColor = if (isDark) Color(0xFF080C14) else AlabasterSand
    val cardBg = if (isDark) Color(0xFF131B2E) else Color.White
    val cardBorder = if (isDark) Color(0xFF23324D) else Color(0xFFE2E8F0)
    val textPrimary = if (isDark) Color(0xFFF8FAFC) else SlateBlue
    val textSecondary = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Account & Settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Spiritual Profile & System Preferences",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode",
                            tint = if (isDark) DesertGold else SlateBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = bgColor
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ==========================================
            // 1. HERO PROFILE CARD (Customizable & Sitr-Protected)
            // ==========================================
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(cardBorder, DesertGold.copy(alpha = 0.3f))
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Pill with Glow
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(EmeraldGreen, Color(0xFF0F766E))
                                        )
                                    )
                                    .border(2.dp, DesertGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val initials = userName.split(" ")
                                    .mapNotNull { it.firstOrNull()?.toString() }
                                    .take(2)
                                    .joinToString("")
                                    .ifEmpty { "JD" }
                                Text(
                                    text = initials,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Name, Handle & Bio
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (discreteSitrMode) "Seeker of Forgiveness" else userName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = if (discreteSitrMode) "@protected_soul" else userHandle,
                                    fontSize = 13.sp,
                                    color = DesertGold,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = userBio,
                                    fontSize = 12.sp,
                                    color = textSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 16.sp
                                )
                            }

                            IconButton(
                                onClick = { showEditProfileDialog = true },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = cardBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Vitality Metrics Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ProfileMetricBadge(
                                icon = "🔥",
                                value = "$streakDays d",
                                label = "Pure Streak",
                                isDark = isDark
                            )
                            ProfileMetricBadge(
                                icon = "💖",
                                value = "$purityIndex%",
                                label = "Purity Index",
                                isDark = isDark
                            )
                            ProfileMetricBadge(
                                icon = "🪙",
                                value = "$coins",
                                label = "Deen XP",
                                isDark = isDark
                            )
                            ProfileMetricBadge(
                                icon = "🛡️",
                                value = userArchetype.title.split(" ").firstOrNull() ?: "Shield",
                                label = "Archetype",
                                isDark = isDark
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 2. FOLDERS / ACCORDION SECTIONS
            // ==========================================
            items(SettingsFolder.values()) { folder ->
                SettingsFolderCard(
                    folder = folder,
                    isExpanded = expandedFolder == folder,
                    onToggleExpand = {
                        expandedFolder = if (expandedFolder == folder) null else folder
                    },
                    isDark = isDark,
                    cardBg = cardBg,
                    cardBorder = cardBorder,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    content = {
                        when (folder) {
                            SettingsFolder.PERMISSIONS -> {
                                PermissionsFolderContent(
                                    viewModel = viewModel,
                                    permissions = permissions,
                                    isDark = isDark,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    onOpenWizard = onNavigateToPermissionsWizard
                                )
                            }
                            SettingsFolder.THEME -> {
                                ThemeFolderContent(
                                    viewModel = viewModel,
                                    isDark = isDark,
                                    themeAccent = themeAccent,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary
                                )
                            }
                            SettingsFolder.SPIRITUAL_CALCULATION -> {
                                SpiritualCalculationFolderContent(
                                    viewModel = viewModel,
                                    calculationMethod = calculationMethod,
                                    asrSchool = asrSchool,
                                    prayerSound = prayerSound,
                                    prePrayerMins = prePrayerMins,
                                    isQiblaCalibrated = isQiblaCalibrated,
                                    isDark = isDark,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary
                                )
                            }
                            SettingsFolder.SHIELD_AND_FOCUS -> {
                                ShieldFocusFolderContent(
                                    viewModel = viewModel,
                                    frictionRule = frictionRule,
                                    discreteSitrMode = discreteSitrMode,
                                    nightShieldActive = nightShieldActive,
                                    isDark = isDark,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    onManageBlockedApps = onNavigateToApps
                                )
                            }
                            SettingsFolder.GAMIFICATION_LEADERBOARD -> {
                                GamificationFolderContent(
                                    viewModel = viewModel,
                                    isAnonMode = isAnonMode,
                                    leaderAlias = leaderAlias,
                                    streakFreezeCount = streakFreezeCount,
                                    streakFreezeActive = streakFreezeActive,
                                    isDark = isDark,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    onOpenLedger = { showLedgerSheet = true }
                                )
                            }
                            SettingsFolder.ACCOUNT_JOURNEY_PERSONA -> {
                                PersonaFolderContent(
                                    viewModel = viewModel,
                                    journeyPersonaMode = journeyPersonaMode,
                                    archetype = userArchetype,
                                    isDark = isDark,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary,
                                    onRetakeDiagnostic = {
                                        viewModel.restartOnboarding()
                                        onNavigateToOnboarding()
                                    },
                                    onClearLogs = { showClearLogsDialog = true },
                                    onExportData = { showExportDialog = true },
                                    onResetProfile = { showResetDialog = true }
                                )
                            }
                            SettingsFolder.APP_PREFERENCES -> {
                                AppPreferencesFolderContent(
                                    viewModel = viewModel,
                                    selectedLanguage = selectedLanguage,
                                    defaultReciter = defaultReciter,
                                    hapticEnabled = hapticEnabled,
                                    soundEffectsEnabled = soundEffectsEnabled,
                                    isDark = isDark,
                                    textPrimary = textPrimary,
                                    textSecondary = textSecondary
                                )
                            }
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nour Deen Shield • v2.4.0 (Engine Active)",
                        fontSize = 12.sp,
                        color = textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Built with Sitr & Spiritual Guidance",
                        fontSize = 11.sp,
                        color = DesertGold
                    )
                }
            }
        }
    }

    // ==========================================
    // DIALOGS & BOTTOM SHEETS
    // ==========================================

    // 1. Edit Profile Dialog
    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(userName) }
        var tempHandle by remember { mutableStateOf(userHandle) }
        var tempBio by remember { mutableStateOf(userBio) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "Edit Profile & Intention",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = textPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempHandle,
                        onValueChange = { tempHandle = it },
                        label = { Text("Spiritual Handle") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempBio,
                        onValueChange = { tempBio = it },
                        label = { Text("Bio / Spiritual Intention") },
                        minLines = 2,
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserProfile(tempName, tempHandle, tempBio, "avatar_1")
                        showEditProfileDialog = false
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Text("Save Changes", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = textSecondary)
                }
            },
            containerColor = cardBg
        )
    }

    // 2. Point Ledger & History Bottom Sheet
    if (showLedgerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLedgerSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = cardBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 30.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Point Ledger & History",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Full record of Deen XP earned & spent",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }

                    Surface(
                        color = DesertGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🪙 $coins Total",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DesertGold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(pointLedger) { entry ->
                        PointLedgerItemRow(
                            entry = entry,
                            isDark = isDark,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }
            }
        }
    }

    // 3. Clear Reflection Logs Confirmation Dialog
    if (showClearLogsDialog) {
        AlertDialog(
            onDismissRequest = { showClearLogsDialog = false },
            title = { Text("Clear Reflection Logs?", fontWeight = FontWeight.Bold, color = textPrimary) },
            text = {
                Text(
                    text = "This will permanently erase your diagnostic reflection entries and private urges notes in compliance with Islamic Sitr privacy.",
                    color = textSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearPersonalReflectionLogs()
                        showClearLogsDialog = false
                        Toast.makeText(context, "Reflection logs cleared with Sitr.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
                ) {
                    Text("Clear All Logs", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearLogsDialog = false }) {
                    Text("Cancel", color = textSecondary)
                }
            },
            containerColor = cardBg
        )
    }

    // 4. Export Data Dialog
    if (showExportDialog) {
        val summary = remember { viewModel.exportUserDataSummary() }
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Exported Deen Summary", fontWeight = FontWeight.Bold, color = textPrimary) },
            text = {
                Column {
                    Text(
                        text = "Your spiritual statistics & active settings summary:",
                        fontSize = 13.sp,
                        color = textSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = summary,
                            fontSize = 12.sp,
                            color = textPrimary,
                            modifier = Modifier.padding(12.dp),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, summary)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Export Nour Deen Summary")
                        context.startActivity(shareIntent)
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Text("Share / Copy", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close", color = textSecondary)
                }
            },
            containerColor = cardBg
        )
    }

    // 5. Reset Profile Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Account Profile?", fontWeight = FontWeight.Bold, color = textPrimary) },
            text = {
                Text(
                    text = "This will restore default profile values and reset streaks. Are you sure you want to proceed?",
                    color = textSecondary,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAccountProfile()
                        showResetDialog = false
                        Toast.makeText(context, "Profile reset to defaults.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
                ) {
                    Text("Reset Profile", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = textSecondary)
                }
            },
            containerColor = cardBg
        )
    }
}

// ==========================================
// ACCORDION FOLDER CARD COMPONENT
// ==========================================
@Composable
fun SettingsFolderCard(
    folder: SettingsFolder,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    isDark: Boolean,
    cardBg: Color,
    cardBorder: Color,
    textPrimary: Color,
    textSecondary: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                listOf(cardBorder, cardBorder.copy(alpha = 0.5f))
            )
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = folder.iconName, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = folder.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            if (folder.badge != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = EmeraldGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = folder.badge,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = folder.subtitle,
                            fontSize = 11.sp,
                            color = textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = textSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    HorizontalDivider(color = cardBorder)
                    Spacer(modifier = Modifier.height(14.dp))
                    content()
                }
            }
        }
    }
}

// ==========================================
// 1. PERMISSIONS FOLDER CONTENT
// ==========================================
@Composable
fun PermissionsFolderContent(
    viewModel: MainViewModel,
    permissions: com.example.ui.viewmodel.PermissionStatus,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    onOpenWizard: () -> Unit
) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Summary & Status Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bundled System Engine Status",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )

            val statusText = "${permissions.coreGrantedCount} / ${permissions.totalCount} Granted"
            val statusColor = if (permissions.allGranted) EmeraldGreen else DesertGold

            Surface(
                color = statusColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Text(
            text = "Nour Deen requires these system permissions to track screen time, instantly block forbidden triggers, and broadcast Adhan reminders.",
            fontSize = 12.sp,
            color = textSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Individual Permission Rows
        PermissionToggleRow(
            title = "Usage Access (Screen Time)",
            description = "Tracks app usage and triggers daily limits",
            isGranted = permissions.hasUsageStats,
            onClick = { PermissionManager.openUsageStatsSettings(context) },
            isDark = isDark,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        PermissionToggleRow(
            title = "Display Over Other Apps (Overlay)",
            description = "Shows full-screen blocker when limits are reached",
            isGranted = permissions.hasOverlay,
            onClick = { PermissionManager.openOverlaySettings(context) },
            isDark = isDark,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        PermissionToggleRow(
            title = "Accessibility Service (Shield)",
            description = "Real-time instant app launch interception",
            isGranted = permissions.hasAccessibility,
            onClick = { PermissionManager.openAccessibilitySettings(context) },
            isDark = isDark,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        PermissionToggleRow(
            title = "Ignore Battery Optimization",
            description = "Keeps foreground monitoring service persistent",
            isGranted = permissions.isIgnoringBattery,
            onClick = { PermissionManager.openBatteryOptimizationSettings(context) },
            isDark = isDark,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        PermissionToggleRow(
            title = "Location Permission",
            description = "Calculates accurate prayer times & Qibla compass",
            isGranted = permissions.hasLocation,
            onClick = { PermissionManager.openLocationSettings(context) },
            isDark = isDark,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        PermissionToggleRow(
            title = "Notification Permission",
            description = "Broadcasts Adhan calls & prayer countdown alerts",
            isGranted = permissions.hasNotification,
            onClick = { PermissionManager.openNotificationSettings(context) },
            isDark = isDark,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onOpenWizard,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Open Step-by-Step Permission Wizard", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun PermissionToggleRow(
    title: String,
    description: String,
    isGranted: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color
) {
    val rowBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val checkColor = if (isGranted) EmeraldGreen else Color(0xFF94A3B8)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(rowBg)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = textSecondary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            color = checkColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Tune,
                    contentDescription = null,
                    tint = checkColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isGranted) "Active" else "Enable",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = checkColor
                )
            }
        }
    }
}

// ==========================================
// 2. THEME & APPEARANCE FOLDER CONTENT
// ==========================================
@Composable
fun ThemeFolderContent(
    viewModel: MainViewModel,
    isDark: Boolean,
    themeAccent: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Dark Mode Toggle Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Dark Mode Canvas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = if (isDark) "Active (OLED Deep Slate)" else "Light (Alabaster Sand)",
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }

            Switch(
                checked = isDark,
                onCheckedChange = { viewModel.toggleDarkMode() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EmeraldGreen
                )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Accent Color Selection
        Text(
            text = "Spiritual Accent Palette",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )

        val accents = listOf(
            Triple("Emerald Green", EmeraldGreen, "Islamic Harmony & Vitality"),
            Triple("Desert Gold", DesertGold, "Makkah Minaret Radiance"),
            Triple("Sapphire Slate", Color(0xFF3B82F6), "Calm & Reflective Focus"),
            Triple("Ruby Red", RubyRed, "Strict Shield Protection")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            accents.forEach { (name, color, _) ->
                val isSelected = themeAccent == name
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) color else color.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { viewModel.setThemeAccent(name) },
                    color = if (isSelected) color.copy(alpha = 0.15f) else Color.Transparent
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(color, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = name.split(" ").firstOrNull() ?: name,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = textPrimary
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. SPIRITUAL & PRAYER CALCULATION CONTENT
// ==========================================
@Composable
fun SpiritualCalculationFolderContent(
    viewModel: MainViewModel,
    calculationMethod: String,
    asrSchool: String,
    prayerSound: String,
    prePrayerMins: Int,
    isQiblaCalibrated: Boolean,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color
) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Calculation Method Selector
        Text(
            text = "Calculation Method",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )

        val methods = listOf(
            "Muslim World League (MWL)",
            "Islamic Society of North America (ISNA)",
            "Umm Al-Qura, Makkah",
            "Univ. of Islamic Sciences, Karachi",
            "Egyptian General Authority"
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            methods.forEach { method ->
                val isSelected = calculationMethod.contains(method.split(" ").first()) || calculationMethod == method
                OptionSelectRow(
                    title = method,
                    isSelected = isSelected,
                    onClick = { viewModel.setCalculationMethodByName(method) },
                    isDark = isDark,
                    textPrimary = textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Asr Juristic School
        Text(
            text = "Asr Juristic Calculation",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val standardSelected = asrSchool.contains("Standard", ignoreCase = true)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (standardSelected) EmeraldGreen else Color.Gray.copy(alpha = 0.3f),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.setAsrJuristicSchool("Standard (Shafi'i, Maliki, Hanbali)") },
                color = if (standardSelected) EmeraldGreen.copy(alpha = 0.15f) else Color.Transparent
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Standard (1x Shadow)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (standardSelected) EmeraldGreen else textPrimary
                    )
                    Text(
                        text = "Shafi'i, Maliki, Hanbali",
                        fontSize = 10.sp,
                        color = textSecondary
                    )
                }
            }

            val hanafiSelected = asrSchool.contains("Hanafi", ignoreCase = true)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        1.dp,
                        if (hanafiSelected) EmeraldGreen else Color.Gray.copy(alpha = 0.3f),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { viewModel.setAsrJuristicSchool("Hanafi") },
                color = if (hanafiSelected) EmeraldGreen.copy(alpha = 0.15f) else Color.Transparent
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Hanafi (2x Shadow)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hanafiSelected) EmeraldGreen else textPrimary
                    )
                    Text(
                        text = "Later Asr calculation",
                        fontSize = 10.sp,
                        color = textSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Prayer Notifications Sound
        Text(
            text = "Prayer Adhan Audio & Reminders",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )

        val sounds = listOf("Full Makkah Adhan", "Madinah Adhan", "Short Takbeer", "Silent Nudge (Vibrate)")
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            sounds.forEach { sound ->
                OptionSelectRow(
                    title = sound,
                    isSelected = prayerSound == sound,
                    onClick = { viewModel.setPrayerNotificationSound(sound) },
                    isDark = isDark,
                    textPrimary = textPrimary
                )
            }
        }

        // Pre-Prayer Reminder (Minutes)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pre-Prayer Reminder",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = "$prePrayerMins minutes before Iqamah",
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(5, 10, 15, 20).forEach { mins ->
                    val isSel = prePrayerMins == mins
                    Surface(
                        color = if (isSel) EmeraldGreen else Color.Gray.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.clickable { viewModel.setPrePrayerReminderMinutes(mins) }
                    ) {
                        Text(
                            text = "${mins}m",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else textPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Qibla Compass Calibration
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Qibla Compass Sensor",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = if (isQiblaCalibrated) "Calibrated with High Accuracy" else "Recalibration Recommended",
                    fontSize = 11.sp,
                    color = if (isQiblaCalibrated) EmeraldGreen else DesertGold
                )
            }

            OutlinedButton(
                onClick = {
                    viewModel.calibrateQibla()
                    Toast.makeText(context, "Compass sensor calibrated!", Toast.LENGTH_SHORT).show()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Recalibrate", fontSize = 11.sp)
            }
        }
    }
}

// ==========================================
// 4. SHIELD & FOCUS FOLDER CONTENT
// ==========================================
@Composable
fun ShieldFocusFolderContent(
    viewModel: MainViewModel,
    frictionRule: String,
    discreteSitrMode: Boolean,
    nightShieldActive: Boolean,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    onManageBlockedApps: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Blocklist Management Shortcut
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Blocklist & App Shield",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = "Restrict social media, browsers, and adult triggers during focus windows",
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }

            Button(
                onClick = onManageBlockedApps,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Manage Apps", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Friction & Cooldown Rules
        Text(
            text = "Friction & Unlock Bypass Rules",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )
        Text(
            text = "Set strict conditions if tempted to bypass a block during vulnerable moments:",
            fontSize = 11.sp,
            color = textSecondary
        )

        val frictionRules = listOf(
            "5-Minute Waiting Timer",
            "Type Reflection Verse / Istighfar",
            "20-Second Breathing Pause",
            "Strict No-Bypass Lockdown"
        )

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            frictionRules.forEach { rule ->
                OptionSelectRow(
                    title = rule,
                    isSelected = frictionRule == rule,
                    onClick = { viewModel.setFrictionCooldownRule(rule) },
                    isDark = isDark,
                    textPrimary = textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Discrete Sitr Mode Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Discrete / Sitr Mode",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = DesertGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "PRIVACY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = DesertGold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Conceals private sin struggles, triggers, and personal logs if someone looks at your phone",
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }

            Switch(
                checked = discreteSitrMode,
                onCheckedChange = { viewModel.toggleDiscreteSitrMode(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EmeraldGreen
                )
            )
        }

        // Night Shield Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Night Shield (10:30 PM - 5:00 AM)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = "High-friction lockdown during late-night hours when temptation peaks",
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }

            Switch(
                checked = nightShieldActive,
                onCheckedChange = { viewModel.toggleNightShield(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EmeraldGreen
                )
            )
        }
    }
}

// ==========================================
// 5. GAMIFICATION & LEADERBOARD FOLDER
// ==========================================
@Composable
fun GamificationFolderContent(
    viewModel: MainViewModel,
    isAnonMode: Boolean,
    leaderAlias: String,
    streakFreezeCount: Int,
    streakFreezeActive: Boolean,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    onOpenLedger: () -> Unit
) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Leaderboard Anonymous Mode
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Leaderboard Anonymous Mode",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = if (isAnonMode) "Displaying as $leaderAlias" else "Displaying your real display name",
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }

            Switch(
                checked = isAnonMode,
                onCheckedChange = { viewModel.toggleLeaderboardAnonymous(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EmeraldGreen
                )
            )
        }

        // Point Ledger & History Shortcut
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { onOpenLedger() },
            color = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.History, contentDescription = null, tint = DesertGold)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("View Point Ledger & History", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                        Text("Detailed breakdown of +5 Salah, +100 Zakat, and Marketplace spend", fontSize = 11.sp, color = textSecondary)
                    }
                }
                Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = textSecondary)
            }
        }

        // Streak Protection & Freezes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Streak Freeze Protection",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = "Available: $streakFreezeCount freezes • ${if (streakFreezeActive) "Active for next miss" else "Inactive"}",
                    fontSize = 11.sp,
                    color = if (streakFreezeActive) EmeraldGreen else textSecondary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedButton(
                    onClick = {
                        val used = viewModel.useStreakFreeze()
                        if (used) Toast.makeText(context, "Streak freeze equipped!", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(context, "No freeze available or already active", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Equip", fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        val bought = viewModel.buyStreakFreeze(150)
                        if (bought) Toast.makeText(context, "Purchased Streak Freeze (+1)", Toast.LENGTH_SHORT).show()
                        else Toast.makeText(context, "Need 150 coins to purchase", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DesertGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+1 (150🪙)", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

// ==========================================
// 6. ACCOUNT & JOURNEY PERSONA FOLDER
// ==========================================
@Composable
fun PersonaFolderContent(
    viewModel: MainViewModel,
    journeyPersonaMode: String,
    archetype: UserArchetype,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    onRetakeDiagnostic: () -> Unit,
    onClearLogs: () -> Unit,
    onExportData: () -> Unit,
    onResetProfile: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Mode Switcher (Muslim vs Seeker)
        Text(
            text = "Journey Persona Mode",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val isMuslim = journeyPersonaMode.contains("Muslim", ignoreCase = true)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, if (isMuslim) EmeraldGreen else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .clickable { viewModel.setJourneyPersonaMode("Muslim (Strengthening Deen)") },
                color = if (isMuslim) EmeraldGreen.copy(alpha = 0.15f) else Color.Transparent
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Muslim", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isMuslim) EmeraldGreen else textPrimary)
                    Text("Strengthening Deen & Ihsan", fontSize = 10.sp, color = textSecondary)
                }
            }

            val isSeeker = journeyPersonaMode.contains("Seeker", ignoreCase = true)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, if (isSeeker) EmeraldGreen else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .clickable { viewModel.setJourneyPersonaMode("Seeker (Exploring Islam)") },
                color = if (isSeeker) EmeraldGreen.copy(alpha = 0.15f) else Color.Transparent
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Seeker", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = if (isSeeker) EmeraldGreen else textPrimary)
                    Text("Exploring Islam & Morals", fontSize = 10.sp, color = textSecondary)
                }
            }
        }

        // Retake Spiritual Diagnostic
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Spiritual Diagnostic Questionnaire",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = "Current: ${archetype.title} • Re-diagnose triggers if your routine changes",
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }

            Button(
                onClick = onRetakeDiagnostic,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Retake", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Data & Privacy Actions
        Text(
            text = "Data & Sitr Privacy",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onClearLogs,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Clear Logs", fontSize = 11.sp)
            }

            OutlinedButton(
                onClick = onExportData,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Export Data", fontSize = 11.sp)
            }

            OutlinedButton(
                onClick = onResetProfile,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RubyRed)
            ) {
                Text("Reset", fontSize = 11.sp)
            }
        }
    }
}

// ==========================================
// 7. APP PREFERENCES FOLDER CONTENT
// ==========================================
@Composable
fun AppPreferencesFolderContent(
    viewModel: MainViewModel,
    selectedLanguage: String,
    defaultReciter: String,
    hapticEnabled: Boolean,
    soundEffectsEnabled: Boolean,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // UI Language Selector
        Text(
            text = "Language & Translation",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )

        val languages = listOf("English", "العربية (Arabic)", "اردو (Urdu)", "Bahasa Indonesia", "Français")
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            languages.forEach { lang ->
                OptionSelectRow(
                    title = lang,
                    isSelected = selectedLanguage == lang,
                    onClick = { viewModel.setSelectedLanguage(lang) },
                    isDark = isDark,
                    textPrimary = textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Default Quran Reciter
        Text(
            text = "Default Quran Reciter",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )

        val reciters = listOf(
            "Mishary Rashid Alafasy",
            "Abdul Basit Abdul Samad",
            "Abdur-Rahman As-Sudais",
            "Maher Al-Muaiqly"
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            reciters.forEach { reciter ->
                OptionSelectRow(
                    title = reciter,
                    isSelected = defaultReciter == reciter,
                    onClick = { viewModel.setDefaultReciter(reciter) },
                    isDark = isDark,
                    textPrimary = textPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Haptic Feedback & Audio SFX
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Haptic Feedback", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Text("Tactile vibrations on point claims & prayer checks", fontSize = 11.sp, color = textSecondary)
            }
            Switch(
                checked = hapticEnabled,
                onCheckedChange = { viewModel.toggleHapticFeedback(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldGreen)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Sound Effects", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Text("Audio chimes on quest completion & streak update", fontSize = 11.sp, color = textSecondary)
            }
            Switch(
                checked = soundEffectsEnabled,
                onCheckedChange = { viewModel.toggleSoundEffects(it) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldGreen)
            )
        }
    }
}

// ==========================================
// HELPER UI COMPONENTS
// ==========================================

@Composable
fun ProfileMetricBadge(
    icon: String,
    value: String,
    label: String,
    isDark: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFFF8FAFC) else SlateBlue
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
        )
    }
}

@Composable
fun OptionSelectRow(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    textPrimary: Color
) {
    val rowBg = if (isSelected) EmeraldGreen.copy(alpha = 0.12f) else if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(rowBg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) EmeraldGreen else textPrimary
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = EmeraldGreen,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun PointLedgerItemRow(
    entry: PointLedgerEntry,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color
) {
    val rowBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    val pointColor = if (entry.isCredit) EmeraldGreen else RubyRed
    val pointPrefix = if (entry.isCredit) "+" else "-"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(rowBg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Text(text = entry.iconEmoji, fontSize = 18.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = entry.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = "${entry.category} • ${entry.timestamp}",
                    fontSize = 11.sp,
                    color = textSecondary
                )
            }
        }

        Text(
            text = "$pointPrefix${entry.points} XP",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = pointColor
        )
    }
}
