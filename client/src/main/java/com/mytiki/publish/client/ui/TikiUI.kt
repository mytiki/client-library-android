/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.TikiClient.Companion.license
import com.mytiki.publish.client.capture.CaptureService
import com.mytiki.publish.client.capture.Company
import com.mytiki.publish.client.email.EmailKeys
import com.mytiki.publish.client.license.LicenseService

/**
 * [TikiUI] class is the main API to interact with TIKI TikiUI program.
 *
 * ## Overview
 *
 * The TikiUI class works as a singleton and initializes the services for:
 * - theming: [Theme]
 * - 3rd party account management: [AccountService]
 * - capture user data: [CaptureService]
 * - data license handling: [LicenseService]
 *
 * ## Example
 *
 * To get started with the rewards system, use the following example:
 *
 * ```kotlin
 * // Start rewards system with default theme
 * TikiUI.start(context)
 *
 * // Start rewards system with a custom theme
 * val customTheme = Theme(
 *      primaryTextColor = Color.Black,
 *      secondaryTextColor = Color.Gray,
 *      primaryBackgroundColor = Color.White,
 *      secondaryBackgroundColor = Color.LightGray,
 *      accentColor = Color.Blue,
 *      fontFamily = FontFamily.Serif
 * )
 * TikiUI.start(context, customTheme)
 * ```
 */
class TikiUI private constructor(
    var tikiPublishingID: String? = null,
    var userId: String? = null,
) {

    companion object {
        val theme = ThemeService()
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    private constructor(builder: Builder): this(
        tikiPublishingID = builder.tikiPublishingID,
        userId = builder.userId,
    ){
        license.company(builder.company!!)
        TikiClient.email.googleKeys(builder.googleKeys.clientId, builder.googleKeys.clientSecret)
        TikiClient.email.outlookKeys(builder.outlookKeys.clientId, builder.outlookKeys.clientSecret)
    }

    class Builder{
        lateinit var googleKeys: EmailKeys
            private set
        lateinit var outlookKeys: EmailKeys
            private set
        lateinit var tikiPublishingID: String
            private set
        lateinit var userId: String
            private set
        lateinit var company: Company
            private set
        lateinit var theme: Theme
            private set


        fun googleKeys( clientId: String, clientSecrete: String): Builder {
            googleKeys = EmailKeys(clientId, clientSecrete)
            return this
        }

        fun outlookKeys( clientId: String, clientSecrete: String): Builder {
            outlookKeys = EmailKeys(clientId, clientSecrete)
            return this
        }
        fun tikiPublishingID(id: String): Builder {
            tikiPublishingID = id
            return this
        }
        fun userId(id: String): Builder {
            userId = id
            return this
        }
        fun company(
            name: String = "Company Inc.",
            jurisdiction: String = "Tennessee, USA",
            privacy: String = "https://your-co.com/privacy",
            terms: String = "https://your-co.com/terms",
        ) : Builder {
            company = Company(name, jurisdiction, privacy, terms)
            return this
        }
        fun theme(
            primaryTextColor: Color,
            secondaryTextColor: Color,
            primaryBackgroundColor: Color,
            secondaryBackgroundColor: Color,
            accentColor: Color,
            fontFamily: FontFamily
        ){
            theme = Theme(
                primaryTextColor,
                secondaryTextColor,
                primaryBackgroundColor,
                secondaryBackgroundColor,
                accentColor,
                fontFamily
            )
        }
        fun build(): TikiUI {
            if (tikiPublishingID.isNullOrEmpty()) throw Exception("set the tikiPublishingID")
            if (googleKeys != null) throw Exception("set the googleKeys")
            if (userId.isNullOrEmpty()) throw Exception("set the userId")
            if (company != null) throw Exception("set the company")

            return TikiUI(this)
        }
    }

    /**
     * Initializes the rewards system and presents the home screen.
     *
     * @param context The application context.
     * @param userId The unique identifier of the user.
     *
     * @throws Exception if company information or license keys are not configured.
     */
    fun show(
        context: Context,
        userId: String,
    ) {

        val intent = Intent(context, UIActivity::class.java)
        context.startActivity(intent)
    }

    /**
     * Configures the app's theme.
     *
     * @param primaryTextColor The primary text color.
     * @param secondaryTextColor The secondary text color.
     * @param primaryBackgroundColor The primary background color.
     * @param secondaryBackgroundColor The secondary background color.
     * @param accentColor The accent color.
     * @param fontFamily The font family to be used.
     */
    fun theme(
        primaryTextColor: Color,
        secondaryTextColor: Color,
        primaryBackgroundColor: Color,
        secondaryBackgroundColor: Color,
        accentColor: Color,
        fontFamily: FontFamily
    ){
        theme Theme(
            primaryTextColor,
            secondaryTextColor,
            primaryBackgroundColor,
            secondaryBackgroundColor,
            accentColor,
            fontFamily
        )
    }


    /**
     * Configures various settings and initializes the rewards system.
     * This function combines company details, licenses, OAuth keys, theme, and initialization.
     *
     * @param context The application context.
     * @param userId The unique identifier of the user.
     * @param companyName The name of the company.
     * @param companyJurisdiction The jurisdiction of the company.
     * @param privacy The privacy policy of the company.
     * @param terms The terms and conditions of the company.
     * @param tikiPublishingID The TIKI publishing ID.
     * @param microblinkLicenseKey The Microblink license key.
     * @param productIntelligenceKey The product intelligence key.
     * @param gmailAPIKey The API key for Gmail.
     * @param outlookAPIKey The API key for Outlook.
     * @param primaryTextColor The primary text color (optional).
     * @param secondaryTextColor The secondary text color (optional).
     * @param primaryBackgroundColor The primary background color (optional).
     * @param secondaryBackgroundColor The secondary background color (optional).
     * @param accentColor The accent color (optional).
     * @param fontFamily The font family to be used (optional).
     */
    fun config(
        context: Context,
        userId: String,
        companyName: String,
        companyJurisdiction: String,
        privacy: String,
        terms: String,
        tikiPublishingID: String,
        microblinkLicenseKey: String,
        productIntelligenceKey: String,
        gmailClientID: String? = null,
        outlookClientID: String? = null,
        primaryTextColor: Color = Theme().primaryTextColor, // optional
        secondaryTextColor: Color = Theme().secondaryTextColor, // optional
        primaryBackgroundColor: Color = Theme().primaryBackgroundColor, // optional
        secondaryBackgroundColor: Color = Theme().secondaryBackgroundColor, // optional
        accentColor: Color = Theme().accentColor, // optional
        fontFamily: FontFamily = Theme().fontFamily,
    ){
        company(companyName, companyJurisdiction, privacy, terms)

        theme(primaryTextColor, secondaryTextColor, primaryBackgroundColor, secondaryBackgroundColor, accentColor, fontFamily)
        show(context, userId)
    }
}
