package com.wedoapps.cricketLiveLine.utils

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.google.firebase.crashlytics.FirebaseCrashlytics

class CricketGuruApplication : Application() {
    init {
        appContext = this
        instance = this

    }

    fun setConnectivityListener(listener: AdsVisibleReceiver.ConnectivityReceiverListener?) {
        AdsVisibleReceiver.connectivityReceiverListener = listener
    }

    companion object {

        val GPS_CHIP_DEBUG = false
        private val USE_MOCK_LOCATION = false
        var TAG = "Khai_lagai_calculator"
        lateinit var appContext: Context
        lateinit var currentContext: Context
        var isUIAvailable = false
        var isServerAvailable = false
        var isBackground = false
        var isActivated = false
        var isInitialized = false
        var isReady = false
        var isConnectedToInternet = false
        var isConnectedToServer = false
        var isGooglePlayServicesAvailable = false
        var instance: CricketGuruApplication? = null

        @Synchronized
        fun sharedInstance(): CricketGuruApplication {
            if (instance == null) instance = CricketGuruApplication()
            return instance as CricketGuruApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.log("Start logging!")
        MultiDex.install(this)
    }
}