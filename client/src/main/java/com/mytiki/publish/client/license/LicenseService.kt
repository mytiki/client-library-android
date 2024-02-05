package com.mytiki.publish.client.license

import android.content.Context
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

class LicenseService {

    private var titleRecord: TitleRecord? = null

    /**
     * Creates a new license for the user.
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
        if(titleRecord == null){
            return null
        }
        val licenses = TikiSdk.trail.license.all(titleRecord!!.id).await()
        return if(licenses.isNotEmpty()) licenses.last() else null
    }

    /**
     * Revokes the user's existing license.
     * @return The revoked license.
     */
    suspend fun revoke(context:Context, userId:String, providerId: String): LicenseRecord {
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
     * @return True if the license is valid, false otherwise.
     */
    suspend fun verify(userId: String): Boolean{
        if(titleRecord == null){
            return false
        }
        return TikiSdk.trail.guard(
            userId,
            listOf(Usecase(UsecaseCommon.ATTRIBUTION)),
            listOf("*"),
        ).await()
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