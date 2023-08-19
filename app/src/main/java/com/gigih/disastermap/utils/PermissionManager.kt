package com.gigih.disastermap.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.gigih.disastermap.databinding.ActivityMapsBinding

class PermissionManager(private val activity: FragmentActivity, private val binding: ActivityMapsBinding) {

    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val notificationPermission = Manifest.permission.ACCESS_NOTIFICATION_POLICY

    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Check if any of the permissions are not granted
            val isAnyPermissionNotGranted = permissions.values.any { !it }

            if (isAnyPermissionNotGranted) {
                showPermissionSettings()
            } else {
                MessageHelper.showSnackbar(binding.root, "Enter a province name to search.")
            }
        }

    fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        permissionsToRequest.add(locationPermission)
        permissionsToRequest.add(notificationPermission)

        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    private fun showPermissionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", activity.packageName, null)
        intent.data = uri
        activity.startActivity(intent)
    }

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }
}
