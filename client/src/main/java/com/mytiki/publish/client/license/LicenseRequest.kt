package com.mytiki.publish.client.license

import com.mytiki.publish.client.TikiClient
import com.mytiki.tiki_sdk_android.trail.Use
import org.json.JSONArray
import org.json.JSONObject

class LicenseRequest(
    val ptr: String,
    val tags: Array<String>,
    val uses: Array<Use>,
    val terms: String,
    val expiry: String? = null,
    val titleDesc: String? = null,
    val licenseDesc: String
){
    val userSignature: ByteArray?

    init {
        val keys = TikiClient.auth.getKey()
        val address = keys?.let { TikiClient.auth.address(it) }
        userSignature = address?.let { TikiClient.auth.signMessage(it, keys.private) }
    }

    fun toJSON(): JSONObject{
        val jsonUse = JSONArray()
        uses.forEach { use ->
            val usecases = JSONArray()
            use.usecases.forEach { usecase ->
                usecases.put(usecase.value)
            }
            val destinations = JSONArray()
            use.destinations?.forEach { destination ->
                destinations.put(destination)
            }
            jsonUse.put(
                JSONObject()
                 .put("usecases", usecases)
                 .put("destinations", destinations)
            )
        }

        val json = JSONObject()
        json.put("ptr", ptr)
        json.put("tags", JSONArray(tags))
        json.put("uses", jsonUse)
        json.put("terms", terms)
        json.put("expiry", expiry)
        json.put("titleDesc", titleDesc)
        json.put("licenseDesc", licenseDesc)
        json.put("userSignature", userSignature)
        return json
    }
}
