package com.example.ui.components

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AlabasterSand
import com.example.ui.theme.DesertGold
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeumorphicBox
import com.example.ui.theme.NeumorphicInsetBox
import com.example.ui.theme.RubyRed
import com.example.ui.theme.SlateBlue
import com.example.utils.LocationAndMosqueManager
import com.example.utils.UserLocationState
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Kaaba Constants
const val KAABA_LATITUDE = 21.4225
const val KAABA_LONGITUDE = 39.8262

/**
 * Data holder for Great-Circle Azimuth mathematical calculation breakdown
 */
data class GreatCircleMathResult(
    val userLat: Double,
    val userLng: Double,
    val deltaLngRad: Double,
    val y: Double,
    val x: Double,
    val thetaRad: Double,
    val bearingDegrees: Float,
    val distanceKm: Double
)

/**
 * Perform Great-Circle Azimuth trigonometric calculation to Kaaba
 */
fun calculateGreatCircleBearing(userLat: Double, userLng: Double): GreatCircleMathResult {
    val phi1 = Math.toRadians(userLat)
    val lambda1 = Math.toRadians(userLng)
    val phi2 = Math.toRadians(KAABA_LATITUDE)
    val lambda2 = Math.toRadians(KAABA_LONGITUDE)

    val dLambda = lambda2 - lambda1

    val y = sin(dLambda) * cos(phi2)
    val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(dLambda)

    val theta = atan2(y, x)
    var bearing = Math.toDegrees(theta)
    bearing = (bearing + 360.0) % 360.0

    // Distance calculation using Haversine formula
    val r = 6371.0
    val dLat = phi2 - phi1
    val a = sin(dLat / 2) * sin(dLat / 2) + cos(phi1) * cos(phi2) * sin(dLambda / 2) * sin(dLambda / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distance = r * c

    return GreatCircleMathResult(
        userLat = userLat,
        userLng = userLng,
        deltaLngRad = dLambda,
        y = y,
        x = x,
        thetaRad = theta,
        bearingDegrees = bearing.toFloat(),
        distanceKm = distance
    )
}

/**
 * State helper holding hardware magnetometer compass reading
 */
data class CompassSensorState(
    val currentHeadingDegrees: Float = 0f,
    val isSensorAvailable: Boolean = false,
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
    val magneticDeclination: Float = 0f
)

/**
 * Custom Composable hook to access device Rotation Vector / Magnetometer hardware
 */
@Composable
fun rememberCompassHeading(userLat: Double = 24.8607, userLng: Double = 67.0011): CompassSensorState {
    val context = LocalContext.current
    var sensorState by remember { mutableStateOf(CompassSensorState()) }

    DisposableEffect(context, userLat, userLng) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        if (sensorManager == null) {
            sensorState = sensorState.copy(isSensorAvailable = false)
            return@DisposableEffect onDispose {}
        }

        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val hasSensors = rotationVectorSensor != null || (accelerometerSensor != null && magnetometerSensor != null)
        sensorState = sensorState.copy(isSensorAvailable = hasSensors)

        // Calculate magnetic declination for True North correction
        val geoField = try {
            GeomagneticField(
                userLat.toFloat(),
                userLng.toFloat(),
                0f,
                System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
        val declination = geoField?.declination ?: 0f
        sensorState = sensorState.copy(magneticDeclination = declination)

        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)
        val lastAccelerometer = FloatArray(3)
        val lastMagnetometer = FloatArray(3)
        var lastAccelSet = false
        var lastMagSet = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    var azimuthDeg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    azimuthDeg = (azimuthDeg + 360) % 360
                    val trueNorthHeading = (azimuthDeg + declination + 360) % 360
                    sensorState = sensorState.copy(
                        currentHeadingDegrees = trueNorthHeading,
                        accuracy = event.accuracy,
                        isSensorAvailable = true
                    )
                } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
                    lastAccelSet = true
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
                    lastMagSet = true
                }

                if (lastAccelSet && lastMagSet && event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) {
                    if (SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)) {
                        SensorManager.getOrientation(rotationMatrix, orientation)
                        var azimuthDeg = Math.toDegrees(orientation[0].toDouble()).toFloat()
                        azimuthDeg = (azimuthDeg + 360) % 360
                        val trueNorthHeading = (azimuthDeg + declination + 360) % 360
                        sensorState = sensorState.copy(
                            currentHeadingDegrees = trueNorthHeading,
                            accuracy = event.accuracy,
                            isSensorAvailable = true
                        )
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                sensorState = sensorState.copy(accuracy = accuracy)
            }
        }

        if (rotationVectorSensor != null) {
            sensorManager.registerListener(listener, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelerometerSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
            magnetometerSensor?.let { sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return sensorState
}

/**
 * Interactive Qibla Compass Modal with Great-Circle Bearing Calculator & Hardware Sensor Dial
 */
@Composable
fun QiblaCompassModal(
    initialLocationState: UserLocationState,
    onDismiss: () -> Unit
) {
    var inputLatStr by remember { mutableStateOf(String.format(Locale.US, "%.4f", initialLocationState.latitude)) }
    var inputLngStr by remember { mutableStateOf(String.format(Locale.US, "%.4f", initialLocationState.longitude)) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val currentLat = inputLatStr.toDoubleOrNull() ?: initialLocationState.latitude
    val currentLng = inputLngStr.toDoubleOrNull() ?: initialLocationState.longitude

    val mathResult = remember(currentLat, currentLng) {
        calculateGreatCircleBearing(currentLat, currentLng)
    }

    val sensorState = rememberCompassHeading(userLat = currentLat, userLng = currentLng)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(24.dp),
            color = AlabasterSand,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SlateBlue.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Qibla Compass",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Qibla Great-Circle Compass",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SlateBlue
                            )
                            Text(
                                text = "Spherical Trigonometry & Hardware Magnetometer",
                                fontSize = 10.sp,
                                color = SlateBlue.copy(alpha = 0.6f)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("✕", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateBlue)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Interactive Circular Neumorphic Compass Dial
                NeumorphicCompassDial(
                    bearingDegrees = mathResult.bearingDegrees,
                    sensorHeadingDegrees = sensorState.currentHeadingDegrees,
                    isSensorAvailable = sensorState.isSensorAvailable,
                    distanceKm = mathResult.distanceKm
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 2. Input Form Layout (User Latitude & Longitude Inputs)
                NeumorphicBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Inputs",
                                tint = DesertGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Coordinates Input (-90..90, -180..180)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = inputLatStr,
                                onValueChange = {
                                    inputLatStr = it
                                    val latVal = it.toDoubleOrNull()
                                    if (latVal == null || latVal < -90.0 || latVal > 90.0) {
                                        validationError = "Latitude must be between -90 and 90"
                                    } else {
                                        validationError = null
                                    }
                                },
                                label = { Text("User Lat (°)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldGreen,
                                    unfocusedBorderColor = SlateBlue.copy(alpha = 0.25f),
                                    focusedLabelColor = EmeraldGreen
                                )
                            )

                            OutlinedTextField(
                                value = inputLngStr,
                                onValueChange = {
                                    inputLngStr = it
                                    val lngVal = it.toDoubleOrNull()
                                    if (lngVal == null || lngVal < -180.0 || lngVal > 180.0) {
                                        validationError = "Longitude must be between -180 and 180"
                                    } else {
                                        validationError = null
                                    }
                                },
                                label = { Text("User Lng (°)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldGreen,
                                    unfocusedBorderColor = SlateBlue.copy(alpha = 0.25f),
                                    focusedLabelColor = EmeraldGreen
                                )
                            )
                        }

                        if (validationError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = validationError!!,
                                fontSize = 11.sp,
                                color = RubyRed,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Quick Location Presets Row
                        Text(
                            text = "PRESET CITIES:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SlateBlue.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Karachi" to (24.8607 to 67.0011),
                                "London" to (51.5074 to -0.1278),
                                "New York" to (40.7128 to -74.0060),
                                "Jakarta" to (-6.2088 to 106.8456)
                            ).forEach { (cityName, coords) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (currentLat == coords.first && currentLng == coords.second)
                                                EmeraldGreen.copy(alpha = 0.25f)
                                            else
                                                AlabasterSand
                                        )
                                        .border(
                                            1.dp,
                                            if (currentLat == coords.first && currentLng == coords.second) EmeraldGreen else SlateBlue.copy(alpha = 0.15f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            inputLatStr = coords.first.toString()
                                            inputLngStr = coords.second.toString()
                                            validationError = null
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = cityName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SlateBlue
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Mathematical Steps & Formula Breakdown Card
                NeumorphicBox(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = "Trigonometry",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Great-Circle Trigonometry Math",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Formulas list
                        Text(
                            text = "1. Kaaba Target: Lat 21.4225° N, Lng 39.8262° E",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SlateBlue
                        )
                        Text(
                            text = "2. Δλ = λ₂ - λ₁ = ${String.format(Locale.US, "%.5f", mathResult.deltaLngRad)} rad",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SlateBlue
                        )
                        Text(
                            text = "3. y = sin(Δλ) · cos(ϕ₂) = ${String.format(Locale.US, "%.5f", mathResult.y)}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SlateBlue
                        )
                        Text(
                            text = "4. x = cos(ϕ₁)sin(ϕ₂) - sin(ϕ₁)cos(ϕ₂)cos(Δλ) = ${String.format(Locale.US, "%.5f", mathResult.x)}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SlateBlue
                        )
                        Text(
                            text = "5. θ = atan2(y, x) = ${String.format(Locale.US, "%.5f", mathResult.thetaRad)} rad",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SlateBlue
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DesertGold.copy(alpha = 0.20f))
                                .border(1.dp, DesertGold, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CALCULATED BEARING:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SlateBlue
                                )
                                Text(
                                    text = "${String.format(Locale.US, "%.2f", mathResult.bearingDegrees)}° Clockwise from North",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SlateBlue
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Neumorphic Circular Compass Dial Composable
 */
@Composable
fun NeumorphicCompassDial(
    bearingDegrees: Float,
    sensorHeadingDegrees: Float,
    isSensorAvailable: Boolean,
    distanceKm: Double
) {
    // UI Rotation Angle = Bearing - Current Heading
    val rawUiRotation = bearingDegrees - sensorHeadingDegrees
    val animatedRotation by animateFloatAsState(
        targetValue = rawUiRotation,
        animationSpec = tween(durationMillis = 300),
        label = "CompassUiRotation"
    )

    val isAlignedWithKaaba = remember(rawUiRotation) {
        val norm = ((rawUiRotation % 360) + 360) % 360
        norm <= 6f || norm >= 354f
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Alignment Banner Status
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isAlignedWithKaaba) EmeraldGreen.copy(alpha = 0.25f) else DesertGold.copy(alpha = 0.15f))
                .border(1.dp, if (isAlignedWithKaaba) EmeraldGreen else DesertGold, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isAlignedWithKaaba) Icons.Default.Navigation else Icons.Default.CompassCalibration,
                    contentDescription = "Status",
                    tint = if (isAlignedWithKaaba) EmeraldGreen else SlateBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAlignedWithKaaba) "✦ DIRECTLY FACING KAABA! ✦" else "ALIGN PHONE TO FACE KAABA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isAlignedWithKaaba) EmeraldGreen else SlateBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Large Circular Dial Container
        NeumorphicInsetBox(
            modifier = Modifier.size(230.dp),
            shape = CircleShape,
            glowColor = if (isAlignedWithKaaba) EmeraldGreen.copy(alpha = 0.5f) else DesertGold.copy(alpha = 0.2f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // 1. Static Compass Outer Dial Canvas (Cardinal Points N, E, S, W counter-rotated by phone heading)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(-sensorHeadingDegrees)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 - 12f

                    // Ticks around compass rim
                    for (i in 0 until 360 step 15) {
                        val angleRad = Math.toRadians(i.toDouble() - 90.0)
                        val tickLen = if (i % 90 == 0) 14f else 8f
                        val start = Offset(
                            (center.x + (radius - tickLen) * cos(angleRad)).toFloat(),
                            (center.y + (radius - tickLen) * sin(angleRad)).toFloat()
                        )
                        val end = Offset(
                            (center.x + radius * cos(angleRad)).toFloat(),
                            (center.y + radius * sin(angleRad)).toFloat()
                        )
                        drawLineWidth(
                            color = if (i == 0) Color(0xFFFF5252) else SlateBlue.copy(alpha = 0.35f),
                            start = start,
                            end = end,
                            width = if (i % 90 == 0) 3f else 1.5f
                        )
                    }
                }

                // 2. Qibla Pointer Needle rotated by UI Rotation Angle (bearing - phone heading)
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(animatedRotation)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 - 24f

                    // Draw Kaaba Pointer Needle (Vibrant Emerald / Gold Arrow pointing UP towards 0° UI Angle)
                    val pointerPath = Path().apply {
                        moveTo(center.x, center.y - radius) // Point tip
                        lineTo(center.x - 14f, center.y - radius + 35f)
                        lineTo(center.x, center.y - radius + 25f)
                        lineTo(center.x + 14f, center.y - radius + 35f)
                        close()
                    }

                    drawPath(
                        path = pointerPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFFFC300), Color(0xFF2ECC71))
                        )
                    )

                    // Counter-pointer tail
                    val tailPath = Path().apply {
                        moveTo(center.x, center.y + radius * 0.6f)
                        lineTo(center.x - 8f, center.y + radius * 0.3f)
                        lineTo(center.x + 8f, center.y + radius * 0.3f)
                        close()
                    }
                    drawPath(tailPath, color = SlateBlue.copy(alpha = 0.3f))

                    // Line connecting center to tip
                    drawLine(
                        color = Color(0xFF2ECC71),
                        start = center,
                        end = Offset(center.x, center.y - radius + 25f),
                        strokeWidth = 3f
                    )
                }

                // 3. Central Dome Center Piece
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(DesertGold, Color(0xFF2ECC71))
                            )
                        )
                        .border(2.dp, AlabasterSand, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    PixelKaabaIcon(modifier = Modifier.size(26.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Readings Footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "QIBLA BEARING",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateBlue.copy(alpha = 0.6f)
                )
                Text(
                    text = "${bearingDegrees.toInt()}°",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SlateBlue
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PHONE HEADING",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateBlue.copy(alpha = 0.6f)
                )
                Text(
                    text = if (isSensorAvailable) "${sensorHeadingDegrees.toInt()}°" else "Sensor Off",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSensorAvailable) EmeraldGreen else RubyRed
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "MAKKAH DISTANCE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateBlue.copy(alpha = 0.6f)
                )
                Text(
                    text = "~${distanceKm.toInt()} km",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DesertGold
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLineWidth(
    color: Color,
    start: Offset,
    end: Offset,
    width: Float
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = width
    )
}
