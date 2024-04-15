package com.mytiki.publish.client.permission

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mytiki.publish.client.TikiClient
import java.lang.reflect.InvocationTargetException

class PermissionActivity : AppCompatActivity() {

  companion object {
    var instance: PermissionActivity? = null
      private set
  }

  private var isNotificationPermissionRequested = false

  private var trackingPermissionLauncher: ActivityResultLauncher<Intent>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    instance = this
    val permissions =
        intent.extras
            ?.getStringArray("permissions")
            ?.map { Permission.fromString(it) }
            ?.filterNotNull()
    if (permissions != null) {
      requestPermission(0, permissions)
    } else {
      TikiClient.permission.permissionCallback?.let { it(false) }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    instance = null
  }

  private fun requestPermission(index: Int, permissions: List<Permission>) {
    if (index >= permissions.size) {
      TikiClient.permission.permissionCallback?.let { it(true) }
      return
    }
    val callback: (Boolean) -> Unit = { granted: Boolean ->
      if (granted) {
        requestPermission(index + 1, permissions)
      } else {
        TikiClient.permission.permissionCallback?.let { it(false) }
      }
    }

    when (permissions[index]) {
      Permission.BACKGROUND_LOCATION ->
          requestBackgroundLocationPermission(permissions[index], callback)
      Permission.NOTIFICATIONS -> requestNotificationPermission(callback)
      Permission.TRACKING -> requestTrackingPermission(callback)
      Permission.BLUETOOTH_CONNECT ->
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
              launchPermission(permissions[index], callback)
      else -> launchPermission(permissions[index], callback)
    }
  }

  private fun launchPermission(permission: Permission, callback: (Boolean) -> Unit) {
    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
          callback(it)
        }
    if (!permission.isAuthorized(this)) {
      requestPermissionLauncher.launch(permission.permission())
    } else callback(true)
  }

  private fun requestBackgroundLocationPermission(
      permission: Permission,
      callback: (Boolean) -> Unit
  ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      if (Permission.FINE_LOCATION.isAuthorized(this) &&
          Permission.COARSE_LOCATION.isAuthorized(this)) {
        launchPermission(permission, callback)
      } else {
        callback(false)
      }
    }
  }

  private fun requestNotificationPermission(callback: ((Boolean) -> Unit)) {
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
        callback(areNotificationsEnabled())
      }
    } else {
      callback(true)
    }
  }

  override fun onResume() {
    super.onResume()
    if (isNotificationPermissionRequested) {
      TikiClient.permission.permissionCallback?.let { it(areNotificationsEnabled()) }
      isNotificationPermissionRequested = false
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

  @SuppressLint("AnnotateVersionCheck")
  private fun requestTrackingPermission(callback: ((Boolean) -> Unit)) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      callback(false)
    } else {
      val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
      intent.data = Uri.fromParts("package", packageName, null)
      trackingPermissionLauncher =
          registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            callback(Permission.TRACKING.isAuthorized(this))
          }
      trackingPermissionLauncher?.launch(intent)
    }
  }
}
