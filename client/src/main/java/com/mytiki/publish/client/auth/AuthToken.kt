package com.mytiki.publish.client.auth

import java.time.LocalDateTime
import java.util.*
import org.json.JSONObject

class AuthToken(
    val username: String,
    val auth: String,
    val refresh: String,
    val expiration: LocalDateTime,
    val provider: TokenProviderEnum
) {
  companion object {
    fun fromString(data: String, key: String): AuthToken {
      val json = JSONObject(data)
      return AuthToken(
          key,
          json.getString("auth"),
          json.getString("refresh"),
          LocalDateTime.parse(json.getString("expiration")),
          TokenProviderEnum.fromString(json.getString("provider"))
              ?: throw Exception("Invalid provider in AuthToken"))
    }
  }

  override fun toString(): String {
    return JSONObject()
        .put("auth", auth)
        .put("refresh", refresh)
        .put("expiration", expiration.toString())
        .put("provider", provider.toString())
        .toString()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as AuthToken

    if (username != other.username) return false
    if (auth != other.auth) return false
    if (refresh != other.refresh) return false
    if (expiration != other.expiration) return false
    return provider == other.provider
  }

  override fun hashCode(): Int {
    var result = username.hashCode()
    result = 31 * result + auth.hashCode()
    result = 31 * result + refresh.hashCode()
    result = 31 * result + expiration.hashCode()
    result = 31 * result + provider.hashCode()
    return result
  }
}
