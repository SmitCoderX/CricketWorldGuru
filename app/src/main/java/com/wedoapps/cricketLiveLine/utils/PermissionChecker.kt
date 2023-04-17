package com.wedoapps.cricketLiveLine.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi

class PermissionChecker(private val context: Context) {

    fun checkAccessFineLocationPermission(): Boolean {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val res = context.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkNotificationPermission(): Boolean {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        val res = context.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun checkWriteExternalPermission(): Boolean {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val res = context.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun checkReadExternalPermission(): Boolean {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val res = context.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun checkCameraPermission(): Boolean {
        val permission = Manifest.permission.CAMERA
        val res = context.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

}