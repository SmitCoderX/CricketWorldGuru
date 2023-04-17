package com.wedoapps.cricketLiveLine.ui

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.wedoapps.cricketLiveLine.R
import com.wedoapps.cricketLiveLine.utils.Constants.SPLASH_DELAY
import com.wedoapps.cricketLiveLine.utils.ReusedMethod
import kotlinx.coroutines.*


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ReusedMethod.setStatusBarGradiant(this)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(java.lang.Runnable {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY)

    }

    override fun onStart() {
        super.onStart()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}