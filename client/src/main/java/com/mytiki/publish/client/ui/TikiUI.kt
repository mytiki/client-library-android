/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.mytiki.publish.client.TikiClient
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
 * // Start rewards system with default themeObj
 * TikiUI.start(context)
 *
 * // Start rewards system with a custom themeObj
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
class TikiUI private constructor(){
    companion object {
        val theme = ThemeService()
    }
    private constructor(builder: Builder): this() {
        TikiClient.license.company(builder.companyConfig)
        TikiClient.license.tikiPublishingID(builder.tikiPublishingID)
        TikiClient.license.userId(builder.ID)
        TikiClient.license.redirectUri(builder.Uri)
        TikiClient.email.googleKeys(builder.googleEmailKeys.clientId, builder.googleEmailKeys.clientSecret)

        builder.outlookEmailKeys?.let { TikiClient.email.outlookKeys(it.clientId, builder.outlookEmailKeys!!.clientSecret) }
        if (builder.themeObj != null) theme.setTheme(builder.themeObj!!) else theme.setTheme(Theme())
    }

    class Builder{
        lateinit var googleEmailKeys: EmailKeys
            private set
        var outlookEmailKeys: EmailKeys? = null
            private set
        lateinit var tikiPublishingID: String
            private set
        lateinit var ID: String
            private set
        lateinit var Uri: String
            private set
        lateinit var companyConfig: Company
            private set
        var themeObj: Theme? = null
            private set


        fun googleKeys( clientId: String, clientSecrete: String): Builder {
            googleEmailKeys = EmailKeys(clientId, clientSecrete)
            return this
        }

        fun outlookKeys( clientId: String, clientSecrete: String): Builder {
            outlookEmailKeys = EmailKeys(clientId, clientSecrete)
            return this
        }
        fun publishingID(id: String): Builder {
            tikiPublishingID = id
            return this
        }
        fun userID(id: String): Builder {
            ID = id
            return this
        }
        fun redirectUri(uri: String): Builder {
            Uri = uri
            return this
        }

        fun company(
            name: String = "Company Inc.",
            jurisdiction: String = "Tennessee, USA",
            privacy: String = "https://your-co.com/privacy",
            terms: String = "https://your-co.com/terms",
        ) : Builder {
            companyConfig = Company(name, jurisdiction, privacy, terms)
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
            themeObj = Theme(
                primaryTextColor,
                secondaryTextColor,
                primaryBackgroundColor,
                secondaryBackgroundColor,
                accentColor,
                fontFamily
            )
        }
        fun build() {
            if (!this::tikiPublishingID.isInitialized) throw Exception("set the publishingID")
            if (!this::googleEmailKeys.isInitialized) throw Exception("set the googleKeys")
            if (!this::ID.isInitialized || ID.isEmpty()) throw Exception("set the userID")
            if (!this::Uri.isInitialized || Uri.isEmpty()) throw Exception("set the redirectUri")
            if (!this::companyConfig.isInitialized) throw Exception("set the company")
            TikiClient.tikiUI(TikiUI(this))
        }
    }

    /**
     * Initializes the rewards system and presents the home screen.
     *
     * @param context The application context.
     * @param userId The unique identifier of the user.
     *
     * @throws Exception if companyConfig information or license keys are not configured.
     */
    fun show(
        context: Context,
    ) {
        val intent = Intent(context, UIActivity::class.java)
        context.startActivity(intent)
    }
}
