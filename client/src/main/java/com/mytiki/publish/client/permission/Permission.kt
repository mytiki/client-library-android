/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */
package com.mytiki.publish.client.permission

import android.Manifest
import android.os.Build
import java.io.Serializable

enum class Permission(val code: Int, private val permission: String? = null) : Serializable {
  CAMERA(100, Manifest.permission.CAMERA),
  MICROPHONE(101, Manifest.permission.RECORD_AUDIO),
  PHOTO_LIBRARY(
      102,
  ),
  VIDEO_LIBRARY(104),
  AUDIO_LIBRARY(112),
  FINE_LOCATION(103, Manifest.permission.ACCESS_FINE_LOCATION),
  COARSE_LOCATION(116, Manifest.permission.ACCESS_COARSE_LOCATION),
  BACKGROUND_LOCATION(105, Manifest.permission.ACCESS_BACKGROUND_LOCATION),
  NOTIFICATIONS(106),
  CALENDAR(107, Manifest.permission.READ_CALENDAR),
  CONTACTS(108, Manifest.permission.READ_CONTACTS),
  REMINDERS(109, Manifest.permission.READ_CALENDAR),
  SPEECH_RECOGNITION(110, Manifest.permission.RECORD_AUDIO),
  HEALTH(111, Manifest.permission.BODY_SENSORS),
  MOTION(113, "android.permission.ACTIVITY_RECOGNITION"),
  TRACKING(114),
  BLUETOOTH_CONNECT(115);

  val displayName
    get() = name.lowercase().replace('_', ' ')

  companion object {
    fun fromString(value: String): Permission? {
      return Permission.entries.firstOrNull() { it.name == value }
    }
  }

  fun permission(): String? {
    return when (this) {
      PHOTO_LIBRARY ->
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
          } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
          }
      VIDEO_LIBRARY ->
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_VIDEO
          } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
          }
      AUDIO_LIBRARY ->
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
          } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
          }
      BLUETOOTH_CONNECT ->
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Manifest.permission.BLUETOOTH_CONNECT
          } else null
      else -> permission
    }
  }

  override fun toString(): String = this.name
}
