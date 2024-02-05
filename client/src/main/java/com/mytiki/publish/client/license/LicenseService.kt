package com.mytiki.publish.client.license

import com.mytiki.publish.client.capture.Company
import com.mytiki.publish.client.email.EmailKeys
import com.mytiki.tiki_sdk_android.trail.LicenseRecord

class LicenseService {

    /**
     * The current license status.
     */
    private var isLicensed: Boolean = false

    var company: Company = Company(
        "",
        "",
        "",
        "",
    )
        private set

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

    /**
     * Retrieves the current license status.
     *
     * @return `true` if the app is licensed, `false` otherwise.
     */
    fun isLicensed(): Boolean {
        return isLicensed
    }

    /**
     * Accepts the data license agreement.
     */
    fun accept() {
        isLicensed = true
    }

    /**
     * Declines the data license agreement.
     */
    fun decline() {
        isLicensed = false
    }

    /**
     * Retrieves an estimate of the license duration.
     *
     * @return [LicenseEstimate] object containing the minimum and maximum duration.
     */
    fun estimate(): LicenseEstimate {
        return LicenseEstimate(5, 15)
    }

    /**
     * Retrieves earnings information related to the license.
     *
     * @return [LicenseEarnings] object containing total earnings, rating, and bonus.
     */
    fun earnings(): LicenseEarnings {
        return LicenseEarnings(34.30, 4.8, 12.00)
    }

    fun company(
        name: String,
        jurisdiction: String,
        privacy: String,
        terms: String
    ){
        company = Company(name, jurisdiction, privacy, terms)
    }
    fun company(
        company: Company
    ){
        this.company = company
    }


    /**
     * Retrieves the terms and conditions associated with the license.
     *
     * @return String containing the terms and conditions.
     *
     * @note Replace the placeholder string with your actual terms and conditions.
     */
    fun terms(): String {
        return "${company.name} ${company.jurisdiction} ${company.privacy} ${company.terms}"
    }
}