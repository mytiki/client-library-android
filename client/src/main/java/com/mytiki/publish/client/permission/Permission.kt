/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */
package com.mytiki.publish.client.permission

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


enum class Permission(val code: Int) {
    CAMERA(100),
    MICROPHONE(101),
    PHOTO_LIBRARY(102),
    VIDEO_LIBRARY(104),
    AUDIO_LIBRARY(112),
    LOCATION_IN_USE(103),
    BACKGROUND_LOCATION(105),
    NOTIFICATIONS(106),
    CALENDAR(107),
    CONTACTS(108),
    REMINDERS(109),
    SPEECH_RECOGNITION(110),
    HEALTH(111),
    MOTION(113),
    TRACKING(114),
    BLUETOOTH_CONNECT(115);

    val displayName
        get() = name.lowercase().replace('_', ' ')

    fun isAuthorized(context: Context): Boolean = when (this) {
        CAMERA -> isPermissionGranted(Manifest.permission.CAMERA, context)
        MICROPHONE -> isPermissionGranted(Manifest.permission.RECORD_AUDIO, context)
        PHOTO_LIBRARY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, context)
            }
        VIDEO_LIBRARY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_VIDEO
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, context)
            }
        AUDIO_LIBRARY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, context)
            }
        LOCATION_IN_USE -> isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION, context) &&
                isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION, context)

        BACKGROUND_LOCATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            isPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION, context)
        } else {
            isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION, context) &&
                    isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION, context)
        }

        NOTIFICATIONS -> isNotificationPermissionGranted(context)
        CALENDAR -> isPermissionGranted(Manifest.permission.READ_CALENDAR, context)
        CONTACTS -> isPermissionGranted(Manifest.permission.READ_CONTACTS, context)
        REMINDERS -> isPermissionGranted(Manifest.permission.READ_CALENDAR, context)
        SPEECH_RECOGNITION -> isPermissionGranted(Manifest.permission.RECORD_AUDIO, context)
        HEALTH -> isPermissionGranted(Manifest.permission.BODY_SENSORS, context)
        MOTION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isPermissionGranted(Manifest.permission.ACTIVITY_RECOGNITION, context)
        } else {
            isTrackingPermissionGranted(context)
        }

        TRACKING -> isTrackingPermissionGranted(context)
        BLUETOOTH_CONNECT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isPermissionGranted(Manifest.permission.BLUETOOTH_CONNECT, context)
        } else {
            false
        }
    }

    fun requestAuth(
        context: ActivityCompat.OnRequestPermissionsResultCallback,
        onRequestResult: ((Boolean) -> Unit) = {}
    ) {
        when (this) {
            CAMERA -> requestPermission(context, Manifest.permission.CAMERA, code)
            MICROPHONE -> requestPermission(
                context,
                Manifest.permission.RECORD_AUDIO,
                code
            )

            PHOTO_LIBRARY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    code
                )
            } else {
                requestPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    code
                )
            }

            VIDEO_LIBRARY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermission(
                    context,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    code
                )
            } else {
                requestPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    code
                )
            }

            AUDIO_LIBRARY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermission(
                    context,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    code
                )
            } else {
                requestPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    code
                )
            }

            LOCATION_IN_USE ->
                ActivityCompat.requestPermissions(
                    context as Activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    code
                )

            BACKGROUND_LOCATION -> requestBackgroundLocationPermission(context)
            NOTIFICATIONS -> requestNotificationPermission(context as Context, onRequestResult)
            CALENDAR -> requestPermission(
                context,
                Manifest.permission.READ_CALENDAR,
                code
            )

            CONTACTS -> requestPermission(
                context,
                Manifest.permission.READ_CONTACTS,
                code
            )

            REMINDERS -> requestPermission(
                context,
                Manifest.permission.READ_CALENDAR,
                code
            )

            SPEECH_RECOGNITION -> requestPermission(
                context,
                Manifest.permission.RECORD_AUDIO,
                code
            )

            HEALTH -> requestPermission(
                context,
                Manifest.permission.BODY_SENSORS,
                code
            )

            MOTION -> requestActivityRecognitionPermission(context, code)
            TRACKING -> requestTrackingPermission(context, onRequestResult)
            BLUETOOTH_CONNECT -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermission(context, Manifest.permission.BLUETOOTH_CONNECT, code)
            }
        }
    }

    private fun isPermissionGranted(permission: String, context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(
        activity: ActivityCompat.OnRequestPermissionsResultCallback,
        permission: String,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(
            activity as Activity,
            arrayOf(permission),
            requestCode
        )
    }

    private fun requestBackgroundLocationPermission(
        activity: ActivityCompat.OnRequestPermissionsResultCallback
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (
                ActivityCompat.checkSelfPermission(
                    activity as Context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    activity as Context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) throw Exception(
                "ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION permission is required to request background location permission"
            )
            ActivityCompat.requestPermissions(
                activity as Activity,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION.code
            )
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Android O and above, check if the app has the permission to show notifications and the notifications are enabled
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.areNotificationsEnabled()
        } else {
            // For Android N and below, check if the app has the permission to show notifications
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            try {
                val method = appOps.javaClass.getDeclaredMethod(
                    "checkOp",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java
                )
                val op =
                    method.invoke(appOps, 11, Binder.getCallingUid(), context.packageName) as Int
                op == AppOpsManager.MODE_ALLOWED
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun requestNotificationPermission(
        context: Context,
        onRequestResult: ((Boolean) -> Unit)
    ) {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        } else {
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo.uid)
        }
        context.startActivity(intent)
        onRequestResult(false)
    }

    private fun isTrackingPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        } else {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            try {
                @Suppress("DEPRECATION") val mode = appOps.checkOpNoThrow(
                    "android:activity_recognition",
                    Binder.getCallingUid(),
                    context.packageName
                )
                mode == AppOpsManager.MODE_ALLOWED
            } catch (e: Exception) {
                false
            }
        }
    }

    @SuppressLint("AnnotateVersionCheck")
    private fun requestTrackingPermission(
        context: ActivityCompat.OnRequestPermissionsResultCallback,
        onRequestResult: ((Boolean) -> Unit)
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            onRequestResult(false)
        } else {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", (context as Context).packageName, null)
            (context as Context).startActivity(intent)
            onRequestResult(false)
        }
    }

    fun requestActivityRecognitionPermission(
        activity: ActivityCompat.OnRequestPermissionsResultCallback,
        code: Int,
    ) {
        val permission = "android.permission.ACTIVITY_RECOGNITION"
        ActivityCompat.requestPermissions(activity as Activity, arrayOf(permission), code)
    }

}
