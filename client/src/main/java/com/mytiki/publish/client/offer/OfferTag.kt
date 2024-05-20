package com.mytiki.publish.client.offer

sealed class OfferTag private constructor(val value: String) {
  data object EMAIL_ADDRESS : OfferTag("email_address")

  data object PHONE_NUMBER : OfferTag("phone_number")

  data object PHYSICAL_ADDRESS : OfferTag("physical_address")

  data object CONTACT_INFO : OfferTag("contact_info")

  data object HEALTH : OfferTag("health")

  data object FITNESS : OfferTag("fitness")

  data object PAYMENT_INFO : OfferTag("payment_info")

  data object CREDIT_INFO : OfferTag("credit_info")

  data object FINANCIAL_INFO : OfferTag("financial_info")

  data object PRECISE_LOCATION : OfferTag("precise_location")

  data object COARSE_LOCATION : OfferTag("coarse_location")

  data object SENSITIVE_INFO : OfferTag("sensitive_info")

  data object CONTACTS : OfferTag("contacts")

  data object MESSAGES : OfferTag("messages")

  data object PHOTO_VIDEO : OfferTag("photo_video")

  data object AUDIO : OfferTag("audio")

  data object GAMEPLAY_CONTENT : OfferTag("gameplay_content")

  data object CUSTOMER_SUPPORT : OfferTag("customer_support")

  data object USER_CONTENT : OfferTag("user_content")

  data object BROWSING_HISTORY : OfferTag("browsing_history")

  data object SEARCH_HISTORY : OfferTag("search_history")

  data object USER_ID : OfferTag("user_id")

  data object DEVICE_ID : OfferTag("device_id")

  data object PURCHASE_HISTORY : OfferTag("purchase_history")

  data object PRODUCT_INTERACTION : OfferTag("product_interaction")

  data object ADVERTISING_DATA : OfferTag("advertising_data")

  data object USAGE_DATA : OfferTag("usage_data")

  data object CRASH_DATA : OfferTag("crash_data")

  data object PERFORMANCE_DATA : OfferTag("performance_data")

  data object DIAGNOSTIC_DATA : OfferTag("diagnostic_data")

  data class Custom(val tag: String) : OfferTag("custom:$tag")

  companion object {

    fun from(tag: String): OfferTag {
      return when (tag) {
        "email_address" -> EMAIL_ADDRESS
        "phone_number" -> PHONE_NUMBER
        "physical_address" -> PHYSICAL_ADDRESS
        "contact_info" -> CONTACT_INFO
        "health" -> HEALTH
        "fitness" -> FITNESS
        "payment_info" -> PAYMENT_INFO
        "credit_info" -> CREDIT_INFO
        "financial_info" -> FINANCIAL_INFO
        "precise_location" -> PRECISE_LOCATION
        "coarse_location" -> COARSE_LOCATION
        "sensitive_info" -> SENSITIVE_INFO
        "contacts" -> CONTACTS
        "messages" -> MESSAGES
        "photo_video" -> PHOTO_VIDEO
        "audio" -> AUDIO
        "gameplay_content" -> GAMEPLAY_CONTENT
        "customer_support" -> CUSTOMER_SUPPORT
        "user_content" -> USER_CONTENT
        "browsing_history" -> BROWSING_HISTORY
        "search_history" -> SEARCH_HISTORY
        "user_id" -> USER_ID
        "device_id" -> DEVICE_ID
        "purchase_history" -> PURCHASE_HISTORY
        "product_interaction" -> PRODUCT_INTERACTION
        "advertising_data" -> ADVERTISING_DATA
        "usage_data" -> USAGE_DATA
        "crash_data" -> CRASH_DATA
        "performance_data" -> PERFORMANCE_DATA
        "diagnostic_data" -> DIAGNOSTIC_DATA
        else -> Custom(tag)
      }
    }
  }
}
