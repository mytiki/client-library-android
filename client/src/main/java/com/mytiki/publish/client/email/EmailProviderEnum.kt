package com.mytiki.publish.client.email

/**
 * Email provider enum
 */
enum class EmailProviderEnum(val authorizationEndpoint: String, val tokenEndpoint: String, val userInfoEndpoint: String) {
    GOOGLE("https://accounts.google.com/o/oauth2/v2/auth",  "https://www.googleapis.com/oauth2/v4/token", "https://openidconnect.googleapis.com/v1/userinfo"),
    OUTLOOK("https://login.microsoftonline.com/common/oauth2/v2.0/authorize" ,"https://login.microsoftonline.com/common/oauth2/v2.0/token" ,"https://graph.microsoft.com/oidc/userinfo");

    fun refreshTokenEndpoint(refreshToken: String, clientID: String): String{
        return when(this.name) {
            EmailProviderEnum.GOOGLE.toString() -> "https://www.googleapis.com/oauth2/v4/token?grant_type=refresh_token&refresh_token=$refreshToken&client_id=$clientID"
            EmailProviderEnum.OUTLOOK.toString() -> ""
            else -> ""
        }
    }

    override fun toString() = this.name
    companion object {
        fun fromString(name: String) = entries.first{name == it.toString()}
    }
}