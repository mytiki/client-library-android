package com.mytiki.publish.client.offer

import com.mytiki.publish.client.permission.Permission
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

class Offer
private constructor(
    val description: String,
    val offerRewards: List<OfferReward>,
    val offerUses: List<OfferUse>,
    val offerTags: List<OfferTag>,
    val ptr: String,
    val permissions: List<Permission>?,
    val mutable: Boolean,
    var id: String,
) {

  class Builder {
    private var description: String = ""
    private var offerRewards: List<OfferReward> = emptyList()
    private var offerUse: List<OfferUse> = emptyList()
    private var offerTags: List<OfferTag> = emptyList()
    private var ptr: String = ""
    private var permissions: List<Permission>? = null
    private var mutable: Boolean = true
    private var id: String = UUID.randomUUID().toString()

    fun description(description: String) = apply { this.description = description }

    fun rewards(offerRewards: List<OfferReward>) = apply { this.offerRewards = offerRewards }

    fun use(offerUse: List<OfferUse>) = apply { this.offerUse = offerUse }

    fun tags(offerTags: List<OfferTag>) = apply { this.offerTags = offerTags }

    fun ptr(ptr: String) = apply { this.ptr = ptr }

    fun permissions(permissions: List<Permission>?) = apply { this.permissions = permissions }

    fun mutable(mutable: Boolean) = apply { this.mutable = mutable }

    fun id(id: String) = apply { this.id = id }

    fun build(): Offer {
      if (id.isEmpty()) id = UUID.randomUUID().toString()
      require(description.isNotEmpty()) { "Description cannot be empty" }
      require(offerRewards.isNotEmpty()) { "Rewards cannot be empty" }
      require(offerUse.isNotEmpty()) { "OfferUse cases cannot be empty" }
      require(offerTags.isNotEmpty()) { "Tags cannot be empty" }
      require(ptr.isNotEmpty()) { "Ptr cannot be empty" }

      return Offer(description, offerRewards, offerUse, offerTags, ptr, permissions, mutable, id)
    }
  }

  companion object {
    fun fromJson(json: JSONObject, id: String): Offer {
      val rewards = json.getJSONArray("offerRewards")
      val use = json.getJSONArray("offerUses")
      val tags = json.getJSONArray("offerTags")
      val permissions = json.optJSONArray("permissions")
      return Offer(
          description = json.getString("description"),
          offerRewards = (0 until rewards.length()).map { OfferReward.fromJson(rewards.getJSONObject(it)) },
          offerUses = (0 until use.length()).map { OfferUse.from(use.getJSONObject(it)) },
          offerTags = (0 until tags.length()).map { OfferTag.from(tags.getString(it)) },
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
        .put("offerRewards", JSONArray(offerRewards.map { it.toJson() }))
        .put("offerUses", JSONArray(offerUses.map { it.toJson() }))
        .put("offerTags", JSONArray(offerTags.map { it.value }))
        .put("permissions", JSONArray(permissions?.map { it.toJson() }))
        .put("mutable", mutable)
        .put("ptr", ptr)
  }
}
