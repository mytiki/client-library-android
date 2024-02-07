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
class TikiUI private constructor(){
    companion object {
        val theme = ThemeService()
    }
    private constructor(builder: Builder): this() {
        TikiClient.license.company(builder.company)
        TikiClient.license.tikiPublishingID(builder.tikiPublishingID)
        TikiClient.license.userId(builder.userId)
        TikiClient.license.redirectUri(builder.redirectUri)
        TikiClient.email.googleKeys(builder.googleKeys.clientId, builder.googleKeys.clientSecret)
        TikiClient.email.outlookKeys(builder.outlookKeys.clientId, builder.outlookKeys.clientSecret)
        theme.setTheme(builder.theme)
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
        lateinit var redirectUri: String
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
        fun redirectUri(uri: String): Builder {
            redirectUri = uri
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
        fun build() {
//            if (tikiPublishingID.isNullOrEmpty()) throw Exception("set the tikiPublishingID")
            if (googleKeys != null) throw Exception("set the googleKeys")
            if (userId.isNullOrEmpty()) throw Exception("set the userId")
            if (redirectUri.isNullOrEmpty()) throw Exception("set the redirectUri")
            if (company != null) throw Exception("set the company")
            TikiClient.tikiUI(TikiUI(this))
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
    ) {
        val intent = Intent(context, UIActivity::class.java)
        context.startActivity(intent)
    }
}
