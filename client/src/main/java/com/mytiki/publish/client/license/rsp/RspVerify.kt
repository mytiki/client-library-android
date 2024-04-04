package com.mytiki.publish.client.license.rsp

import org.json.JSONObject

class RspVerify(
    val verified: Boolean,
    val reason: String
) {
    companion object {
        fun fromJson(data: JSONObject) = RspVerify(
            data.getBoolean("verified"),
            data.getString("reason")
        )
    }
}
