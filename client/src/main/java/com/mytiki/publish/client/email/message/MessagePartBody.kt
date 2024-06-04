package com.mytiki.publish.client.email.message

import org.json.JSONObject

class MessagePartBody(val attachmentId: String?, val size: Int?, val data: String?) {
  companion object {
    fun fromJson(json: JSONObject): MessagePartBody {
      return MessagePartBody(
          try {
            json.getString("attachmentId")
          } catch (error: Exception) {
            null
          },
          try {
            json.getInt("size")
          } catch (error: Exception) {
            null
          },
          try {
            json.getString("data")
          } catch (error: Exception) {
            null
          },
      )
    }
  }

  fun toJson(): JSONObject {
    return JSONObject().put("attachmentId", attachmentId).put("size", size).put("data", data)
  }
}
