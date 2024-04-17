package com.mytiki.publish.client.permission

import java.io.Serializable

class PermissionWrapper(
    val permissions: List<Permission>,
) : Serializable
