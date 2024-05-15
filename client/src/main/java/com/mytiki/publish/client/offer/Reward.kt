package com.mytiki.publish.client.offer

import org.json.JSONObject

class Reward(val description: String, val type: RewardType, val amount: String) {
  companion object {
    fun fromJson(json: JSONObject): Reward {
      return Reward(
          description = json.getString("description"),
          type = RewardType.from(json.getString("type")),
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
