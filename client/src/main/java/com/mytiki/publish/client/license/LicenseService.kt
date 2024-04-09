package com.mytiki.publish.client.license

import android.content.Context
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.license.rsp.RspCreate
import com.mytiki.publish.client.license.rsp.RspVerify
import com.mytiki.publish.client.utils.apiService.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Service for managing licenses.
 */
class LicenseService {
    private val baseUrl = "https://trail.mytiki.com"

    /**
     * Creates a new license for the user.
     * @param context The context.
     * @return Boolean indicating the success of the license creation.
     * @throws Exception if there is an error creating the license.
     */
    suspend fun create(context: Context): Boolean {
        val licenseRequest = LicenseRequest()
        val jsonBody = licenseRequest.toJSON(context)
        val body = jsonBody.toString().toRequestBody("application/json".toMediaType())
        val addressToken = TikiClient.auth.addressToken().await()
        val response = ApiService.post(
            header = mapOf(
                "Authorization" to "Bearer $addressToken",
                "Content-Type" to "application/json",
            ),
            endPoint = "$baseUrl/license/create",
            onError = Exception("error on creating license"),
            body = body
        ).await() ?: throw Exception("error on creating license")
        val rspCreate = RspCreate.fromJson(JSONObject(response.string()))
        return rspCreate.id.isNotEmpty()
    }

    /**
     * Verifies the validity of the user's license.
     * @return Boolean indicating whether the license is valid.
     * @throws Exception if there is an error verifying the license.
     */
    suspend fun verify(): Boolean {
        val response = ApiService.post(
            header = mapOf(
                "Authorization" to "Bearer ${TikiClient.auth.addressToken().await()}",
                "Content-Type" to "application/json",
            ),
            endPoint = "$baseUrl/license/verify",
            onError = Exception("error on creating license")
        ).await()
        if (response == null) throw Exception("error on verify license")
        return RspVerify.fromJson(JSONObject(response.string())).verified
    }

    /**
     * Retrieves the terms of service.
     * @param context The context.
     * @return The terms of service as a String.
     */
    fun terms(context: Context): String {
        val terms = context.assets.open("terms.md").bufferedReader().use { it.readText() }
        val replacements = mapOf(
            "{{{COMPANY}}}" to TikiClient.config.companyName,
            "{{{JURISDICTION}}}" to TikiClient.config.companyJurisdiction,
            "{{{TOS}}}" to TikiClient.config.tosUrl,
            "{{{POLICY}}}" to TikiClient.config.privacyUrl
        )
        return replacements.entries.fold(terms) { acc, (key, value) -> acc.replace(key, value) }
    }
}
