package com.mytiki.publish.client.license

import android.content.Context
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.license.rsp.RspCreate
import com.mytiki.publish.client.license.rsp.RspVerify
import com.mytiki.publish.client.offer.*
import com.mytiki.publish.client.utils.apiService.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/** Service for managing licenses. */
class LicenseService {
  private val baseUrl = "https://trail.mytiki.com"

  /**
   * Creates a new license for the user.
   *
   * @param context The context. This is typically the application context or the current activity.
   * @param use The use case for which the license is being created.
   * @param tags An array of tags associated with the license.
   * @return Boolean indicating the success of the license creation. Returns true if the license was
   *   successfully created, false otherwise.
   * @throws Exception if there is an error creating the license. The exception message contains
   *   details about the error.
   */
  suspend fun create(context: Context, use: Use, tags: List<Tag>) =
      manageLicense(context, use, tags)

  /**
   * Revokes the license for the user.
   *
   * @param context The context. This is typically the application context or the current activity.
   * @return Boolean indicating the success of the license revocation. Returns true if the license
   *   was successfully revoked, false otherwise.
   * @throws Exception if there is an error revoking the license. The exception message contains
   *   details about the error.
   */
  suspend fun revoke(context: Context) = manageLicense(context, null, null)

  /**
   * Manages the license for the user. This is a private function used by the create and revoke
   * functions.
   *
   * @param context The context. This is typically the application context or the current activity.
   * @param use The use case for which the license is being managed. This is null when revoking the
   *   license.
   * @param tags An array of tags associated with the license. This is null when revoking the
   *   license.
   * @return Boolean indicating the success of the license management. Returns true if the license
   *   was successfully managed, false otherwise.
   * @throws Exception if there is an error managing the license. The exception message contains
   *   details about the error.
   */
  private suspend fun manageLicense(context: Context, use: Use?, tags: List<Tag>?): Boolean {
    val licenseRequest = LicenseRequest()
    val jsonBody = licenseRequest.toJSON(context, use, tags)
    val body = jsonBody.toString().toRequestBody("application/json".toMediaType())
    val addressToken = TikiClient.auth.addressToken().await()
    val response =
        ApiService.post(
                header =
                    mapOf(
                        "Authorization" to "Bearer $addressToken",
                        "Content-Type" to "application/json",
                    ),
                endPoint = "$baseUrl/license/create",
                onError = Exception("error on creating license"),
                body = body)
            .await() ?: throw Exception("error on creating license")
    val rspCreate = RspCreate.fromJson(JSONObject(response.string()))
    return rspCreate.id.isNotEmpty()
  }

  /**
   * Verifies the validity of the user's license.
   *
   * @return Boolean indicating whether the license is valid.
   * @throws Exception if there is an error verifying the license.
   */
  suspend fun verify(): Boolean {
    val response =
        ApiService.post(
                header =
                    mapOf(
                        "Authorization" to "Bearer ${TikiClient.auth.addressToken().await()}",
                        "Content-Type" to "application/json",
                    ),
                endPoint = "$baseUrl/license/verify",
                onError = Exception("error on creating license"))
            .await()
    if (response == null) throw Exception("error on verify license")
    return RspVerify.fromJson(JSONObject(response.string())).verified
  }

  /**
   * Retrieves the terms of service.
   *
   * @param context The context.
   * @return The terms of service as a String.
   */
  fun terms(context: Context): String {
    val terms = context.assets.open("terms.md").bufferedReader().use { it.readText() }
    if (TikiClient.config != null) {
      val replacements =
          mapOf(
              "{{{COMPANY}}}" to TikiClient.config!!.companyName,
              "{{{JURISDICTION}}}" to TikiClient.config!!.companyJurisdiction,
              "{{{TOS}}}" to TikiClient.config!!.tosUrl,
              "{{{POLICY}}}" to TikiClient.config!!.privacyUrl)
      return replacements.entries.fold(terms) { acc, (key, value) -> acc.replace(key, value) }
    } else {
      throw Exception("Config not found")
    }
  }
}
