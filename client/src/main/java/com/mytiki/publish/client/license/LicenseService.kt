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
   * @param offerUses An array of offerUses for which the license is being created.
   * @param offerTags An array of offerTags associated with the license.
   * @return Boolean indicating the success of the license creation. Returns true if the license was
   *   successfully created, false otherwise.
   * @throws Exception if there is an error creating the license. The exception message contains
   *   details about the error.
   */
  suspend fun create(
      context: Context,
      ptr: String,
      description: String,
      offerUses: List<OfferUse>,
      offerTags: List<OfferTag>
  ) = manageLicense(context, ptr, description, offerUses, offerTags)

  /**
   * Creates a new license for the user using an Offer object.
   *
   * @param context The context. This is typically the application context or the current activity.
   * @param offer The Offer object containing the details of the license to be created.
   * @return Boolean indicating the success of the license creation. Returns true if the license was
   *   successfully created, false otherwise.
   * @throws Exception if there is an error creating the license. The exception message contains
   *   details about the error.
   */
  suspend fun create(context: Context, offer: Offer) =
      manageLicense(context, offer.ptr, offer.description, offer.offerUses, offer.offerTags)

  /**
   * Revokes the license for the user.
   *
   * @param context The context. This is typically the application context or the current activity.
   * @return Boolean indicating the success of the license revocation. Returns true if the license
   *   was successfully revoked, false otherwise.
   * @throws Exception if there is an error revoking the license. The exception message contains
   *   details about the error.
   */
  suspend fun revoke(context: Context, ptr: String, description: String, offerTags: List<OfferTag>) =
      manageLicense(context, ptr, description, emptyList(), offerTags)

  /**
   * Revokes the license for the user using an Offer object.
   *
   * @param context The context. This is typically the application context or the current activity.
   * @param offer The Offer object containing the details of the license to be revoked.
   * @return Boolean indicating the success of the license revocation. Returns true if the license
   *   was successfully revoked, false otherwise.
   * @throws Exception if there is an error revoking the license. The exception message contains
   *   details about the error.
   */
  suspend fun revoke(context: Context, offer: Offer) =
      manageLicense(context, offer.ptr, offer.description, emptyList(), offer.offerTags)

  /**
   * Manages the license for the user. This is a private function used by the create and revoke
   * functions.
   *
   * @param context The context. This is typically the application context or the current activity.
   * @param offerUses An array of offerUses for which the license is being managed. This is null when revoking
   *   the license.
   * @param offerTags An array of offerTags associated with the license. This is null when revoking the
   *   license.
   * @return Boolean indicating the success of the license management. Returns true if the license
   *   was successfully managed, false otherwise.
   * @throws Exception if there is an error managing the license. The exception message contains
   *   details about the error.
   */
  private suspend fun manageLicense(
      context: Context,
      prt: String,
      description: String,
      offerUses: List<OfferUse>,
      offerTags: List<OfferTag>
  ): Boolean {
    val licenseRequest =
        LicenseRequest(
            ptr = prt,
            offerTags = offerTags,
            offerUses = offerUses,
            description = description,
            expiry = null,
            terms = terms(context))
    val jsonBody = licenseRequest.toJSON(context)
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
   * Verifies the validity of a user's license.
   *
   * This function sends a POST request to the license verification endpoint of the server. The
   * server responds with the verification status of the license.
   *
   * @return A Boolean indicating the verification status of the license. It returns true if the
   *   license is valid, false otherwise.
   * @throws Exception if there is an error during the verification process. The exception message
   *   contains details about the error. For example, it throws an exception if the server response
   *   is null.
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
                onError = Exception("error on creating license"),
            )
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
