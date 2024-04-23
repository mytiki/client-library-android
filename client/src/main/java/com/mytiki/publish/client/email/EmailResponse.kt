package com.mytiki.publish.client.email

import org.json.JSONObject

class EmailResponse (
    val sub: String,
    val name: String,
    val given_name: String,
    val family_name: String,
    val picture: String,
    val email: String,
    val email_verified: Boolean,
    val locale: String,
){
    companion object{
        fun fromJson(json: JSONObject) = EmailResponse(
            json.getString("sub"),
            json.getString("name"),
            json.getString("given_name"),
            json.getString("family_name"),
            json.getString("picture"),
            json.getString("email"),
            json.getBoolean("email_verified"),
            json.getString("locale"),
        )
    }
}