package com.mytiki.publish.client.email.messageResponse

import org.json.JSONArray
import org.json.JSONObject

class MessagePart(
    val partId: String?,
    val mimeType: String?,
    val filename: String?,
    val headers: Array<MessageHeader?>?,
    var body: MessagePartBody?,
    val parts: Array<MessagePart?>?
){
    companion object{
        fun fromJson(json: JSONObject): MessagePart{
            val arrayPart = try{json.getJSONArray("parts")}catch(error: Exception){null}
            val parts = arrayPart?.let { arrayOfNulls<MessagePart>(it.length()) }
            if (arrayPart != null) {
                for (i in 0 until arrayPart.length()) parts?.set(i,
                    fromJson(arrayPart.getJSONObject(i))
                )
            }

            val arrayHeaders = try{json.getJSONArray("headers")}catch(error: Exception){null}
            val headers = arrayHeaders?.let { arrayOfNulls<MessageHeader>(it.length()) }
            if (arrayHeaders != null) {
                for (i in 0 until arrayHeaders.length()) headers?.set(i,
                    MessageHeader.fromJson(arrayHeaders.getJSONObject(i))
                )
            }

            return MessagePart(
                try {json.getString("partId")}catch(error: Exception){null},
                try {json.getString("mimeType")}catch(error: Exception){null},
                try {json.getString("filename")}catch(error: Exception){null},
                headers,
                MessagePartBody.fromJson(json.getJSONObject("body")),
                parts,
            )
        }
    }

    fun toJson(): JSONObject{
        val arrayPart = JSONArray()
        if (!parts.isNullOrEmpty()) {
            parts.forEach {
                if (it != null) {
                    arrayPart.put(it.toJson())
                }
            }
        }

        val arrayHeaders = JSONArray()
        if (!headers.isNullOrEmpty()) {
            headers.forEach {
                if (it != null) {
                    arrayHeaders.put(it.toJson())
                }
            }
        }

        return JSONObject()
            .put("partId", partId)
            .put("mimeType", mimeType)
            .put("filename", filename)
            .put("headers", arrayHeaders)
            .put("body", body?.toJson())
            .put("parts", arrayPart)
    }
}