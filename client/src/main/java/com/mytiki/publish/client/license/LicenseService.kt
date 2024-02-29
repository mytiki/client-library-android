package com.mytiki.publish.client.license

import android.content.Context
import android.util.Log
import com.mytiki.publish.client.capture.Company
import com.mytiki.tiki_sdk_android.TikiSdk
import com.mytiki.tiki_sdk_android.trail.LicenseRecord
import com.mytiki.tiki_sdk_android.trail.Tag
import com.mytiki.tiki_sdk_android.trail.TagCommon
import com.mytiki.tiki_sdk_android.trail.TitleRecord
import com.mytiki.tiki_sdk_android.trail.Use
import com.mytiki.tiki_sdk_android.trail.Usecase
import com.mytiki.tiki_sdk_android.trail.UsecaseCommon
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

/**
 * Service for managing licenses.
 */
class LicenseService {

    /**
     * The current license status.
     */
    private var isLicensed: Boolean = false

    /**
     * Company information.
     */
    var company: Company = Company("", "", "", "")
        private set

    /**
     * Tiki Publishing ID.
     */
    var tikiPublishingID: String? = null
        private set

    /**
     * User ID.
     */
    var userId: String? = null
        private set

    /**
     * Redirect URI.
     */
    var redirectUri: String? = null
        private set

    private var titleRecord: TitleRecord? = null

    /**
     * Creates a new license for the user.
     * @param context The context.
     * @param userId The user ID.
     * @param providerId The provider ID.
     * @param terms The terms.
     * @return The created LicenseRecord.
     */
    suspend fun create(context: Context, userId: String, providerId: String, terms: String): LicenseRecord {
        checkInitialization(context, userId, providerId).await()
        return TikiSdk.trail.license.create(
            titleRecord!!.id,
            listOf(Use(listOf(Usecase(UsecaseCommon.ATTRIBUTION)))),
            terms,
            null,
            "Receipt data"
        ).await()
    }

    /**
     * Retrieves the user's active license.
     * @return The user's active license.
     */
    suspend fun get(): LicenseRecord? {
        if (titleRecord == null) {
            return null
        }
        val licenses = TikiSdk.trail.license.all(titleRecord!!.id).await()
        return if (licenses.isNotEmpty()) licenses.last() else null
    }

    /**
     * Revokes the user's existing license.
     * @param context The context.
     * @param userId The user ID.
     * @param providerId The provider ID.
     * @return The revoked license.
     */
    suspend fun revoke(context: Context, userId: String, providerId: String): LicenseRecord {
        checkInitialization(context, userId, providerId).await()
        return TikiSdk.trail.license.create(
            titleRecord!!.id,
            listOf(),
            "revoked license",
            null,
            "Receipt data"
        ).await()
    }

    /**
     * Verifies the validity of the user's license.
     * @param userId The user ID.
     * @return True if the license is valid, false otherwise.
     */
    suspend fun verify(userId: String): Boolean {
        if (titleRecord == null) {
            return false
        }
        return TikiSdk.trail.guard(
            userId,
            listOf(Usecase(UsecaseCommon.ATTRIBUTION)),
            listOf("*"),
        ).await()
    }

    /**
     * Retrieves the current license status.
     * @return `true` if the app is licensed, `false` otherwise.
     */
    fun isLicensed(): Boolean {
        Log.d("**********", "state: $$isLicensed")
        return isLicensed
    }

    /**
     * Accepts the data license agreement.
     */
    fun accept() {
        val before = isLicensed!!
        isLicensed = true
        Log.d("**********", "before: $before ------ after: $$isLicensed")
    }

    /**
     * Declines the data license agreement.
     */
    fun decline() {
        val before = isLicensed!!
        isLicensed = false
        Log.d("**********", "before: $before ------ after: $$isLicensed")
    }

    /**
     * Retrieves an estimate of the license duration.
     * @return [LicenseEstimate] object containing the minimum and maximum duration.
     */
    fun estimate(): LicenseEstimate {
        return LicenseEstimate(5, 15)
    }

    /**
     * Retrieves earnings information related to the license.
     * @return [LicenseEarnings] object containing total earnings, rating, and bonus.
     */
    fun earnings(): LicenseEarnings {
        return LicenseEarnings(34.30, 4.8, 12.00)
    }

    /**
     * Sets company information.
     * @param name The name of the company.
     * @param jurisdiction The jurisdiction of the company.
     * @param privacy The privacy policy of the company.
     * @param terms The terms of service of the company.
     */
    fun company(
        name: String,
        jurisdiction: String,
        privacy: String,
        terms: String
    ) {
        company = Company(name, jurisdiction, privacy, terms)
    }

    /**
     * Sets company information.
     * @param company The company information.
     */
    fun company(company: Company) {
        this.company = company
    }

    /**
     * Sets the Tiki Publishing ID.
     * @param id The Tiki Publishing ID.
     */
    fun tikiPublishingID(id: String) {
        tikiPublishingID = id
    }

    /**
     * Sets the user ID.
     * @param id The user ID.
     */
    fun userId(id: String) {
        userId = id
    }

    /**
     * Sets the redirect URI.
     * @param uri The redirect URI.
     */
    fun redirectUri(uri: String) {
        redirectUri = uri
    }

    /**
     * Retrieves the terms and conditions associated with the license.
     * @return String containing the terms and conditions.
     */
    fun terms(): String {
        return "${company.name} ${company.jurisdiction} ${company.privacy} ${company.terms}"
    }

    private fun checkInitialization(
        context: Context,
        userId: String,
        providerId: String
    ): Deferred<Unit> {
        return MainScope().async {
            if (titleRecord == null || TikiSdk.id != userId) {
                TikiSdk.initialize(userId, providerId, context).await()
                titleRecord = TikiSdk.trail.title.create(
                    userId,
                    listOf(Tag(TagCommon.PURCHASE_HISTORY))
                ).await()
            }
        }
    }
}
