package com.mytiki.publish.client.email.messageResponse

import org.json.JSONObject

class MessageHeader(
    val name: String?,
    val value: String?
) {
    companion object {
        fun fromJson(json: JSONObject) = MessageHeader(
            try {json.getString("name")}catch (error: Exception){null},
            try {json.getString("value")}catch (error: Exception){null},
        )
    }

    fun toJson(): JSONObject{
        return JSONObject()
            .put("name", name)
            .put("value", value)
    }
}