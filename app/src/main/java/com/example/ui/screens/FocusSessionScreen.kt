package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PixelAccountProfileDialog
import com.example.ui.components.PixelAvatarIcon
import com.example.ui.components.PixelBgCanvas
import com.example.ui.components.PixelCoinIcon
import com.example.ui.components.PixelDarkBorder
import com.example.ui.components.PixelLocationAwarenessHeaderCard
import com.example.ui.components.PixelNamazAndQiblaCard
import com.example.ui.components.PixelNearbyMasjidsCard
import com.example.ui.components.PixelOsLocationTechModal
import com.example.ui.components.QiblaCompassModal
import com.example.ui.theme.SlateBlue
import com.example.ui.viewmodel.MainViewModel

@Composable
fun FocusSessionScreen(viewModel: MainViewModel) {
    val coins by viewModel.coins.collectAsState()
    val purityIndex by viewModel.purityIndex.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val totalZakatDonated by viewModel.totalZakatDonated.collectAsState()
    val resistedCount by viewModel.resistedCount.collectAsState()

    // Location & Mosque Management State
    val locationState by viewModel.locationState.collectAsState()
    val calculatedPrayers by viewModel.calculatedPrayers.collectAsState()
    val nearbyMosques by viewModel.nearbyMosques.collectAsState()

    var showAccountProfile by remember { mutableStateOf(false) }
    var showLocationTechModal by remember { mutableStateOf(false) }
    var showQiblaCompassModal by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Qibla Compass Modal (Great-Circle Bearing Trigonometry & Hardware Magnetometer)
    if (showQiblaCompassModal) {
        QiblaCompassModal(
            initialLocationState = locationState,
            onDismiss = { showQiblaCompassModal = false }
        )
    }

    // Location Engine & OS Tech Modal
    if (showLocationTechModal) {
        PixelOsLocationTechModal(
            locationState = locationState,
            onDismiss = { showLocationTechModal = false },
            onSelectPreset = { newState ->
                viewModel.updateLocationState(newState)
            },
            onSetCalculationMethod = { method ->
                viewModel.setCalculationMethod(method)
            },
            onRequestRealGps = {
                viewModel.startRealGpsTracking(context)
            }
        )
    }

    // Account Profile Dialog
    if (showAccountProfile) {
        PixelAccountProfileDialog(
            totalCoins = coins,
            streakDays = streakDays,
            purityIndex = purityIndex,
            zakatDonated = totalZakatDonated,
            resistedCount = resistedCount,
            onDismiss = { showAccountProfile = false }
        )
    }

    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val textColor = if (isDarkMode) Color(0xFFF8FAFC) else SlateBlue
    val subtextColor = if (isDarkMode) Color(0xFF94A3B8) else SlateBlue.copy(alpha = 0.75f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkMode) Color(0xFF080C14) else PixelBgCanvas)
    ) {
        // Islamic Background Tile Watermark Effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val step = 80f
            val dotColor = if (isDarkMode) Color(0xFF101726) else Color(0xFFDCDFE3)

            for (x in 0..(w / step).toInt()) {
                for (y in 0..(h / step).toInt()) {
                    val px = x * step
                    val py = y * step
                    drawCircle(
                        color = dotColor,
                        radius = 20f,
                        center = Offset(px, py),
                        style = Stroke(width = 1.5f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // 1. TOP HEADER WITH COINS & AVATAR
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Location & Prayer Engine",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Real-time solar calculation, Qibla compass & nearby masjids radar",
                        fontSize = 10.5.sp,
                        color = subtextColor
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable { showAccountProfile = true }
                ) {
                    PixelCoinIcon(modifier = Modifier.size(22.dp))
                    Text(
                        text = String.format("%,d", coins),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textColor
                    )
                    PixelAvatarIcon(modifier = Modifier.size(34.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // 2. DYNAMIC SOLAR NAMAZ & QIBLA CARD
            // ==========================================
            PixelNamazAndQiblaCard(
                calculatedPrayers = calculatedPrayers,
                locationState = locationState,
                onOpenTechModal = { showLocationTechModal = true },
                onOpenQiblaCompass = { showQiblaCompassModal = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 3. NEARBY MASJIDS FINDER & RADAR CARD
            // ==========================================
            PixelNearbyMasjidsCard(
                nearbyMosques = nearbyMosques,
                locationState = locationState,
                onToggleBookmark = { mosqueId ->
                    viewModel.toggleMosqueBookmark(mosqueId)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 4. LOCATION ENGINE & OS POSITIONING HEADER CARD
            // ==========================================
            PixelLocationAwarenessHeaderCard(
                locationState = locationState,
                onOpenTechVisualizer = { showLocationTechModal = true }
            )

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}


