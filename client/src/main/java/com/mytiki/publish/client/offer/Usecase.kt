package com.mytiki.publish.client.offer

sealed class Usecase private constructor(val value: String) {
  data object ATTRIBUTION : Usecase("attribution")

  data object RETARGETING : Usecase("retargeting")

  data object PERSONALIZATION : Usecase("personalization")

  data object AI_TRAINING : Usecase("ai_training")

  data object DISTRIBUTION : Usecase("distribution")

  data object ANALYTICS : Usecase("analytics")

  data object SUPPORT : Usecase("support")

  data class Custom(val usecase: String) : Usecase("custom:$usecase")

  companion object {
    fun from(usecase: String): Usecase {
      return when {
        usecase == "attribution" -> ATTRIBUTION
        usecase == "retargeting" -> RETARGETING
        usecase == "personalization" -> PERSONALIZATION
        usecase == "ai_training" -> AI_TRAINING
        usecase == "distribution" -> DISTRIBUTION
        usecase == "analytics" -> ANALYTICS
        usecase == "support" -> SUPPORT
        else -> Custom(usecase)
      }
    }
  }
}
