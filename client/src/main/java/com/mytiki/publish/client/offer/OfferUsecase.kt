package com.mytiki.publish.client.offer

sealed class OfferUsecase private constructor(val value: String) {
  data object ATTRIBUTION : OfferUsecase("attribution")

  data object RETARGETING : OfferUsecase("retargeting")

  data object PERSONALIZATION : OfferUsecase("personalization")

  data object AI_TRAINING : OfferUsecase("ai_training")

  data object DISTRIBUTION : OfferUsecase("distribution")

  data object ANALYTICS : OfferUsecase("analytics")

  data object SUPPORT : OfferUsecase("support")

  data class Custom(val usecase: String) : OfferUsecase("custom:$usecase")

  companion object {
    fun from(usecase: String): OfferUsecase {
      return when {
        usecase == "attribution" -> ATTRIBUTION
        usecase == "retargeting" -> RETARGETING
        usecase == "personalization" -> PERSONALIZATION
        usecase == "ai_training" -> AI_TRAINING
        usecase == "distribution" -> DISTRIBUTION
        usecase == "analytics" -> ANALYTICS
        usecase == "support" -> SUPPORT
        "custom:" in usecase -> Custom(usecase.removePrefix("custom:"))
        else -> Custom(usecase)
      }
    }
  }
}
