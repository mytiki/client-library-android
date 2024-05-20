package com.mytiki.publish.client.license

import android.content.Context
import android.util.Base64
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.offer.OfferTag
import com.mytiki.publish.client.offer.OfferUse
import java.time.LocalDateTime
import org.json.JSONArray
import org.json.JSONObject

class LicenseRequest(
    val ptr: String,
    val offerTags: List<OfferTag>,
    val offerUses: List<OfferUse>,
    val description: String,
    val expiry: LocalDateTime?,
    val terms: String
) {
  private val userSignature: ByteArray?

  init {
    val keys = TikiClient.auth.getKey()
    val address = TikiClient.auth.address(keys)
    userSignature = address?.let { TikiClient.auth.signMessage(it, keys.private) }
  }

  fun toJSON(context: Context): JSONObject {
    val usesJson = JSONArray().apply { offerUses.forEach { put(it.toJson()) } }
    val tagsJson = JSONArray().apply { offerTags.forEach { put(it.value) } }

    val jsonBody =
        JSONObject()
            .put("ptr", ptr)
            .put("offerTags", tagsJson)
            .put("offerUses", usesJson)
            .put("description", description)
            .put("origin", context.packageName)
            .put("expiry", expiry?.toString())
            .put("terms", terms)

    val privateKey = TikiClient.auth.getKey().private ?: throw Exception("Private key not found")

    val signature =
        TikiClient.auth.signMessage(jsonBody.toString(), privateKey)
            ?: throw Exception("Error on Signing Message")

    jsonBody.put("signature", Base64.encodeToString(signature, Base64.DEFAULT or Base64.NO_WRAP))
    return jsonBody
  }
}
