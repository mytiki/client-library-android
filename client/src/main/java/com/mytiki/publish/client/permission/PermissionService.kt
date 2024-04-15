package com.mytiki.publish.client.permission

import android.content.Intent
import androidx.activity.ComponentActivity

class PermissionService {
  var permissionCallback: ((Boolean) -> Unit)? = null

  fun requestPermissions(
      activity: ComponentActivity,
      permissions: List<Permission>,
      permissionCallback: (Boolean) -> Unit,
  ) {
    this.permissionCallback = permissionCallback
    val permissionsList = permissions.map { it.toString() }
    val intent = Intent(activity, PermissionActivity::class.java)
    intent.putExtra("permissions", permissionsList.toTypedArray())
    activity.startActivity(intent)
  }
}
