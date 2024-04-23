package com.mytiki.publish.client.auth

enum class TokenProviderEnum {
  GMAIL,
  OUTLOOK,
  TIKI;

  companion object {
    fun fromString(name: String) = entries.firstOrNull() { name == it.toString() }
  }

  override fun toString() = this.name
}
