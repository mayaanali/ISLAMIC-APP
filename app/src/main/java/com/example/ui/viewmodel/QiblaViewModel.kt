package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.data.AppContainer
import com.example.data.DefaultAppContainer
import com.example.utils.CalculatedPrayerTime
import com.example.utils.CalculationMethod
import com.example.utils.LocationAndMosqueManager
import com.example.utils.NearbyMosque
import com.example.utils.UserLocationState
import kotlinx.coroutines.flow.StateFlow

class QiblaViewModel(
    application: Application,
    private val container: AppContainer = DefaultAppContainer(application)
) : AndroidViewModel(application) {

    private val mosqueManager = container.locationAndMosqueManager

    val locationState: StateFlow<UserLocationState> = mosqueManager.locationState
    val calculatedPrayers: StateFlow<List<CalculatedPrayerTime>> = mosqueManager.calculatedPrayers
    val nearbyMosques: StateFlow<List<NearbyMosque>> = mosqueManager.nearbyMosques

    fun updateLocation(state: UserLocationState) {
        mosqueManager.updateLocation(state)
    }

    fun setCalculationMethod(method: CalculationMethod) {
        mosqueManager.setCalculationMethod(method)
    }

    fun startDeviceLocationUpdates(context: Context) {
        mosqueManager.startDeviceLocationUpdates(context)
    }

    fun toggleBookmarkMosque(mosqueId: String) {
        mosqueManager.toggleBookmarkMosque(mosqueId)
    }
}

