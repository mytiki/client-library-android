package com.mytiki.publish.client.email

import org.json.JSONObject
import java.util.Date


class IndexData(val email: String, val date: Date, val nextPageToken: String?) {
    companion object {
        fun fromJson(data: String?, key: String): IndexData {
            val json = JSONObject(data)
            return IndexData(
                key,
                Date(json.getLong("date")),
                json.getString("nextPageToken")
            )
        }
    }

    fun toJson(): String {
        return JSONObject()
            .put("nextPageToken", nextPageToken)
            .put("date", date.time)
            .toString()
    }
}