package com.mytiki.publish.client.email

/** Email provider enum */
enum class EmailProviderEnum(
    val authorizationEndpoint: String,
    val tokenEndpoint: String,
    val userInfoEndpoint: String,
    val scopes: String,
    val messagesIndexListEndpoint: String,
) {
  GOOGLE(
      "https://accounts.google.com/o/oauth2/v2/auth",
      "https://www.googleapis.com/oauth2/v4/token",
      "https://openidconnect.googleapis.com/v1/userinfo",
      "openid email profile https://www.googleapis.com/auth/gmail.readonly",
      "https://gmail.googleapis.com/gmail/v1/users/me/messages?maxResults=500");

  companion object {
    fun fromString(name: String) = entries.firstOrNull() { name == it.toString() }
  }

  override fun toString() = this.name

  fun refreshTokenEndpoint(refreshToken: String, clientID: String): String {
    return when (this.name) {
      toString() ->
          "https://www.googleapis.com/oauth2/v4/token?grant_type=refresh_token&refresh_token=$refreshToken&client_id=$clientID"
      else -> ""
    }
  }

  fun messageEndpoint(messageID: String): String {
    return when (this.name) {
      toString() ->
          "https://gmail.googleapis.com/gmail/v1/users/me/messages/${messageID}?format=full"
      else -> ""
    }
  }

  fun attachmentEndpoint(messageID: String, attachmentID: String): String {
    return when (this.name) {
      toString() ->
          "https://gmail.googleapis.com/gmail/v1/users/me/messages/$messageID/attachments/$attachmentID"
      else -> ""
    }
  }
}
