package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Psychological Friction Engine Steps:
 * 1. Hard Pause (60-second unskippable countdown)
 * 2. Cognitive Reset (verbatim typing challenge)
 * 3. The Mirror (onboarding goal + streak reflection)
 * 4. Gamified Escape Hatch (+50 Points reward vs. unlock)
 */
enum class FrictionStep {
    HARD_PAUSE,
    COGNITIVE_RESET,
    THE_MIRROR,
    ESCAPE_HATCH,
    UNLOCKED_SUCCESS
}

private const val REQUIRED_TYPING_STATEMENT = "I am trading my peace for a temporary distraction."

@Composable
fun LateNightFrictionScreen(
    appName: String = "Restricted App",
    reason: String = "Night Shield Active",
    streakDays: Int = 14,
    onboardingGoal: String = "break free from late-night habits and strengthen your connection with Allah",
    onGoHome: () -> Unit,
    onEscapeHatchReward: () -> Unit = {},
    onUnlockConfirmed: () -> Unit = {}
) {
    var currentStep by remember { mutableStateOf(FrictionStep.HARD_PAUSE) }
    var secondsRemaining by remember { mutableIntStateOf(60) }
    var typedText by remember { mutableStateOf("") }
    var isCamouflageMode by remember { mutableStateOf(false) }

    // Hard Pause 60-second Timer
    LaunchedEffect(currentStep) {
        if (currentStep == FrictionStep.HARD_PAUSE) {
            secondsRemaining = 60
            while (secondsRemaining > 0) {
                delay(1000L)
                secondsRemaining--
            }
        }
    }

    // Quick-Exit Gesture Detection (Swipe Down to immediately camouflage into generic Qibla / Prayer Screen)
    val gestureModifier = Modifier.pointerInput(Unit) {
        detectDragGestures { _, dragAmount ->
            if (dragAmount.y > 40f) {
                // Swiped down! Activate instant Sitr Camouflage
                isCamouflageMode = true
            }
        }
    }

    if (isCamouflageMode) {
        SitrCamouflageScreen(
            onExit = {
                isCamouflageMode = false
                onGoHome()
            }
        )
        return
    }

    // Y2K Retro Digital High-Contrast Minimal Aesthetic
    val terminalBlack = Color(0xFF07090E)
    val terminalCardBg = Color(0xFF0F1420)
    val matrixGreen = Color(0xFF10B981)
    val cyberAmber = Color(0xFFF59E0B)
    val cyberCyan = Color(0xFF06B6D4)
    val mutedGray = Color(0xFF64748B)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(terminalBlack)
            .then(gestureModifier)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Terminal Header with Sitr Camouflage shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(terminalCardBg)
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (secondsRemaining > 0 && currentStep == FrictionStep.HARD_PAUSE) cyberAmber else matrixGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "[ NIGHT SHIELD // SITR MODE ]",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = matrixGreen,
                        letterSpacing = 1.sp
                    )
                }

                // Quick-Exit Button / Camouflage trigger
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .clickable { isCamouflageMode = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Camouflage Mode",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Quick Exit (Swipe ↓)",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Dynamic Step Container
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (currentStep) {
                    FrictionStep.HARD_PAUSE -> {
                        HardPauseView(
                            appName = appName,
                            secondsRemaining = secondsRemaining,
                            onTimeFinished = { currentStep = FrictionStep.COGNITIVE_RESET }
                        )
                    }

                    FrictionStep.COGNITIVE_RESET -> {
                        CognitiveResetView(
                            typedText = typedText,
                            onTextChanged = { typedText = it },
                            onCompleted = { currentStep = FrictionStep.THE_MIRROR }
                        )
                    }

                    FrictionStep.THE_MIRROR -> {
                        TheMirrorView(
                            streakDays = streakDays,
                            onboardingGoal = onboardingGoal,
                            onProceedToEscapeHatch = { currentStep = FrictionStep.ESCAPE_HATCH }
                        )
                    }

                    FrictionStep.ESCAPE_HATCH -> {
                        GamifiedEscapeHatchView(
                            streakDays = streakDays,
                            onEscapeHatchReward = {
                                onEscapeHatchReward()
                                onGoHome()
                            },
                            onConfirmUnlock = {
                                currentStep = FrictionStep.UNLOCKED_SUCCESS
                                onUnlockConfirmed()
                            }
                        )
                    }

                    FrictionStep.UNLOCKED_SUCCESS -> {
                        UnlockedStatusView(
                            appName = appName,
                            onReturnHome = onGoHome
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Safe Return Button
            Button(
                onClick = onGoHome,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CLOSE & PROTECT PEACE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

/**
 * STEP 1: The Hard Pause (60-Second Delay)
 */
@Composable
private fun HardPauseView(
    appName: String,
    secondsRemaining: Int,
    onTimeFinished: () -> Unit
) {
    val progress = secondsRemaining / 60f
    val minutes = secondsRemaining / 60
    val secs = secondsRemaining % 60
    val formattedTime = String.format("%02d:%02d", minutes, secs)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0B101B))
            .border(1.2.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        // Pixel Icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Hard Pause",
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "[ HARD PAUSE: 60 SECONDS ]",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF59E0B),
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Target: $appName",
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Big Digital Monospace Countdown
        Text(
            text = formattedTime,
            fontFamily = FontFamily.Monospace,
            fontSize = 54.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Color(0xFFF59E0B),
            trackColor = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Your brain is chasing instant dopamine. Sit with this silence for 60 seconds. Let your logical mind catch up.",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (secondsRemaining == 0) {
            Button(
                onClick = onTimeFinished,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(
                    text = "PROCEED TO COGNITIVE RESET →",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        } else {
            Text(
                text = "NO BYPASS • TIMER ACTIVE (${secondsRemaining}s)",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

/**
 * STEP 2: The Cognitive Reset (Active Typing Challenge)
 */
@Composable
private fun CognitiveResetView(
    typedText: String,
    onTextChanged: (String) -> Unit,
    onCompleted: () -> Unit
) {
    val isExactMatch = typedText.trim() == REQUIRED_TYPING_STATEMENT
    val progressPercent = (typedText.length.toFloat() / REQUIRED_TYPING_STATEMENT.length.toFloat()).coerceIn(0f, 1f)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0B101B))
            .border(1.2.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "[ STEP 2: COGNITIVE RESET ]",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF06B6D4),
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "To proceed, you must type this exact truth verbatim:",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Target Phrase Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF162032))
                .border(1.dp, Color(0xFF06B6D4).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "\"$REQUIRED_TYPING_STATEMENT\"",
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Typing Input
        OutlinedTextField(
            value = typedText,
            onValueChange = onTextChanged,
            placeholder = {
                Text(
                    text = "Type statement here...",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            },
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = if (isExactMatch) Color(0xFF10B981) else Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0E1726),
                unfocusedContainerColor = Color(0xFF0E1726),
                focusedBorderColor = if (isExactMatch) Color(0xFF10B981) else Color(0xFF06B6D4),
                unfocusedBorderColor = Color(0xFF1E293B)
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = if (isExactMatch) ImeAction.Done else ImeAction.Default
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (isExactMatch) onCompleted()
                }
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { progressPercent },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = if (isExactMatch) Color(0xFF10B981) else Color(0xFF06B6D4),
            trackColor = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onCompleted,
            enabled = isExactMatch,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981),
                disabledContainerColor = Color(0xFF1E293B),
                contentColor = Color.Black,
                disabledContentColor = Color(0xFF64748B)
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        ) {
            Text(
                text = if (isExactMatch) "CONFIRMED • PROCEED TO THE MIRROR →" else "TYPE EXACT PHRASE TO UNLOCK",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * STEP 3: The Mirror (Onboarding Goal & Pure Streak Reflection)
 */
@Composable
private fun TheMirrorView(
    streakDays: Int,
    onboardingGoal: String,
    onProceedToEscapeHatch: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0B101B))
            .border(1.2.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "[ STEP 3: THE MIRROR ]",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE11D48),
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Streak At Stake Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1F121C))
                .border(1.dp, Color(0xFFE11D48).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥",
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "$streakDays-DAY PURE STREAK AT RISK",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFDA4AF)
                )
                Text(
                    text = "Resetting now drops your Shield Purity to 0%",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Onboarding Goal Recall Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF121B2B))
                .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "YOUR INTENTION ON DAY 1:",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF93C5FD),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"When you joined, you said your biggest goal was to $onboardingGoal.\"",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color.White,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Are you sure you want to trade all this progress for a temporary spike in dopamine?",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = onProceedToEscapeHatch,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF59E0B),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        ) {
            Text(
                text = "CONTINUE TO FINAL CHOICE →",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * STEP 4: The Gamified Escape Hatch
 */
@Composable
private fun GamifiedEscapeHatchView(
    streakDays: Int,
    onEscapeHatchReward: () -> Unit,
    onConfirmUnlock: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0B101B))
            .border(1.2.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "[ STEP 4: THE ESCAPE HATCH ]",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF10B981),
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Choose your path right now:",
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // High Value Reward Escape Hatch Button
        Button(
            onClick = onEscapeHatchReward,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF10B981),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "🛡️", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "CANCEL UNLOCK & EARN +50 XP",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Enough for a new Marketplace item",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF064E3B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Secondary subtle bypass button
        OutlinedButton(
            onClick = onConfirmUnlock,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFEF4444)
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF7F1D1D)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Text(
                text = "Unlock Anyway (Reset Streak to 0)",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color(0xFFF87171)
            )
        }
    }
}

@Composable
private fun UnlockedStatusView(
    appName: String,
    onReturnHome: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0B101B))
            .border(1.2.dp, Color(0xFF1E293B), RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LockOpen,
            contentDescription = "Temporary Unlock",
            tint = Color(0xFFF59E0B),
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Temporary Access Active",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Night Shield will automatically re-engage in 10 minutes.",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onReturnHome,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "RETURN HOME",
                fontFamily = FontFamily.Monospace,
                color = Color.White
            )
        }
    }
}

/**
 * Sitr Camouflage Screen (Qibla Compass & Prayer Schedule)
 * Triggered by swiping down during any blocker moment to safeguard privacy
 */
@Composable
fun SitrCamouflageScreen(
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = "Prayer",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Qibla & Daily Salah",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }

                IconButton(onClick = onExit) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            // Central Qibla Compass Visual
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(2.dp, Color(0xFF1E293B), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Compass",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "64° ENE (Makkah)",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF34D399)
                    )
                    Text(
                        text = "Calibrated",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Prayer times list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .padding(14.dp)
            ) {
                Text(
                    text = "TODAY'S PRAYERS",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                CamouflagePrayerRow("Fajr", "05:15 AM", true)
                CamouflagePrayerRow("Dhuhr", "12:30 PM", true)
                CamouflagePrayerRow("Asr", "03:45 PM", true)
                CamouflagePrayerRow("Maghrib", "06:45 PM", true)
                CamouflagePrayerRow("Isha", "08:15 PM", true)
            }

            Button(
                onClick = onExit,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Return to Home",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CamouflagePrayerRow(name: String, time: String, isDone: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            color = Color.White,
            fontSize = 13.sp
        )
        Text(
            text = time,
            color = Color(0xFF10B981),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )
    }
}
