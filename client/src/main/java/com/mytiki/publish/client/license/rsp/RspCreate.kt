package com.mytiki.publish.client.license.rsp

import org.json.JSONObject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class RspCreate(
    val id: String,
    val timestamp: String,
    val signature: ByteArray
) {
    companion object {
        @OptIn(ExperimentalEncodingApi::class)
        fun fromJson(json: JSONObject): RspCreate {
            return RspCreate(
                json.getString("id"),
                json.getString("timestamp"),
                Base64.Default.decode(json.getString("signature"))
            )
        }
    }
}

