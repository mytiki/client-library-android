package com.mytiki.publish.client.offer

import org.json.JSONArray
import org.json.JSONObject

data class OfferUse(var offerUsecases: List<OfferUsecase>, var destinations: List<String>? = null) {

  fun toJson(): JSONObject {
    val usecaseJson = JSONArray().apply { offerUsecases.forEach { put(it) } }
    val destinationJson = JSONArray().apply { destinations?.forEach { put(it) } }
    return JSONObject().put("offerUsecases", usecaseJson).put("destinations", destinationJson)
  }

  companion object {

    fun from(json: JSONObject): OfferUse {
      val usecases = json.getJSONArray("offerUsecases")
      val destinations = json.optJSONArray("destinations")
      return OfferUse(
          offerUsecases = (0 until usecases.length()).map { OfferUsecase.from(usecases.getString(it)) },
          destinations =
              if (destinations.length() != 0)
                  (0 until destinations.length()).map { destinations.getString(it) }
              else null)
    }
  }
}
