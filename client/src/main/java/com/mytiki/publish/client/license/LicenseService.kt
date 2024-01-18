package com.mytiki.publish.client.license

import com.mytiki.tiki_sdk_android.trail.LicenseRecord

class LicenseService {
    /**
     * Creates a new license for the user.
     * @return The created LicenseRecord.
     */
    fun create(): LicenseRecord {
        throw NotImplementedError()
    }

    /**
     * Retrieves the user's active license.
     * @return The user's active license.
     */
    fun get(): LicenseRecord {
        throw NotImplementedError()
    }

    /**
     * Revokes the user's existing license.
     * @return The revoked license.
     */
    fun revoke(): LicenseRecord {
        throw NotImplementedError()
    }

    /**
     * Verifies the validity of the user's license.
     * @return True if the license is valid, false otherwise.
     */
    fun verify(): Boolean{
        return false
    }
}