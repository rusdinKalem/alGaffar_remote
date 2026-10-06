package com.roesch.jwsalgaffar.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat

class PermissionManager(private var context: Context) {
    private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED

    fun requestPermissions() {
        val permissionsToRequest = permissions.filter {
            ActivityCompat.checkSelfPermission(context, it) != PERMISSION_GRANTED
        }
        if (permissionsToRequest.isNotEmpty() && context is Activity) {
            ActivityCompat.requestPermissions(context as Activity, permissionsToRequest.toTypedArray(), 5)
        } else {
            Log.d("PERMISSIONS", "All permissions already granted")
        }
    }
}