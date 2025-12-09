package com.example.miiproyecto1

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class InventoryApplication : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}
