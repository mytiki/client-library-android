package com.mytiki.publish.client.offer

sealed class OfferRewardType private constructor(val value: String) {
  data object VIRTUAL_CURENCY : OfferRewardType("VIRTUAL_CURENCY")

  data object EXCLUSIVE_ACCESS : OfferRewardType("EXCLUSIVE_ACCESS")

  data object UPGRADES : OfferRewardType("UPGRADES")

  data class Custom(val type: String) : OfferRewardType("custom:$type")


  companion object {
    fun from(type: String): OfferRewardType {
      return when {
        type == "VIRTUAL_CURENCY" -> VIRTUAL_CURENCY
        type == "EXCLUSIVE_ACCESS" -> EXCLUSIVE_ACCESS
        type == "UPGRADES" -> UPGRADES
        "custom:" in type -> Custom(type.removePrefix("custom:"))
        else -> Custom(type)
      }
    }
  }
}
