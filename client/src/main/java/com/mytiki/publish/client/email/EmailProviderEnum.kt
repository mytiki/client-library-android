package com.mytiki.publish.client.email

import com.mytiki.publish.client.ProvidersInterface
import com.mytiki.publish.client.R

/**
 * Email provider enum
 */
enum class EmailProviderEnum(val authorizationEndpoint: String, val tokenEndpoint: String, val userInfoEndpoint: String): ProvidersInterface {
    GOOGLE("https://accounts.google.com/o/oauth2/v2/auth",  "https://www.googleapis.com/oauth2/v4/token", "https://openidconnect.googleapis.com/v1/userinfo"),
    OUTLOOK("https://login.microsoftonline.com/common/oauth2/v2.0/authorize" ,"https://login.microsoftonline.com/common/oauth2/v2.0/token" ,"https://graph.microsoft.com/oidc/userinfo");

    companion object {
        fun fromString(name: String) = entries.first{name == it.toString()}
    }
    override fun toString() = this.name

    override fun resId() = when (this) {
        GOOGLE -> R.drawable.gmail
        OUTLOOK -> R.drawable.outlook
    }

    fun refreshTokenEndpoint(refreshToken: String, clientID: String): String{
        return when(this.name) {
            EmailProviderEnum.GOOGLE.toString() -> "https://www.googleapis.com/oauth2/v4/token?grant_type=refresh_token&refresh_token=$refreshToken&client_id=$clientID"
            EmailProviderEnum.OUTLOOK.toString() -> ""
            else -> ""
        }
    }
}