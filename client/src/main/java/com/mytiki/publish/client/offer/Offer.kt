package com.mytiki.publish.client.offer

import com.mytiki.publish.client.permission.Permission
import org.json.JSONArray
import org.json.JSONObject

class Offer(
    val description: String,
    val rewards: List<Reward>,
    val use: Use,
    val tags: List<Tag>,
    val ptr: String,
    val permissions: List<Permission>? = null,
    val mutable: Boolean = true,
    val active: Boolean = false
) {
  companion object {
    fun fromJson(json: JSONObject, ptr: String): Offer {
      val rewards = json.getJSONArray("rewards")
      val use = json.getJSONObject("use")
      val tags = json.getJSONArray("tags")
      val permissions = json.optJSONArray("permissions")
      return Offer(
          description = json.getString("description"),
          rewards = (0 until rewards.length()).map { Reward.fromJson(rewards.getJSONObject(it)) },
          use = Use.from(use),
          tags = (0 until tags.length()).map { Tag.from(tags.getString(it)) },
          ptr = ptr,
          permissions =
              if (permissions != null)
                  (0 until permissions.length()).mapNotNull {
                    Permission.fromJson(permissions.getJSONObject(it))
                  }
              else null,
          mutable = json.optBoolean("mutable", true),
          active = json.optBoolean("active", false))
    }
  }

  fun toJson(): JSONObject {
    return JSONObject()
        .put("description", description)
        .put("rewards", JSONArray(rewards.map { it.toJson() }))
        .put("use", use.toJson())
        .put("tags", JSONArray(tags.map { it.value }))
        .put("permissions", JSONArray(permissions?.map { it.toJson() }))
        .put("mutable", mutable)
        .put("active", active)
  }
}
