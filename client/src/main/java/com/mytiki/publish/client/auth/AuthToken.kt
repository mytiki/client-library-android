package com.mytiki.publish.client.auth

import android.util.Log
import com.mytiki.publish.client.email.EmailProviderEnum
import org.json.JSONObject
import java.util.Date

class AuthToken (val auth: String, val refresh: String, val expiration: Date, val provider: EmailProviderEnum){
    companion object{
        fun fromString(data: String): AuthToken{
            val json = JSONObject(data)
            Log.d("**************", json.toString())
            return AuthToken(
                json.getString("auth"),
                json.getString("refresh"),
                Date(json.getLong("expiration")),
                EmailProviderEnum.fromString(json.getString("provider"))
            )
        }
    }
    override fun toString(): String {
        return JSONObject()
            .put("auth", auth)
            .put("refresh", refresh)
            .put("expiration", expiration.time)
            .put("provider" , provider.toString())
            .toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuthToken

        if (auth != other.auth) return false
        if (refresh != other.refresh) return false
        return expiration == other.expiration
    }
}