package com.mytiki.publish.client.license

import android.content.Context
import android.util.Base64
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.offer.Tag
import com.mytiki.publish.client.offer.Use
import java.time.LocalDateTime
import org.json.JSONArray
import org.json.JSONObject

class LicenseRequest(
    val ptr: String,
    val tags: List<Tag>,
    val uses: List<Use>,
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
    val usesJson = JSONArray().apply { uses.forEach { put(it.toJson()) } }
    val tagsJson = JSONArray().apply { tags.forEach { put(it.value) } }

    val jsonBody =
        JSONObject()
            .put("ptr", ptr)
            .put("tags", tagsJson)
            .put("uses", usesJson)
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
