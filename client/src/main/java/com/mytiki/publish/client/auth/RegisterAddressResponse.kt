package com.mytiki.publish.client.auth

import org.json.JSONObject


class RegisterAddressResponse(val id: String, val address: String, val pubKey: String) {
    companion object{
        fun fromJson(json: JSONObject): RegisterAddressResponse {
            return RegisterAddressResponse(
                json.getString("id"),
                json.getString("address"),
                json.getString("pubKey")
            )
        }
    }
}
