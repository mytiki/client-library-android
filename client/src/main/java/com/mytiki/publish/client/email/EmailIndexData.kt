package com.mytiki.publish.client.email

import java.time.LocalDateTime
import org.json.JSONObject

class EmailIndexData(
    val email: String,
    val lastDate: LocalDateTime?,
    val historicDate: LocalDateTime?,
    val downloadInProgress: Boolean
) {
  companion object {
    fun fromJson(data: String?, key: String): EmailIndexData {
      val json = JSONObject(data)
      return EmailIndexData(
          key,
          try {
            LocalDateTime.parse(json.getString("lastDate"))
          } catch (error: Exception) {
            null
          },
          try {
            LocalDateTime.parse(json.getString("historicDate"))
          } catch (error: Exception) {
            null
          },
          json.getBoolean("downloadInProgress"))
    }
  }

  fun toJson(): String {
    return JSONObject()
        .put("lastDate", lastDate?.toString())
        .put("historicDate", historicDate?.toString())
        .put("downloadInProgress", downloadInProgress)
        .toString()
  }
}
