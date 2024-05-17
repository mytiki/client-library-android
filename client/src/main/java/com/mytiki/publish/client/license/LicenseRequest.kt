package com.mytiki.publish.client.license

import android.content.Context
import android.util.Base64
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.offer.Tag
import com.mytiki.publish.client.offer.Use
import org.json.JSONArray
import org.json.JSONObject

class LicenseRequest {
  private val userSignature: ByteArray?

  init {
    val keys = TikiClient.auth.getKey()
    val address = TikiClient.auth.address(keys)
    userSignature = address?.let { TikiClient.auth.signMessage(it, keys.private) }
  }

  fun toJSON(context: Context, use: Use?, tag: List<Tag>?): JSONObject {
    val usecaseJson = JSONArray().apply { use?.usecases?.forEach { put(it) } }
    val destinationJson = JSONArray().apply { use?.destinations?.forEach { put(it) } }
    val tagsJson = JSONArray().apply { tag?.forEach { put(it.value) } }

    val jsonBody =
        JSONObject()
            .put("ptr", TikiClient.userID)
            .put("tags", tagsJson)
            .put(
                "uses",
                JSONArray()
                    .put(
                        JSONObject().apply {
                          put("usecases", usecaseJson)
                          put("destinations", destinationJson)
                        }))
            .put("description", "")
            .put("origin", context.packageName)
            .put("expiry", JSONObject.NULL)
            .put("terms", TikiClient.license.terms(context))

    val privateKey = TikiClient.auth.getKey().private ?: throw Exception("Private key not found")

    val signature =
        TikiClient.auth.signMessage(jsonBody.toString(), privateKey)
            ?: throw Exception("Error on Signing Message")

    jsonBody.put("signature", Base64.encodeToString(signature, Base64.DEFAULT or Base64.NO_WRAP))
    return jsonBody
  }
}
