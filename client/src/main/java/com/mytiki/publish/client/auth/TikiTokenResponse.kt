package com.mytiki.publish.client.auth

import org.json.JSONObject

class TikiTokenResponse private constructor(
    val access_token: String,
    val scope: String,
    val token_type: String,
    val expires_in: Int
) {
    companion object{
        fun fromJson(json: JSONObject): TikiTokenResponse {
            return TikiTokenResponse(
                json.getString("access_token"),
                json.getString("scope"),
                json.getString("token_type"),
                json.getInt("expires_in")
            )
        }
    }
}
