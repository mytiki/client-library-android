package com.mytiki.publish.client.auth

import org.json.JSONObject


class AuthAddrRsp(val id: String, val address: String, val pubKey: String) {
    companion object{
        fun fromJson(json: JSONObject): AuthAddrRsp {
            return AuthAddrRsp(
                json.getString("id"),
                json.getString("address"),
                json.getString("pubKey")
            )
        }
    }
}
