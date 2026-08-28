package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DiagnosticOption
import com.example.data.DiagnosticQuestion
import com.example.data.DiagnosticQuestionnaireData
import com.example.data.UserArchetype
import com.example.ui.components.PixelBgCanvas
import com.example.ui.components.PixelCardContainer
import com.example.ui.components.PixelCoinIcon
import com.example.ui.components.PixelCrescentStarIcon
import com.example.ui.components.PixelDarkBorder
import com.example.ui.components.PixelGoldAccent
import com.example.ui.components.PixelGreenAccent
import com.example.ui.components.PixelHeartIcon
import com.example.ui.theme.AlabasterSand
import com.example.ui.theme.DesertGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.RubyRed
import com.example.ui.theme.SlateBlue
import com.example.ui.viewmodel.MainViewModel

/**
 * Deep-Dive Spiritual & Trigger Diagnostic Onboarding Questionnaire
 * Based on Islamic psychology and Sitr (concealment of sins).
 */
@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val questions = DiagnosticQuestionnaireData.questions
    val totalQuestions = questions.size

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var isShowingResults by remember { mutableStateOf(false) }
    var detectedArchetype by remember { mutableStateOf<UserArchetype?>(null) }

    val answers by viewModel.diagnosticAnswers.collectAsState()

    val screenBg = if (isDark) Color(0xFF080C14) else Color(0xFFF8FAFC)
    val textColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(screenBg)
    ) {
        if (!isShowingResults) {
            val currentQuestion = questions[currentQuestionIndex]
            val currentAnswer = answers[currentQuestion.id]
            val selectedOptionKey = currentAnswer?.first
            val customText = currentAnswer?.second ?: ""

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Top Header with Sitr & Confidentiality Indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentQuestionIndex > 0) {
                        IconButton(
                            onClick = { currentQuestionIndex-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = textColor
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(36.dp))
                    }

                    // Sitr Confidentiality Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0xFF131B2E) else Color(0xFFE2E8F0))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confidential",
                            modifier = Modifier.size(12.dp),
                            tint = if (isDark) PixelGoldAccent else SlateBlue
                        )
                        Text(
                            text = "SITR (CONFIDENTIAL)",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) PixelGoldAccent else SlateBlue,
                            letterSpacing = 0.8.sp
                        )
                    }

                    Text(
                        text = "${currentQuestionIndex + 1}/$totalQuestions",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = subtitleColor
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = EmeraldGreen,
                    trackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Phase Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            when (currentQuestion.phaseNumber) {
                                1 -> EmeraldGreen.copy(alpha = 0.15f)
                                2 -> RubyRed.copy(alpha = 0.15f)
                                else -> DesertGold.copy(alpha = 0.15f)
                            }
                        )
                        .border(
                            1.dp,
                            when (currentQuestion.phaseNumber) {
                                1 -> EmeraldGreen
                                2 -> RubyRed
                                else -> DesertGold
                            },
                            RoundedCornerShape(14.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "PHASE ${currentQuestion.phaseNumber} • ${currentQuestion.phaseTitle.uppercase()}",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when (currentQuestion.phaseNumber) {
                            1 -> if (isDark) EmeraldGreen else Color(0xFF0D6832)
                            2 -> if (isDark) Color(0xFFFF5252) else Color(0xFFBA2D0B)
                            else -> if (isDark) DesertGold else Color(0xFF9A7B00)
                        },
                        letterSpacing = 0.6.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Title
                Text(
                    text = currentQuestion.questionText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 27.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = currentQuestion.subtitle,
                    fontSize = 12.5.sp,
                    color = subtitleColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Options List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentQuestion.options.forEach { option ->
                        val isSelected = selectedOptionKey == option.key

                        DiagnosticOptionCard(
                            option = option,
                            isSelected = isSelected,
                            customTextValue = if (option.isCustomInput) customText else "",
                            onSelect = {
                                viewModel.saveDiagnosticAnswer(
                                    questionId = currentQuestion.id,
                                    optionKey = option.key,
                                    customText = if (option.isCustomInput) customText else ""
                                )
                            },
                            onCustomTextChanged = { newText ->
                                viewModel.saveDiagnosticAnswer(
                                    questionId = currentQuestion.id,
                                    optionKey = option.key,
                                    customText = newText
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Continue / Next Button
                val isAnswered = selectedOptionKey != null &&
                        (!currentQuestion.options.firstOrNull { it.key == selectedOptionKey }?.isCustomInput!! || customText.isNotBlank())

                Button(
                    onClick = {
                        if (currentQuestionIndex < totalQuestions - 1) {
                            currentQuestionIndex++
                        } else {
                            // Complete and compute archetype
                            val archetype = viewModel.completeDiagnosticAndApplyIntervention()
                            detectedArchetype = archetype
                            isShowingResults = true
                        }
                    },
                    enabled = isAnswered,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(
                            elevation = if (isAnswered) 6.dp else 0.dp,
                            shape = RoundedCornerShape(16.dp),
                            spotColor = EmeraldGreen.copy(alpha = 0.4f)
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        contentColor = Color.White,
                        disabledContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                        disabledContentColor = subtitleColor
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (currentQuestionIndex < totalQuestions - 1) "Continue" else "Analyze My Spiritual Blueprint",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        } else {
            // ==========================================
            // PHASE 4: DIAGNOSTIC RESULTS & BLUEPRINT
            // ==========================================
            detectedArchetype?.let { archetype ->
                DiagnosticResultsScreen(
                    archetype = archetype,
                    onStartApp = onComplete
                )
            }
        }
    }
}

/**
 * Option Card with Option Letter Badge, Description, and Expandable Custom Text Input
 */
@Composable
private fun DiagnosticOptionCard(
    option: DiagnosticOption,
    isSelected: Boolean,
    customTextValue: String,
    onSelect: () -> Unit,
    onCustomTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkTheme.current
    val cardBg = when {
        isSelected -> if (isDark) Color(0xFF16253B) else Color(0xFFEFF6FF)
        else -> if (isDark) Color(0xFF101726) else Color.White
    }

    val cardBorder = when {
        isSelected -> EmeraldGreen
        else -> if (isDark) Color(0xFF1E2D4A) else Color(0xFFE2E8F0)
    }

    val textColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isSelected) 4.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isSelected) EmeraldGreen.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(if (isSelected) 1.8.dp else 1.dp, cardBorder, RoundedCornerShape(16.dp))
            .clickable { onSelect() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Key Letter Badge (A, B, C, D, E)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) EmeraldGreen else if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.key,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) Color.White else if (isDark) Color(0xFFCBD5E1) else SlateBlue
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Label and description
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = option.label,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = textColor,
                        lineHeight = 19.sp
                    )
                    if (option.description.isNotEmpty() && !option.isCustomInput) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = option.description,
                            fontSize = 11.sp,
                            color = subtitleColor
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = EmeraldGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Expandable Custom Text Field for Option E ("Something else")
            AnimatedVisibility(visible = option.isCustomInput && isSelected) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    OutlinedTextField(
                        value = customTextValue,
                        onValueChange = onCustomTextChanged,
                        placeholder = {
                            Text(
                                text = "Please share what you are experiencing or struggling with...",
                                fontSize = 12.5.sp,
                                color = subtitleColor
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1),
                            focusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                            unfocusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                    )
                }
            }
        }
    }
}

/**
 * Diagnostic Results & Tailored Intervention Blueprint
 */
@Composable
private fun DiagnosticResultsScreen(
    archetype: UserArchetype,
    onStartApp: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val textColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF1E293B)
    val subtitleColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Spiritual Badge Tag
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(EmeraldGreen.copy(alpha = 0.15f))
                .border(1.2.dp, EmeraldGreen, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = archetype.badge,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isDark) EmeraldGreen else Color(0xFF0D6832),
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Your Spiritual Blueprint",
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = subtitleColor,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = archetype.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            textAlign = TextAlign.Center
        )

        Text(
            text = archetype.subtitle,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDark) DesertGold else SlateBlue
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Quran Verse of Mercy & Sitr
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) Color(0xFF131B2E) else Color(0xFFF0FDF4))
                .border(1.2.dp, if (isDark) Color(0xFF2A3854) else Color(0xFFBBF7D0), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "“${archetype.quranVerse}”",
                    fontSize = 13.5.sp,
                    fontStyle = FontStyle.Italic,
                    color = textColor,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "— ${archetype.quranRef}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) DesertGold else Color(0xFF15803D),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Diagnosis Analysis Description
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) Color(0xFF101726) else Color.White)
                .border(1.dp, if (isDark) Color(0xFF1E2D4A) else Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "DIAGNOSTIC ASSESSMENT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = subtitleColor,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = archetype.description,
                    fontSize = 13.sp,
                    color = textColor,
                    lineHeight = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Customized Intervention Protocols
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) Color(0xFF101726) else Color.White)
                .border(1.dp, if (isDark) Color(0xFF1E2D4A) else Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "APP CONFIGURATION & INTERVENTIONS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = subtitleColor,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                archetype.actionItems.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = item,
                            fontSize = 12.5.sp,
                            color = textColor,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Primary Action: Begin My Guided Path
        Button(
            onClick = onStartApp,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = EmeraldGreen.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmeraldGreen,
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Begin My Guided Path",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
