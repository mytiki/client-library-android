package com.mytiki.publish.client.email.message

import org.json.JSONArray
import org.json.JSONObject

class MessageResponse(
    val messages: Array<Message?>?,
    val nextPageToken: String?,
    val resultSizeEstimate: Int?,
) {
  companion object {

    fun fromJson(json: JSONObject): MessageResponse {
      val array =
          try {
            json.getJSONArray("messages")
          } catch (error: Exception) {
            null
          }
      val jsonMessages = array?.let { arrayOfNulls<Message>(it.length()) }
      if (array != null) {
        for (i in 0 until array.length()) jsonMessages?.set(
            i, Message.fromJson(array.getJSONObject(i)))
      }
      return MessageResponse(
          jsonMessages,
          try {
            json.getString("nextPageToken")
          } catch (error: Exception) {
            null
          },
          try {
            json.getInt("resultSizeEstimate")
          } catch (error: Exception) {
            null
          })
    }
  }

  fun toJson(): JSONObject {
    val array = JSONArray()
    if (!messages.isNullOrEmpty()) {
      messages.forEach {
        if (it != null) {
          array.put(it.toJson())
        }
      }
    }
    return JSONObject()
        .put("messages", array)
        .put("nextPageToken", nextPageToken)
        .put("resultSizeEstimate", resultSizeEstimate)
  }
}
