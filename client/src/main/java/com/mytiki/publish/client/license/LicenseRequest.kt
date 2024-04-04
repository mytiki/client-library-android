package com.mytiki.publish.client.license

import android.content.Context
import com.mytiki.publish.client.TikiClient
import org.json.JSONArray
import org.json.JSONObject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class LicenseRequest(
    val ptr: String,
){
    val userSignature: ByteArray?

    init {
        val keys = TikiClient.auth.getKey()
        val address = keys?.let { TikiClient.auth.address(it) }
        userSignature = address?.let { TikiClient.auth.signMessage(it, keys.private) }
    }

    fun toJSON(context: Context): JSONObject{
        val jsonBody = JSONObject()
            .put("ptr", TikiClient.userID)
            .put("tags", JSONArray().put("purchase_history"))
            .put("uses", JSONArray().put(
                JSONObject().apply {
                    put("usecases", JSONArray().put("attribution"))
                    put("destinations", JSONArray().put("*"))
                }
            ))
            .put("description", "")
            .put("origin", context.packageName)
            .put("expiry", JSONObject.NULL)
            .put("terms", TikiClient.license.terms(context))
        return jsonBody
    }
}
