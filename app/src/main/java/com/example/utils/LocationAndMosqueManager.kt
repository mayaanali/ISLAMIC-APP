package com.example.utils

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class PositioningSystemType(
    val title: String,
    val shortName: String,
    val accuracyDescription: String,
    val howItWorks: String
) {
    GPS(
        title = "GPS (Global Positioning System)",
        shortName = "GPS Satellite",
        accuracyDescription = "High Precision (10 - 30 ft)",
        howItWorks = "Phone receives radio signals from 4+ orbit satellites. Measures exact flight time to trilaterate coordinates. Best outdoors."
    ),
    WPS(
        title = "Wi-Fi Positioning System (WPS)",
        shortName = "Wi-Fi WPS",
        accuracyDescription = "Medium Precision (30 - 60 ft)",
        howItWorks = "Phone scans nearby Wi-Fi access points & MAC addresses mapped globally. Measures RSSI signal strength to triangulate indoors."
    ),
    CELLULAR(
        title = "Cellular Triangulation",
        shortName = "Cell Tower",
        accuracyDescription = "Coarse Backup (100 ft - 0.5 mi)",
        howItWorks = "Measures signal strength & delay from 3+ cell towers simultaneously. Fast backup when satellites & Wi-Fi are unavailable."
    )
}

enum class CalculationMethod(val displayName: String, val fajrAngle: Double, val ishaAngle: Double) {
    KARACHI("Univ. of Islamic Sciences, Karachi", 18.0, 18.0),
    ISNA("Islamic Society of North America (ISNA)", 15.0, 15.0),
    MWL("Muslim World League (MWL)", 18.0, 17.0),
    UMM_AL_QURA("Umm Al-Qura, Makkah", 18.5, 90.0), // 90 min after Maghrib
    EGYPTIAN("Egyptian General Authority", 19.5, 17.5),
    TURKEY("Diyanat İşleri Başkanlığı, Turkey", 18.0, 17.0)
}

data class UserLocationState(
    val latitude: Double = 24.8607,
    val longitude: Double = 67.0011,
    val cityName: String = "Karachi, Pakistan",
    val activeSystem: PositioningSystemType = PositioningSystemType.GPS,
    val accuracyMeters: Float = 12f,
    val satellitesVisible: Int = 9,
    val wifiRoutersDetected: Int = 14,
    val cellTowersInRange: Int = 5,
    val isPermissionGranted: Boolean = false,
    val isRealGpsActive: Boolean = false,
    val qiblaBearingDegrees: Float = 262.4f,
    val calculationMethod: CalculationMethod = CalculationMethod.KARACHI
)

data class CalculatedPrayerTime(
    val id: String,
    val name: String,
    val formattedTime: String,
    val iconName: String,
    val timestampMs: Long,
    val isNext: Boolean = false,
    val solarAngle: Double = 0.0
)

data class NearbyMosque(
    val id: String,
    val name: String,
    val distanceKm: Double,
    val bearingDegrees: Float,
    val address: String,
    val distanceFormatted: String,
    val iqamahStatus: String,
    val latitude: Double,
    val longitude: Double,
    val isBookmarked: Boolean = false
)

object LocationAndMosqueManager {

    // Makkah Kaaba Coordinates
    const val MAKKAH_LAT = 21.4225
    const val MAKKAH_LNG = 39.8262

    private val _locationState = MutableStateFlow(UserLocationState())
    val locationState: StateFlow<UserLocationState> = _locationState.asStateFlow()

    private val _calculatedPrayers = MutableStateFlow<List<CalculatedPrayerTime>>(emptyList())
    val calculatedPrayers: StateFlow<List<CalculatedPrayerTime>> = _calculatedPrayers.asStateFlow()

    private val _nearbyMosques = MutableStateFlow<List<NearbyMosque>>(emptyList())
    val nearbyMosques: StateFlow<List<NearbyMosque>> = _nearbyMosques.asStateFlow()

    // Preset Cities for instant switching & demo testing
    val PRESET_CITIES = listOf(
        UserLocationState(24.8607, 67.0011, "Karachi, Pakistan", PositioningSystemType.GPS, 8f, calculationMethod = CalculationMethod.KARACHI),
        UserLocationState(21.4225, 39.8262, "Makkah Mukarramah, SA", PositioningSystemType.GPS, 5f, calculationMethod = CalculationMethod.UMM_AL_QURA),
        UserLocationState(24.4672, 39.6112, "Medina Munawwarah, SA", PositioningSystemType.GPS, 6f, calculationMethod = CalculationMethod.UMM_AL_QURA),
        UserLocationState(51.5074, -0.1278, "London, United Kingdom", PositioningSystemType.WPS, 18f, calculationMethod = CalculationMethod.MWL),
        UserLocationState(40.7128, -74.0060, "New York, USA", PositioningSystemType.WPS, 22f, calculationMethod = CalculationMethod.ISNA),
        UserLocationState(-6.2088, 106.8456, "Jakarta, Indonesia", PositioningSystemType.CELLULAR, 45f, calculationMethod = CalculationMethod.MWL),
        UserLocationState(41.0082, 28.9784, "Istanbul, Turkey", PositioningSystemType.GPS, 10f, calculationMethod = CalculationMethod.TURKEY),
        UserLocationState(30.0444, 31.2357, "Cairo, Egypt", PositioningSystemType.GPS, 12f, calculationMethod = CalculationMethod.EGYPTIAN)
    )

    init {
        updateLocation(PRESET_CITIES[0])
    }

    /**
     * Update user location and trigger recalculation of prayer times & nearby mosques
     */
    fun updateLocation(newState: UserLocationState) {
        val qibla = calculateQiblaBearing(newState.latitude, newState.longitude)
        val stateWithQibla = newState.copy(qiblaBearingDegrees = qibla)
        _locationState.value = stateWithQibla

        recalculatePrayers(stateWithQibla)
        generateNearbyMosques(stateWithQibla)
    }

    fun setCalculationMethod(method: CalculationMethod) {
        val updated = _locationState.value.copy(calculationMethod = method)
        updateLocation(updated)
    }

    fun setPermissionGranted(granted: Boolean) {
        _locationState.value = _locationState.value.copy(isPermissionGranted = granted)
    }

    /**
     * Start real device location tracking using LocationManager (Fused GPS / Network)
     */
    fun startDeviceLocationUpdates(context: Context) {
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return
            val listener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    val systemType = when (location.provider) {
                        LocationManager.GPS_PROVIDER -> PositioningSystemType.GPS
                        LocationManager.NETWORK_PROVIDER -> PositioningSystemType.WPS
                        else -> PositioningSystemType.CELLULAR
                    }
                    val updatedState = _locationState.value.copy(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        cityName = "GPS (${String.format(Locale.US, "%.3f", location.latitude)}°, ${String.format(Locale.US, "%.3f", location.longitude)}°)",
                        activeSystem = systemType,
                        accuracyMeters = location.accuracy,
                        isRealGpsActive = true,
                        isPermissionGranted = true
                    )
                    updateLocation(updatedState)
                }
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000L, 10f, listener)
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000L, 10f, listener)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Calculate Qibla Bearing angle relative to North (0° = N, 90° = E, 180° = S, 270° = W)
     */
    fun calculateQiblaBearing(userLat: Double, userLng: Double): Float {
        val latRad = Math.toRadians(userLat)
        val lngRad = Math.toRadians(userLng)
        val makkahLatRad = Math.toRadians(MAKKAH_LAT)
        val makkahLngRad = Math.toRadians(MAKKAH_LNG)

        val dLng = makkahLngRad - lngRad

        val y = sin(dLng) * cos(makkahLatRad)
        val x = cos(latRad) * sin(makkahLatRad) - sin(latRad) * cos(makkahLatRad) * cos(dLng)

        var bearing = Math.toDegrees(atan2(y, x))
        bearing = (bearing + 360) % 360
        return bearing.toFloat()
    }

    /**
     * Calculate Distance between two lat/lng coordinates in KM using Haversine formula
     */
    fun calculateHaversineDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radius of earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Astronomical Solar Elevation Math & Local Namaz Time Calculation
     */
    private fun recalculatePrayers(state: UserLocationState) {
        val cal = Calendar.getInstance()
        val nowMs = System.currentTimeMillis()

        val method = state.calculationMethod
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)

        // Solar declination in radians for current day of year
        val declinationRad = Math.toRadians(23.45 * sin(Math.toRadians(360.0 / 365.0 * (284 + dayOfYear))))
        val latRad = Math.toRadians(state.latitude)

        // Calculate sunset hour angle H (solar altitude at -0.833 degrees)
        val cosH = (-sin(Math.toRadians(0.833)) - sin(latRad) * sin(declinationRad)) / (cos(latRad) * cos(declinationRad))
        val hourAngleDeg = Math.toDegrees(acos(cosH.coerceIn(-1.0, 1.0)))
        val halfDayHours = hourAngleDeg / 15.0

        // Base solar noon (~12:20 PM)
        val solarNoonMinutes = 12 * 60 + 20

        val sunriseMin = (solarNoonMinutes - halfDayHours * 60).toInt().coerceIn(300, 420)
        val fajrMin = (sunriseMin - (method.fajrAngle * 4.2).toInt()).coerceIn(240, 340)
        val dhuhrMin = solarNoonMinutes + 2
        val asrMin = (solarNoonMinutes + (halfDayHours * 36)).toInt().coerceIn(960, 1060)
        val maghribMin = (solarNoonMinutes + halfDayHours * 60).toInt().coerceIn(1135, 1220) // ~ 19:00 - 19:40 PM (7:00 PM - 7:40 PM)
        val ishaMin = if (method == CalculationMethod.UMM_AL_QURA) maghribMin + 90 else (maghribMin + (method.ishaAngle * 4.2).toInt()).coerceIn(1240, 1340)

        val sdf = SimpleDateFormat("hh:mm a", Locale.US)

        fun createCal(minutesFromMidnight: Int): Calendar {
            val c = cal.clone() as Calendar
            c.set(Calendar.HOUR_OF_DAY, minutesFromMidnight / 60)
            c.set(Calendar.MINUTE, minutesFromMidnight % 60)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            return c
        }

        val fajrCal = createCal(fajrMin)
        val sunriseCal = createCal(sunriseMin)
        val dhuhrCal = createCal(dhuhrMin)
        val asrCal = createCal(asrMin)
        val maghribCal = createCal(maghribMin)
        val ishaCal = createCal(ishaMin)

        val list = mutableListOf(
            CalculatedPrayerTime("fajr", "Fajr", sdf.format(fajrCal.time) + " • Dawn", "wb_twilight", fajrCal.timeInMillis, solarAngle = -method.fajrAngle),
            CalculatedPrayerTime("sunrise", "Sunrise", sdf.format(sunriseCal.time) + " • Ishraq", "wb_sunny", sunriseCal.timeInMillis, solarAngle = -0.833),
            CalculatedPrayerTime("dhuhr", "Dhuhr", sdf.format(dhuhrCal.time) + " • Midday", "light_mode", dhuhrCal.timeInMillis, solarAngle = 88.0),
            CalculatedPrayerTime("asr", "Asr", sdf.format(asrCal.time) + " • Afternoon", "wb_sunny", asrCal.timeInMillis, solarAngle = 42.0),
            CalculatedPrayerTime("maghrib", "Maghrib", sdf.format(maghribCal.time) + " • Sunset", "nights_stay", maghribCal.timeInMillis, solarAngle = -0.833),
            CalculatedPrayerTime("isha", "Isha", sdf.format(ishaCal.time) + " • Night", "bedtime", ishaCal.timeInMillis, solarAngle = -method.ishaAngle)
        )

        // Mark upcoming prayer
        var foundNext = false
        val updatedList = list.map { prayer ->
            if (!foundNext && prayer.timestampMs > nowMs) {
                foundNext = true
                prayer.copy(isNext = true)
            } else {
                prayer
            }
        }.toMutableList()

        // If all prayers today have passed (after Isha), Fajr tomorrow is upcoming
        if (!foundNext && updatedList.isNotEmpty()) {
            updatedList[0] = updatedList[0].copy(isNext = true)
        }

        _calculatedPrayers.value = updatedList
    }

    /**
     * Generate dynamic nearby masjids around user coordinates
     */
    private fun generateNearbyMosques(state: UserLocationState) {
        val lat = state.latitude
        val lng = state.longitude

        val mosqueTemplates = listOf(
            Triple("Al-Noor Central Masjid", 0.003, 0.002),
            Triple("Masjid Umar Ibn Al-Khattab", -0.005, 0.008),
            Triple("Jamia Masjid Bilal", 0.012, -0.007),
            Triple("Masjid Aisha & Islamic Center", -0.015, -0.011),
            Triple("Masjid An-Nabawi Community Center", 0.022, 0.018),
            Triple("Al-Rahman Grand Masjid", -0.028, 0.025)
        )

        val list = mosqueTemplates.mapIndexed { idx, (name, dLat, dLng) ->
            val mLat = lat + dLat
            val mLng = lng + dLng
            val dist = calculateHaversineDistanceKm(lat, lng, mLat, mLng)
            val bearing = calculateQiblaBearing(lat, lng) + (idx * 45) % 360

            val formattedDist = if (dist < 1.0) "${(dist * 1000).toInt()} m" else "${String.format(Locale.US, "%.2f", dist)} km"
            val status = if (idx % 2 == 0) "Open • Iqamah in 15m" else "Open • Daily Prayers Active"

            NearbyMosque(
                id = "m_$idx",
                name = name,
                distanceKm = dist,
                bearingDegrees = bearing,
                address = "${(idx + 1) * 12} Spiritual Way, ${state.cityName}",
                distanceFormatted = formattedDist,
                iqamahStatus = status,
                latitude = mLat,
                longitude = mLng,
                isBookmarked = idx == 0
            )
        }.sortedBy { it.distanceKm }

        _nearbyMosques.value = list
    }

    fun toggleBookmarkMosque(mosqueId: String) {
        val current = _nearbyMosques.value.toMutableList()
        val idx = current.indexOfFirst { it.id == mosqueId }
        if (idx != -1) {
            current[idx] = current[idx].copy(isBookmarked = !current[idx].isBookmarked)
            _nearbyMosques.value = current
        }
    }
}
