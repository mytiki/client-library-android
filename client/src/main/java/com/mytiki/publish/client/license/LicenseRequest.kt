package com.mytiki.publish.client.license

import android.content.Context
import android.util.Base64
import com.mytiki.publish.client.TikiClient
import org.json.JSONArray
import org.json.JSONObject

class LicenseRequest{
    private val userSignature: ByteArray?

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

        val privateKey = TikiClient.auth.getKey()?.private ?:
        throw Exception("Private key not found")

        val signature =  TikiClient.auth.signMessage(jsonBody.toString(), privateKey) ?:
        throw Exception("Error on Signing Message")

        jsonBody.put("signature", Base64.encodeToString(signature, Base64.DEFAULT or Base64.NO_WRAP))
        return jsonBody
    }
}
