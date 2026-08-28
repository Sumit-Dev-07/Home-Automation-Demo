package com.app.iot.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun nearbyDevicePermissions(): Array<String> {
	val permissions = mutableListOf<String>()
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
		permissions += Manifest.permission.NEARBY_WIFI_DEVICES
	}
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
		permissions += Manifest.permission.BLUETOOTH_SCAN
		permissions += Manifest.permission.BLUETOOTH_CONNECT
	}
	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
		permissions += Manifest.permission.ACCESS_FINE_LOCATION
		permissions += Manifest.permission.ACCESS_COARSE_LOCATION
	}
	return permissions.toTypedArray()
}

fun Context.hasNearbyDevicePermissions(): Boolean {
	return nearbyDevicePermissions().all { permission ->
		ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
	}
}

fun Activity.shouldShowNearbyPermissionRationale(): Boolean {
	return nearbyDevicePermissions().any { permission ->
		ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
	}
}

fun Context.openAppPermissionSettings() {
	val intent = Intent(
		Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
		Uri.fromParts("package", packageName, null)
	)
	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	startActivity(intent)
}
