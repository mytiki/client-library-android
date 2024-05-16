package com.mytiki.publish.client.offer

import com.mytiki.publish.client.permission.Permission
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

class Offer
private constructor(
    val description: String,
    val rewards: List<Reward>,
    val use: Use,
    val tags: List<Tag>,
    val ptr: String,
    val permissions: List<Permission>? = null,
    val mutable: Boolean = true,
    active: Boolean = false,
) {
  var active: Boolean = active
    private set

  private var isFirstChange = true

  var id = UUID.randomUUID().toString()
    private set

  private constructor(
      description: String,
      rewards: List<Reward>,
      use: Use,
      tags: List<Tag>,
      ptr: String,
      permissions: List<Permission>? = null,
      mutable: Boolean = true,
      active: Boolean = false,
      isFirstChange: Boolean = true,
      id: String = UUID.randomUUID().toString()
  ) : this(description, rewards, use, tags, ptr, permissions, mutable) {
    this.active = active
    this.isFirstChange = isFirstChange
    this.id = id
  }

  class Builder {
    private var description: String = ""
    private var rewards: List<Reward> = emptyList()
    private var use: Use = Use(emptyList())
    private var tags: List<Tag> = emptyList()
    private var ptr: String = ""
    private var permissions: List<Permission>? = null
    private var mutable: Boolean = true
    private var active: Boolean = false
    private var isFirstChange: Boolean = true
    private var id: String = UUID.randomUUID().toString()

    fun description(description: String) = apply { this.description = description }

    fun rewards(rewards: List<Reward>) = apply { this.rewards = rewards }

    fun use(use: Use) = apply { this.use = use }

    fun tags(tags: List<Tag>) = apply { this.tags = tags }

    fun ptr(ptr: String) = apply { this.ptr = ptr }

    fun permissions(permissions: List<Permission>?) = apply { this.permissions = permissions }

    fun mutable(mutable: Boolean) = apply { this.mutable = mutable }

    fun active(active: Boolean) = apply { this.active = active }

    fun build(): Offer {
      require(description.isNotEmpty()) { "Description cannot be empty" }
      require(rewards.isNotEmpty()) { "Rewards cannot be empty" }
      require(use.usecases.isNotEmpty()) { "Use cases cannot be empty" }
      require(tags.isNotEmpty()) { "Tags cannot be empty" }
      require(ptr.isNotEmpty()) { "Ptr cannot be empty" }

      return Offer(description, rewards, use, tags, ptr, permissions, mutable, active)
    }
  }

  companion object {
    fun fromJson(json: JSONObject, id: String): Offer {
      val rewards = json.getJSONArray("rewards")
      val use = json.getJSONObject("use")
      val tags = json.getJSONArray("tags")
      val permissions = json.optJSONArray("permissions")
      return Offer(
          description = json.getString("description"),
          rewards = (0 until rewards.length()).map { Reward.fromJson(rewards.getJSONObject(it)) },
          use = Use.from(use),
          tags = (0 until tags.length()).map { Tag.from(tags.getString(it)) },
          ptr = json.getString("ptr"),
          permissions =
              if (permissions != null)
                  (0 until permissions.length()).mapNotNull {
                    Permission.fromJson(permissions.getJSONObject(it))
                  }
              else null,
          mutable = json.optBoolean("mutable", true),
          active = json.optBoolean("active", false),
          isFirstChange = json.optBoolean("isFirstChange", true),
          id = id,
      )
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
        .put("isFirstChange", isFirstChange)
        .put("ptr", ptr)
  }

  internal fun activate(): Offer {
    if (mutable) {
      active = true
    } else {
      if (isFirstChange) {
        active = true
        isFirstChange = false
      }
    }
    return this
  }

  internal fun deactivate(): Offer {
    if (mutable) {
      active = false
    } else {
      if (isFirstChange) {
        active = false
        isFirstChange = false
      }
    }
    return this
  }
}
