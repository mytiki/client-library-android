package com.mytiki.publish.client.offer

import org.json.JSONArray
import org.json.JSONObject

data class Use(var usecases: List<Usecase>, var destinations: List<String>? = null) {

  fun toJson(): JSONObject {
    val usecaseJson = JSONArray().apply { usecases.forEach { put(it) } }
    val destinationJson = JSONArray().apply { destinations?.forEach { put(it) } }
    return JSONObject().put("usecases", usecaseJson).put("destinations", destinationJson)
  }

  companion object {

    fun from(json: JSONObject): Use {
      val usecases = json.getJSONArray("usecases")
      val destinations = json.optJSONArray("destinations")
      return Use(
          usecases = (0 until usecases.length()).map { Usecase.from(usecases.getString(it)) },
          destinations =
              if (destinations.length() != 0)
                  (0 until destinations.length()).map { destinations.getString(it) }
              else null)
    }
  }
}
