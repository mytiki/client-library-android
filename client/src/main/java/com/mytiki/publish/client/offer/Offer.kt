package com.mytiki.publish.client.offer

import com.mytiki.publish.client.permission.Permission
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

class Offer
private constructor(
    val description: String,
    val rewards: List<Reward>,
    val uses: List<Use>,
    val tags: List<Tag>,
    val ptr: String,
    val permissions: List<Permission>?,
    val mutable: Boolean,
    var id: String,
) {

  class Builder {
    private var description: String = ""
    private var rewards: List<Reward> = emptyList()
    private var use: List<Use> = emptyList()
    private var tags: List<Tag> = emptyList()
    private var ptr: String = ""
    private var permissions: List<Permission>? = null
    private var mutable: Boolean = true
    private var id: String = UUID.randomUUID().toString()

    fun description(description: String) = apply { this.description = description }

    fun rewards(rewards: List<Reward>) = apply { this.rewards = rewards }

    fun use(use: List<Use>) = apply { this.use = use }

    fun tags(tags: List<Tag>) = apply { this.tags = tags }

    fun ptr(ptr: String) = apply { this.ptr = ptr }

    fun permissions(permissions: List<Permission>?) = apply { this.permissions = permissions }

    fun mutable(mutable: Boolean) = apply { this.mutable = mutable }

    fun id(id: String) = apply { this.id = id }

    fun build(): Offer {
      if (id.isEmpty()) id = UUID.randomUUID().toString()
      require(description.isNotEmpty()) { "Description cannot be empty" }
      require(rewards.isNotEmpty()) { "Rewards cannot be empty" }
      require(use.isNotEmpty()) { "Use cases cannot be empty" }
      require(tags.isNotEmpty()) { "Tags cannot be empty" }
      require(ptr.isNotEmpty()) { "Ptr cannot be empty" }

      return Offer(description, rewards, use, tags, ptr, permissions, mutable, id)
    }
  }

  companion object {
    fun fromJson(json: JSONObject, id: String): Offer {
      val rewards = json.getJSONArray("rewards")
      val use = json.getJSONArray("uses")
      val tags = json.getJSONArray("tags")
      val permissions = json.optJSONArray("permissions")
      return Offer(
          description = json.getString("description"),
          rewards = (0 until rewards.length()).map { Reward.fromJson(rewards.getJSONObject(it)) },
          uses = (0 until use.length()).map { Use.from(use.getJSONObject(it)) },
          tags = (0 until tags.length()).map { Tag.from(tags.getString(it)) },
          ptr = json.getString("ptr"),
          permissions =
              if (permissions != null)
                  (0 until permissions.length()).mapNotNull {
                    Permission.fromJson(permissions.getJSONObject(it))
                  }
              else null,
          mutable = json.optBoolean("mutable", true),
          id = id,
      )
    }
  }

  fun toJson(): JSONObject {
    return JSONObject()
        .put("description", description)
        .put("rewards", JSONArray(rewards.map { it.toJson() }))
        .put("uses", JSONArray(uses.map { it.toJson() }))
        .put("tags", JSONArray(tags.map { it.value }))
        .put("permissions", JSONArray(permissions?.map { it.toJson() }))
        .put("mutable", mutable)
        .put("ptr", ptr)
  }
}
