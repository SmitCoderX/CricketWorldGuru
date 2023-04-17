package com.wedoapps.cricketLiveLine.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

object ShowLogToast {
    //
    fun showLog(message: String?) {
        val LOG_TAG: String = ReusedMethod.Companion.getApplicationName(
            CricketGuruApplication.instance!!
        ) + "_LOG"
        Log.d(LOG_TAG, message!!)
    }

    fun showLog(tag: String?, message: String?) {
        if (Constants.LOG_DEBUG) Log.d(tag, message!!)
    }

    fun showLogError(tag: String?, message: String?) {
        if (Constants.LOG_DEBUG) Log.e(tag, message!!)
    }

    fun showLogInfo(tag: String?, message: String?) {
        if (Constants.LOG_DEBUG) Log.e(tag, message!!)
    }

    fun showToast(context: Context?, message: String?, time: Int) {
        if (time == 0) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}