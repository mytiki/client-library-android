package com.mytiki.publish.client.email

import java.time.LocalDateTime
import java.util.*
import org.json.JSONObject

class IndexData(
    val email: String,
    val lastDate: LocalDateTime?,
    val historicDate: LocalDateTime?,
    val downloadInProgress: Boolean
) {
  companion object {
    fun fromJson(data: String?, key: String): IndexData {
      val json = JSONObject(data)
      return IndexData(
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
