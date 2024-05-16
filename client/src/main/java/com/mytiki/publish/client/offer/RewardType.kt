package com.mytiki.publish.client.offer

sealed class RewardType private constructor(val value: String) {
  data object VIRTUAL_CURENCY : RewardType("VIRTUAL_CURENCY")

  data object EXCLUSIVE_ACCESS : RewardType("EXCLUSIVE_ACCESS")

  data object UPGRADES : RewardType("UPGRADES")

  data class Custom(val type: String) : RewardType("custom:$type")
}
