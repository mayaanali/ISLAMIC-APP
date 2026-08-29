package com.example.data

import android.content.Context
import com.example.utils.LocationAndMosqueManager
import com.example.utils.PermissionManager

interface AppContainer {
    val database: AppDatabase
    val repository: AppRepository
    val locationAndMosqueManager: LocationAndMosqueManager
    val permissionManager: PermissionManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    override val repository: AppRepository by lazy {
        AppRepository(context, database)
    }

    override val locationAndMosqueManager: LocationAndMosqueManager
        get() = LocationAndMosqueManager

    override val permissionManager: PermissionManager
        get() = PermissionManager
}

