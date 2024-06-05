package com.mytiki.publish.client.email.message

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import org.json.JSONArray
import org.json.JSONObject

class Message(
    val id: String,
    val threadId: String,
    val labelIds: List<String?>?,
    val snippet: String?,
    val historyId: String?,
    val internalDate: LocalDateTime?,
    val payload: MessagePart?,
    val sizeEstimate: Int?,
    val raw: String?
) {
  companion object {
    fun fromJson(json: JSONObject): Message {

      val array =
          try {
            json.getJSONArray("labelIds")
          } catch (error: Exception) {
            null
          }
      val labelIds = array?.let { arrayOfNulls<String>(it.length()) }
      if (array != null) {
        for (i in 0 until array.length()) labelIds?.set(i, array.getString(i))
      }

      return Message(
          json.getString("id"),
          json.getString("threadId"),
          labelIds?.toList(),
          try {
            json.getString("snippet")
          } catch (error: Exception) {
            null
          },
          try {
            json.getString("historyId")
          } catch (error: Exception) {
            null
          },
          try {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(json.getString("internalDate").toLong()),
                ZoneId.systemDefault())
          } catch (error: Exception) {
            null
          },
          try {
            MessagePart.fromJson(json.getJSONObject("payload"))
          } catch (error: Exception) {
            null
          },
          try {
            json.getInt("sizeEstimate")
          } catch (error: Exception) {
            null
          },
          try {
            json.getString("raw")
          } catch (error: Exception) {
            null
          },
      )
    }
  }

  fun toJson(): JSONObject {
    val labelIdsArray = JSONArray()
    if (!labelIds.isNullOrEmpty()) {
      labelIds.forEach {
        if (it != null) {
          labelIdsArray.put(it)
        }
      }
    }

    return JSONObject()
        .put("id", id)
        .put("threadId", threadId)
        .put("labelIds", labelIdsArray)
        .put("snippet", snippet)
        .put("historyId", historyId)
        .put(
            "internalDate",
            internalDate?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli())
        .put("payload", payload?.toJson())
        .put("sizeEstimate", sizeEstimate)
        .put("raw", raw)
  }
}
