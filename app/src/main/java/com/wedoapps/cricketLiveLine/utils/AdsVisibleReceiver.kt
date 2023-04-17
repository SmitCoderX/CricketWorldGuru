@file:Suppress("DEPRECATION")

package com.wedoapps.cricketLiveLine.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

open class AdsVisibleReceiver : BroadcastReceiver() {
    //private var isVisibleAds = false

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val isAdsVisible = intent.getBooleanExtra("adsVisible", false)
       // isVisibleAds = isAdsVisible
        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onAdsVisibilityNotifyChanged(isAdsVisible)
        }
    }

    interface ConnectivityReceiverListener {
        fun onAdsVisibilityNotifyChanged(isAdsVisibleReceiver: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
       // private var isAdsVisibleReceiver: Boolean = false
    }
}