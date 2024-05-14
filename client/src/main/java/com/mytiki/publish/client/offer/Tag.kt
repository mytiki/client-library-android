package com.mytiki.publish.client.offer

sealed class Tag private constructor(val value: String) {
  data object EMAIL_ADDRESS : Tag("email_address")

  data object PHONE_NUMBER : Tag("phone_number")

  data object PHYSICAL_ADDRESS : Tag("physical_address")

  data object CONTACT_INFO : Tag("contact_info")

  data object HEALTH : Tag("health")

  data object FITNESS : Tag("fitness")

  data object PAYMENT_INFO : Tag("payment_info")

  data object CREDIT_INFO : Tag("credit_info")

  data object FINANCIAL_INFO : Tag("financial_info")

  data object PRECISE_LOCATION : Tag("precise_location")

  data object COARSE_LOCATION : Tag("coarse_location")

  data object SENSITIVE_INFO : Tag("sensitive_info")

  data object CONTACTS : Tag("contacts")

  data object MESSAGES : Tag("messages")

  data object PHOTO_VIDEO : Tag("photo_video")

  data object AUDIO : Tag("audio")

  data object GAMEPLAY_CONTENT : Tag("gameplay_content")

  data object CUSTOMER_SUPPORT : Tag("customer_support")

  data object USER_CONTENT : Tag("user_content")

  data object BROWSING_HISTORY : Tag("browsing_history")

  data object SEARCH_HISTORY : Tag("search_history")

  data object USER_ID : Tag("user_id")

  data object DEVICE_ID : Tag("device_id")

  data object PURCHASE_HISTORY : Tag("purchase_history")

  data object PRODUCT_INTERACTION : Tag("product_interaction")

  data object ADVERTISING_DATA : Tag("advertising_data")

  data object USAGE_DATA : Tag("usage_data")

  data object CRASH_DATA : Tag("crash_data")

  data object PERFORMANCE_DATA : Tag("performance_data")

  data object DIAGNOSTIC_DATA : Tag("diagnostic_data")

  data class Custom(val tag: String) : Tag("custom:$tag")

  companion object {

    fun from(tag: String): Tag {
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
