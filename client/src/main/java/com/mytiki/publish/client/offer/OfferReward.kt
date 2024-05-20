package com.mytiki.publish.client.offer

import org.json.JSONObject

class OfferReward(val description: String, val type: OfferRewardType, val amount: String) {
  companion object {
    fun fromJson(json: JSONObject): OfferReward {
      return OfferReward(
          description = json.getString("description"),
          type = OfferRewardType.from(json.getString("type")),
          amount = json.getString("amount"))
    }
  }

  fun toJson(): JSONObject {
    return JSONObject()
        .put("description", description)
        .put("type", type.value)
        .put("amount", amount)
  }
}
