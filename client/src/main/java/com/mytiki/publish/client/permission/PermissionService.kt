package com.mytiki.publish.client.permission

import android.content.Intent
import androidx.activity.ComponentActivity

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
    val permissionsList = permissions.map { it.toString() }
    val intent = Intent(activity, PermissionActivity::class.java)
    intent.putExtra("permissions", permissionsList.toTypedArray())
    activity.startActivity(intent)
  }
}
