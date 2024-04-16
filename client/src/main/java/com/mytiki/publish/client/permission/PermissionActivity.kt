package com.mytiki.publish.client.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mytiki.publish.client.TikiClient

class PermissionActivity : AppCompatActivity() {

  private val callbackMap = mutableMapOf<Permission, Boolean>()
  private var diferentPermissions = mutableListOf<Permission>()
  private var normalPermissions = mutableListOf<Permission>()
  private var isNotificationPermissionRequested = false
  private var trackingPermissionLauncher: ActivityResultLauncher<Intent>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val permissions =
        intent.extras?.getStringArray("permissions")?.mapNotNull { Permission.fromString(it) }

    if (permissions != null) {
      checkPermissions(permissions)
      val permissionString = normalPermissions.mapNotNull { it.permission() }
      val requestPermissionLauncher =
          registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
              requestMap ->
            requestMap.onEachIndexed { index, map ->
              if (map.key == normalPermissions[index].permission()) {
                callbackMap[normalPermissions[index]] = map.value
              }
            }
            if (diferentPermissions.isEmpty()) {
              TikiClient.permission.permissionCallback?.let { it -> it(callbackMap) }
              this.finish()
            } else {
              handleDifferentPermissions()
            }
          }
      requestPermissionLauncher.launch(permissionString.toTypedArray())
      //      ActivityCompat.requestPermissions(this, permissionString.toTypedArray(), REQUEST_CODE)
    } else {
      TikiClient.permission.permissionCallback
    }
  }

  fun handleDifferentPermissions() {
    if (diferentPermissions.isNotEmpty()) {
      when (diferentPermissions[0]) {
        Permission.BACKGROUND_LOCATION -> requestLocation()
        Permission.NOTIFICATIONS -> requestNotificationPermission()
        Permission.TRACKING -> requestTrackingPermission()
        else -> {
          TikiClient.permission.permissionCallback?.let { it -> it(callbackMap) }
          this.finish()
        }
      }
    } else {
      TikiClient.permission.permissionCallback?.let { it -> it(callbackMap) }
      this.finish()
    }
  }

  fun requestLocation() {
    if (!TikiClient.isPermissionAuthorized(this, Permission.BACKGROUND_LOCATION)) {
      if (TikiClient.isPermissionAuthorized(this, Permission.FINE_LOCATION) &&
          TikiClient.isPermissionAuthorized(this, Permission.COARSE_LOCATION)) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            Permission.BACKGROUND_LOCATION.code)
      } else {
        callbackMap[Permission.BACKGROUND_LOCATION] = false
        diferentPermissions.remove(Permission.BACKGROUND_LOCATION)
        handleDifferentPermissions()
      }
    } else {
      callbackMap[Permission.BACKGROUND_LOCATION] = true
      diferentPermissions.remove(Permission.BACKGROUND_LOCATION)
      handleDifferentPermissions()
    }
  }

  private fun requestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (!TikiClient.isPermissionAuthorized(this, Permission.NOTIFICATIONS)) {
        val intent =
            Intent().apply {
              action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
              putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
        startActivity(intent)
        isNotificationPermissionRequested = true
      } else {
        callbackMap[Permission.NOTIFICATIONS] =
            TikiClient.isPermissionAuthorized(this, Permission.NOTIFICATIONS)
        isNotificationPermissionRequested = false
        diferentPermissions.remove(Permission.NOTIFICATIONS)
        handleDifferentPermissions()
      }
    } else {
      callbackMap[Permission.NOTIFICATIONS] = true
      isNotificationPermissionRequested = false
      diferentPermissions.remove(Permission.NOTIFICATIONS)
      handleDifferentPermissions()
    }
  }

  @SuppressLint("AnnotateVersionCheck")
  private fun requestTrackingPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      callbackMap[Permission.TRACKING] = false
      diferentPermissions.remove(Permission.TRACKING)
      handleDifferentPermissions()
    } else {
      val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
      intent.data = Uri.fromParts("package", packageName, null)
      trackingPermissionLauncher =
          registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            callbackMap[Permission.TRACKING] =
                TikiClient.isPermissionAuthorized(this, Permission.TRACKING)
            diferentPermissions.remove(Permission.TRACKING)
            handleDifferentPermissions()
          }
      trackingPermissionLauncher?.launch(intent)
    }
  }

  override fun onResume() {
    super.onResume()
    if (isNotificationPermissionRequested) {
      callbackMap[Permission.NOTIFICATIONS] =
          TikiClient.isPermissionAuthorized(this, Permission.NOTIFICATIONS)
      isNotificationPermissionRequested = false
      diferentPermissions.remove(Permission.NOTIFICATIONS)
      handleDifferentPermissions()
    }
  }

  override fun onRequestPermissionsResult(
      requestCode: Int,
      permissions: Array<out String>,
      grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == Permission.BACKGROUND_LOCATION.code) {
      callbackMap[Permission.BACKGROUND_LOCATION] =
          grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
      diferentPermissions.remove(Permission.BACKGROUND_LOCATION)
      handleDifferentPermissions()
    }
  }

  private fun checkPermissions(permissions: List<Permission>) {
    permissions.forEach {
      if (!TikiClient.isPermissionAuthorized(this, it)) {
        if (it == Permission.BACKGROUND_LOCATION ||
            it == Permission.NOTIFICATIONS ||
            it == Permission.TRACKING ||
            it == Permission.BLUETOOTH_CONNECT) {
          diferentPermissions.add(it)
        } else {
          normalPermissions.add(it)
        }
      } else {
        callbackMap[it] = true
      }
    }
  }
}
