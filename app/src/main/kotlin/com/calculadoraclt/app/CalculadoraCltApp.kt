package com.calculadoraclt.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.calculadoraclt.app.ads.LaunchAdManager
import com.google.android.gms.ads.MobileAds

class CalculadoraCltApp : Application() {

    lateinit var launchAdManager: LaunchAdManager
        private set

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)

        launchAdManager = LaunchAdManager(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(launchAdManager)
        launchAdManager.loadAd()
    }
}
