package com.example.ui.components

import com.example.ui.theme.SlateBlue
import com.example.ui.theme.AlabasterSand

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.utils.CalculationMethod
import com.example.utils.LocationAndMosqueManager
import com.example.utils.NearbyMosque
import com.example.utils.PositioningSystemType
import com.example.utils.UserLocationState
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Header Card displaying Location, Active Positioning Technology (GPS/WPS/Cell), and Accuracy
 */
@Composable
fun PixelLocationAwarenessHeaderCard(
    locationState: UserLocationState,
    onOpenTechVisualizer: () -> Unit
) {
    PixelCardContainer(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenTechVisualizer() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = PixelGreenAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = locationState.cityName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Positioning System Badge
                val (badgeColor, systemIcon) = when (locationState.activeSystem) {
                    PositioningSystemType.GPS -> PixelGreenAccent to Icons.Default.SatelliteAlt
                    PositioningSystemType.WPS -> PixelGoldAccent to Icons.Default.Wifi
                    PositioningSystemType.CELLULAR -> Color(0xFF38BDF8) to Icons.Default.CellTower
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = badgeColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, badgeColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = systemIcon,
                            contentDescription = locationState.activeSystem.shortName,
                            tint = PixelDarkBorder,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = locationState.activeSystem.shortName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PixelDarkBorder
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Coordinates & Accuracy details bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${String.format(Locale.US, "%.4f", locationState.latitude)}° N, ${String.format(Locale.US, "%.4f", locationState.longitude)}° E",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF475569)
                )

                Text(
                    text = "Qibla: ${locationState.qiblaBearingDegrees.toInt()}°",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PixelGreenAccent
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "Accuracy",
                        tint = PixelGreenAccent,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "±${locationState.accuracyMeters.toInt()}m",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder
                    )
                }
            }
        }
    }
}

/**
 * Comprehensive Modal: How Location Tracking Works (OS Tech Visualizer + Permission + Presets)
 */
@Composable
fun PixelOsLocationTechModal(
    locationState: UserLocationState,
    onDismiss: () -> Unit,
    onSelectPreset: (UserLocationState) -> Unit,
    onSetCalculationMethod: (CalculationMethod) -> Unit,
    onRequestRealGps: () -> Unit
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            onRequestRealGps()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFFBF8F1),
            border = androidx.compose.foundation.BorderStroke(3.dp, PixelDarkBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Location Tech",
                            tint = PixelGreenAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Location Engine & Tech",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelDarkBorder
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Real Device GPS Permission Trigger Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (hasPermission) PixelGreenAccent.copy(alpha = 0.15f) else PixelGoldAccent.copy(alpha = 0.2f))
                        .border(2.dp, if (hasPermission) PixelGreenAccent else PixelGoldAccent, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (hasPermission) "GPS Permissions Granted" else "1. Request Location Permission",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PixelDarkBorder
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (hasPermission) "Receiving live location updates" else "Required by OS privacy rules to fetch coordinates",
                                fontSize = 10.sp,
                                color = Color(0xFF475569)
                            )
                        }

                        Button(
                            onClick = {
                                if (!hasPermission) {
                                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                } else {
                                    onRequestRealGps()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PixelDarkBorder,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "GPS",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (hasPermission) "Acquire GPS" else "Allow",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: How OS Location Tracking Works (The 3 Positioning Systems)
                Text(
                    text = "How Your Phone OS Tracks Location",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PixelDarkBorder
                )

                Spacer(modifier = Modifier.height(8.dp))

                PositioningSystemType.values().forEach { system ->
                    val isActive = locationState.activeSystem == system
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isActive) Color.White else Color(0xFFF1F5F9))
                            .border(
                                width = if (isActive) 2.5.dp else 1.dp,
                                color = if (isActive) PixelGreenAccent else Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = system.title,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PixelDarkBorder
                                )

                                Text(
                                    text = system.accuracyDescription,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) PixelGreenAccent else Color(0xFF64748B)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = system.howItWorks,
                                fontSize = 10.sp,
                                color = Color(0xFF475569),
                                lineHeight = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Calculation Method Picker
                Text(
                    text = "Islamic Calculation Method",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PixelDarkBorder
                )
                Spacer(modifier = Modifier.height(6.dp))

                CalculationMethod.values().forEach { method ->
                    val isSelected = locationState.calculationMethod == method
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSetCalculationMethod(method) }
                            .background(if (isSelected) PixelGoldAccent.copy(alpha = 0.25f) else Color.Transparent)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = method.displayName,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = PixelDarkBorder
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = PixelDarkBorder,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 4: Global City Coordinates Presets
                Text(
                    text = "Switch Coordinates / Global Cities",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PixelDarkBorder
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LocationAndMosqueManager.PRESET_CITIES.chunked(2).forEach { rowCities ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowCities.forEach { city ->
                                val isCurrent = locationState.cityName == city.cityName
                                Button(
                                    onClick = { onSelectPreset(city) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isCurrent) PixelGreenAccent else Color.White,
                                        contentColor = PixelDarkBorder
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.5.dp, PixelDarkBorder)
                                ) {
                                    Text(
                                        text = city.cityName.split(",")[0],
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dynamic Solar Namaz Time & Qibla Direction Component Card
 */
@Composable
fun PixelNamazAndQiblaCard(
    calculatedPrayers: List<com.example.utils.CalculatedPrayerTime>,
    locationState: UserLocationState,
    onOpenTechModal: () -> Unit,
    onOpenQiblaCompass: () -> Unit = {}
) {
    PixelCardContainer(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Prayers & Qibla",
                        tint = PixelGoldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Calculated Namaz & Qibla",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder
                    )
                }

                IconButton(
                    onClick = onOpenTechModal,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Method",
                        tint = PixelDarkBorder,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Method: ${locationState.calculationMethod.displayName}",
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Qibla Bearing Radar Visualizer Mini Banner (Clickable for Compass)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SlateBlue)
                    .clickable { onOpenQiblaCompass() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "QIBLA BEARING",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PixelGoldAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• OPEN COMPASS ➔",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = PixelGreenAccent
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${locationState.qiblaBearingDegrees.toInt()}° relative to North",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Distance to Makkah: ~${(LocationAndMosqueManager.calculateHaversineDistanceKm(locationState.latitude, locationState.longitude, LocationAndMosqueManager.MAKKAH_LAT, LocationAndMosqueManager.MAKKAH_LNG)).toInt()} km",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Compass Icon Pointer
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PixelGoldAccent)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Qibla Direction",
                        tint = PixelDarkBorder,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(locationState.qiblaBearingDegrees)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calculated Prayer Times Grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                calculatedPrayers.forEach { prayer ->
                    val isNext = prayer.isNext
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isNext) PixelGreenAccent.copy(alpha = 0.2f) else Color(0xFFF8FAFC))
                            .border(
                                width = if (isNext) 2.dp else 1.dp,
                                color = if (isNext) PixelGreenAccent else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isNext) PixelGreenAccent else PixelGoldAccent)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = prayer.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PixelDarkBorder
                                )
                                if (isNext) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• UPCOMING",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PixelGreenAccent
                                    )
                                }
                            }

                            Text(
                                text = prayer.formattedTime,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = PixelDarkBorder
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Nearby Masjids Finder & Distance Radar Card
 */
@Composable
fun PixelNearbyMasjidsCard(
    nearbyMosques: List<NearbyMosque>,
    locationState: UserLocationState,
    onToggleBookmark: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isRadarView by remember { mutableStateOf(false) }

    PixelCardContainer(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = "Nearby Masjids",
                        tint = PixelGreenAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Nearby Masjids (${nearbyMosques.size})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelDarkBorder
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (isExpanded) {
                        Button(
                            onClick = { isRadarView = !isRadarView },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRadarView) PixelGoldAccent else Color(0xFFF1F5F9),
                                contentColor = PixelDarkBorder
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isRadarView) Icons.Default.Map else Icons.Default.NearMe,
                                    contentDescription = "Toggle Radar",
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isRadarView) "List View" else "Radar View",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Dropdown Toggle Button
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .border(1.5.dp, PixelDarkBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Toggle Menu Dropdown",
                            tint = PixelDarkBorder
                        )
                    }
                }
            }

            if (!isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8FAFC))
                        .clickable { isExpanded = true }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tap to expand ${nearbyMosques.size} nearby masjids & prayer times",
                        fontSize = 11.sp,
                        color = PixelDarkBorder.copy(alpha = 0.75f)
                    )
                    Text(
                        text = "Expand ▾",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PixelGreenAccent
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

            if (isRadarView) {
                // Interactive 2D Radar Canvas Map Visualizer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val cX = size.width / 2f
                        val cY = size.height / 2f
                        val maxR = size.width.coerceAtMost(size.height) * 0.4f

                        // Radar Rings
                        drawCircle(color = PixelGreenAccent.copy(alpha = 0.3f), radius = maxR, center = Offset(cX, cY), style = Stroke(width = 1.5f))
                        drawCircle(color = PixelGreenAccent.copy(alpha = 0.2f), radius = maxR * 0.66f, center = Offset(cX, cY), style = Stroke(width = 1f))
                        drawCircle(color = PixelGreenAccent.copy(alpha = 0.15f), radius = maxR * 0.33f, center = Offset(cX, cY), style = Stroke(width = 1f))

                        // Crosshairs
                        drawLine(color = PixelGreenAccent.copy(alpha = 0.2f), start = Offset(cX, cY - maxR), end = Offset(cX, cY + maxR), strokeWidth = 1f)
                        drawLine(color = PixelGreenAccent.copy(alpha = 0.2f), start = Offset(cX - maxR, cY), end = Offset(cX + maxR, cY), strokeWidth = 1f)

                        // Center User Blip
                        drawCircle(color = Color.White, radius = 6f, center = Offset(cX, cY))
                        drawCircle(color = PixelGreenAccent, radius = 10f, center = Offset(cX, cY), style = Stroke(width = 2f))

                        // Draw Nearby Mosques on Radar
                        nearbyMosques.forEachIndexed { idx, mosque ->
                            val normalizedDist = (mosque.distanceKm / 3.0).coerceAtMost(1.0).toFloat()
                            val rad = (mosque.bearingDegrees * (PI / 180f)).toFloat()
                            val mX = cX + (normalizedDist * maxR) * sin(rad)
                            val mY = cY - (normalizedDist * maxR) * cos(rad)

                            drawCircle(color = PixelGoldAccent, radius = 7f, center = Offset(mX, mY))
                        }
                    }

                    Text(
                        text = "YOU ARE IN THE CENTER • 3 KM RADAR",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PixelGoldAccent,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    )
                }
            } else {
                // List of Nearby Masjids
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    nearbyMosques.forEach { mosque ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = mosque.name,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = PixelDarkBorder,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = mosque.address,
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = PixelGreenAccent.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = mosque.distanceFormatted,
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = PixelDarkBorder,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Text(
                                            text = mosque.iqamahStatus,
                                            fontSize = 9.5.sp,
                                            color = Color(0xFF334155)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { onToggleBookmark(mosque.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (mosque.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = if (mosque.isBookmarked) PixelGoldAccent else Color(0xFF94A3B8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}
}
