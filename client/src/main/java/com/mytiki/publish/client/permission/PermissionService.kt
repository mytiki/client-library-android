package com.mytiki.publish.client.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

/**
 * This class is responsible for handling permissions in the application. It provides a method to
 * request permissions and a callback to handle the result.
 */
class PermissionService {
  /**
   * A callback function that is invoked when the permissions request is completed. The function
   * receives a map where the keys are the requested permissions and the values are Booleans
   * indicating whether each permission was granted.
   */
  var permissionCallback: ((Map<Permission, Boolean>) -> Unit)? = null
  /**
   * This function is used to request permissions. It starts a new PermissionActivity and passes the
   * requested permissions as an extra in the intent.
   *
   * @param activity The activity from which the permissions request is made.
   * @param permissions The list of permissions to request.
   * @param permissionCallback The callback function to invoke when the permissions request is
   *   completed.
   */
  fun requestPermissions(
      activity: ComponentActivity,
      permissions: List<Permission>,
      permissionCallback: (Map<Permission, Boolean>) -> Unit,
  ) {
    this.permissionCallback = permissionCallback
    val permissionsWrapper = PermissionWrapper(permissions)
    val intent = Intent(activity, PermissionActivity::class.java)
    intent.putExtra("permissionsWrapper", permissionsWrapper)
    activity.startActivity(intent)
  }

  /**
   * Checks if a specific permission is granted.
   *
   * @param context The context from which the check is made.
   * @param permission The permission to check.
   * @return Boolean indicating whether the permission is granted.
   */
  fun isAuthorized(context: Context, permission: Permission): Boolean =
      when (permission) {
        Permission.CAMERA -> isPermissionGranted(Manifest.permission.CAMERA, context)
        Permission.MICROPHONE -> isPermissionGranted(Manifest.permission.RECORD_AUDIO, context)
        Permission.PHOTO_LIBRARY ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) ==
                  PackageManager.PERMISSION_GRANTED
            } else {
              isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, context)
            }
        Permission.VIDEO_LIBRARY ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) ==
                  PackageManager.PERMISSION_GRANTED
            } else {
              isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, context)
            }
        Permission.AUDIO_LIBRARY ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) ==
                  PackageManager.PERMISSION_GRANTED
            } else {
              isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, context)
            }
        Permission.FINE_LOCATION ->
            isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION, context)
        Permission.COARSE_LOCATION ->
            isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION, context)
        Permission.BACKGROUND_LOCATION ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
              isPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION, context)
            } else {
              isAuthorized(context, Permission.FINE_LOCATION) &&
                  isAuthorized(context, Permission.COARSE_LOCATION)
            }
        Permission.NOTIFICATIONS -> isNotificationPermissionGranted(context)
        Permission.CALENDAR -> isPermissionGranted(Manifest.permission.READ_CALENDAR, context)
        Permission.CONTACTS -> isPermissionGranted(Manifest.permission.READ_CONTACTS, context)
        Permission.REMINDERS -> isPermissionGranted(Manifest.permission.READ_CALENDAR, context)
        Permission.SPEECH_RECOGNITION ->
            isPermissionGranted(Manifest.permission.RECORD_AUDIO, context)
        Permission.HEALTH -> isPermissionGranted(Manifest.permission.BODY_SENSORS, context)
        Permission.MOTION ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
              isPermissionGranted(Manifest.permission.ACTIVITY_RECOGNITION, context)
            } else {
              isTrackingPermissionGranted(context)
            }
        Permission.TRACKING -> isTrackingPermissionGranted(context)
        Permission.BLUETOOTH_CONNECT ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
              isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT, context)
            } else {
              false
            }
      }

  /**
   * Checks if a specific Android permission is granted.
   *
   * @param permission The Android permission to check.
   * @param context The context from which the check is made.
   * @return Boolean indicating whether the permission is granted.
   */
  private fun isPermissionGranted(permission: String, context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) ==
        PackageManager.PERMISSION_GRANTED
  }

  /**
   * Checks if the application has the permission to show notifications.
   *
   * @param context The context from which the check is made.
   * @return Boolean indicating whether the permission is granted.
   */
  @SuppressLint("DiscouragedPrivateApi")
  private fun isNotificationPermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // For Android O and above, check if the app has the permission to show notifications and the
      // notifications are enabled
      val notificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.areNotificationsEnabled()
    } else {
      // For Android N and below, check if the app has the permission to show notifications
      val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
      try {
        val method =
            appOps.javaClass.getDeclaredMethod(
                "checkOp",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java)
        val op = method.invoke(appOps, 11, Binder.getCallingUid(), context.packageName) as Int
        op == AppOpsManager.MODE_ALLOWED
      } catch (e: Exception) {
        false
      }
    }
  }

  /**
   * Checks if the application has the permission for activity recognition.
   *
   * @param context The context from which the check is made.
   * @return Boolean indicating whether the permission is granted.
   */
  private fun isTrackingPermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      PackageManager.PERMISSION_GRANTED ==
          ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)
    } else {
      val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
      try {
        @Suppress("DEPRECATION")
        val mode =
            appOps.checkOpNoThrow(
                "android:activity_recognition", Binder.getCallingUid(), context.packageName)
        mode == AppOpsManager.MODE_ALLOWED
      } catch (e: Exception) {
        false
      }
    }
  }
}
