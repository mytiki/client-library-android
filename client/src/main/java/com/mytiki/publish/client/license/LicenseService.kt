package com.mytiki.publish.client.license

import android.content.Context
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.utils.apiService.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Service for managing licenses.
 */
class LicenseService {

    /**
     * The current license status.
     */
    private var isLicensed: Boolean = false

    private val baseUrl = "https://trail.mytiki.com"

    /**
     * Creates a new license for the user.
     * @param context The context.
     * @param userId The user ID.
     * @param providerId The provider ID.
     * @param terms The terms.
     * @return The created LicenseRecord.
     */
    suspend fun create(context: Context): Boolean {
        val licenseRequest = LicenseRequest(TikiClient.userID)
        val jsonBody = licenseRequest.toJSON(context)
        val body = jsonBody.toString().toRequestBody("application/json".toMediaType())
        val addressToken = TikiClient.auth.addressToken().await()
        ApiService.post(
            header = mapOf(
                "Authorization" to "Bearer $addressToken",
                "Content-Type" to "application/json",
            ),
            endPoint = "https://trail.mytiki.com/license/create",
            onError = Exception("error on creating license"),
            body = body
        ).await()
        return true
    }

    /**
     * Revokes the user's existing license.
     * @param context The context.
     * @param userId The user ID.
     * @param providerId The provider ID.
     * @return The revoked license.
     */
    suspend fun revoke(context: Context) {}

    /**
     * Verifies the validity of the user's license.
     * @param userId The user ID.
     * @return True if the license is valid, false otherwise.
     */
    suspend fun verify(): Boolean {
        ApiService.post(
            header = mapOf(
                "Authorization" to "Bearer ${TikiClient.auth.addressToken().await()}",
                "Content-Type" to "application/json",
            ),
            endPoint = "https://trail.mytiki.com/license/verify",
            onError = Exception("error on creating license")
        ).await()
        return true
    }

    /**
     * Retrieves the terms of service.
     * @param context The context.
     * @return The terms of service.
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
