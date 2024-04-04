package com.mytiki.publish.client.license

import android.content.Context
import com.mytiki.publish.client.TikiClient
import com.mytiki.tiki_sdk_android.TikiSdk
import com.mytiki.tiki_sdk_android.trail.LicenseRecord
import com.mytiki.tiki_sdk_android.trail.TitleRecord
import com.mytiki.tiki_sdk_android.trail.Use
import com.mytiki.tiki_sdk_android.trail.Usecase
import com.mytiki.tiki_sdk_android.trail.UsecaseCommon

/**
 * Service for managing licenses.
 */
class LicenseService {

    /**
     * The current license status.
     */
    private var isLicensed: Boolean = false

    private var titleRecord: TitleRecord? = null



    /**
     * Creates a new license for the user.
     * @param context The context.
     * @param userId The user ID.
     * @param providerId The provider ID.
     * @param terms The terms.
     * @return The created LicenseRecord.
     */
    suspend fun create(context: Context, terms: String): LicenseRecord {
        return TikiSdk.trail.license.create(
            titleRecord!!.id,
            listOf(Use(listOf(Usecase(UsecaseCommon.ATTRIBUTION)))),
            terms,
            null,
            "Receipt data"
        ).await()
    }

    /**
     * Revokes the user's existing license.
     * @param context The context.
     * @param userId The user ID.
     * @param providerId The provider ID.
     * @return The revoked license.
     */
//    suspend fun revoke(context: Context): LicenseRecord {
//    }

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
     * Retrieves the terms and conditions associated with the license.
     * @return String containing the terms and conditions.
     */
    fun terms(): String {
        return "${TikiClient.config.companyName} ${TikiClient.config.companyJurisdiction} ${TikiClient.config.privacyUrl} ${TikiClient.config.tosUrl}"
    }
}
