package com.mytiki.publish.client.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
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
import java.lang.reflect.InvocationTargetException

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
    if (!Permission.BACKGROUND_LOCATION.isAuthorized(this)) {
      if (Permission.FINE_LOCATION.isAuthorized(this) &&
          Permission.COARSE_LOCATION.isAuthorized(this)) {
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
      if (!areNotificationsEnabled()) {
        val intent =
            Intent().apply {
              action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
              putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
        startActivity(intent)
        isNotificationPermissionRequested = true
      } else {
        callbackMap[Permission.NOTIFICATIONS] = areNotificationsEnabled()
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
            callbackMap[Permission.TRACKING] = Permission.TRACKING.isAuthorized(this)
            diferentPermissions.remove(Permission.TRACKING)
            handleDifferentPermissions()
          }
      trackingPermissionLauncher?.launch(intent)
    }
  }

  override fun onResume() {
    super.onResume()
    if (isNotificationPermissionRequested) {
      callbackMap[Permission.NOTIFICATIONS] = areNotificationsEnabled()
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
      if (!it.isAuthorized(this)) {
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

  private fun areNotificationsEnabled(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      val notificationManager =
          getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.areNotificationsEnabled()
    } else {
      val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
      val appInfo = applicationContext.applicationInfo
      val pkg = applicationContext.applicationContext.packageName
      val uid = appInfo.uid

      try {
        val appOpsClass = Class.forName(AppOpsManager::class.java.name)
        val checkOpNoThrowMethod =
            appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE, Integer.TYPE, String::class.java)
        val opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION")

        val value = opPostNotificationValue.get(Integer::class.java) as Int
        checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) as Int == AppOpsManager.MODE_ALLOWED
      } catch (e: ClassNotFoundException) {
        true
      } catch (e: NoSuchMethodException) {
        true
      } catch (e: NoSuchFieldException) {
        true
      } catch (e: InvocationTargetException) {
        true
      } catch (e: IllegalAccessException) {
        true
      }
    }
  }
}
