/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.license

import com.mytiki.publish.client.ui.license.Company
import com.mytiki.publish.client.ui.license.LicenseEarnings
import com.mytiki.publish.client.ui.license.LicenseEstimate
import com.mytiki.publish.client.ui.license.LicenseKeys

/**
 * [LicenseService] manages licensing-related functionalities using TIKI infrastructure,
 * including license status, acceptance, decline, and providing license-related information.
 *
 * ## Example
 *
 * To use the [LicenseService] class, follow the example below:
 *
 * ```kotlin
 * val licenseService = LicenseService()
 *
 * // Check if there is a valid license for the user data.
 * val isLicensed = licenseService.isLicensed()
 *
 * // Accept the license agreement.
 * licenseService.accept()
 *
 * // Decline the license agreement.
 * licenseService.decline()
 *
 * // Retrieve an estimate of the license returns for the user.
 * val licenseEstimate = licenseService.estimate()
 *
 * // Retrieve earnings information related to the license.
 * val licenseEarnings = licenseService.earnings()
 *
 * // Retrieve the terms and conditions.
 * val termsAndConditions = licenseService.terms()
 * ```
 */
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

    var licenseKeys: LicenseKeys = LicenseKeys(
        "",
        "",
        ""
    )
        private set


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

    fun licenses(
        tikiPublishingID: String,
        microblinkLicenseKey: String,
        productIntelligenceKey: String,
    ){
        licenseKeys = LicenseKeys(tikiPublishingID, microblinkLicenseKey, productIntelligenceKey)
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
