package com.mytiki.publish.client.offer

sealed class RewardType private constructor(val value: String) {
  data object VIRTUAL_CURENCY : RewardType("VIRTUAL_CURENCY")

  data object EXCLUSIVE_ACCESS : RewardType("EXCLUSIVE_ACCESS")

  data object UPGRADES : RewardType("UPGRADES")

  data class Custom(val type: String) : RewardType("custom:$type")


  companion object {
    fun from(type: String): RewardType {
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
