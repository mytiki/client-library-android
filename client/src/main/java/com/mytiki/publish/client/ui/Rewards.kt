/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.mytiki.apps_receipt_rewards.capture.CaptureService
import com.mytiki.apps_receipt_rewards.card.Card
import com.mytiki.apps_receipt_rewards.card.CardService
import com.mytiki.publish.client.ui.license.LicenseService
import java.util.UUID

/**
 * [Rewards] class is the main API to interact with TIKI Rewards program.
 *
 * ## Overview
 *
 * The Rewards class works as a singleton and initializes the services for:
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
 * Rewards.start(context)
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
 * Rewards.start(context, customTheme)
 * ```
 */
object Rewards {

    /**
     * An instance of [CaptureService] for handling data capture functionalities.
     */
    val capture: CaptureService = CaptureService()

    /**
     * An instance of [LicenseService] for managing data licenses.
     */
    val license: LicenseService = LicenseService()

    /**
     * An instance of [ThemeService] for managing the app's theme.
     */
    val theme: ThemeService = ThemeService()

    /**
     * An instance of [CardService] for managing user cards.
     */
    val card: CardService = CardService()

    private var userId: String = UUID.randomUUID().toString()

    var googleClientID: String? = null
    var outlookClientID: String? = null

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
        this.userId = userId


        val intent = Intent(context, RewardsActivity::class.java)
        context.startActivity(intent)
    }

    /**
     * Configures company details for licensing purposes.
     *
     * @param name The name of the company.
     * @param jurisdiction The jurisdiction of the company.
     * @param privacy The privacy policy of the company.
     * @param terms The terms and conditions of the company.
     */
    fun company(
        name: String = "Company Inc.",
        jurisdiction: String = "Tennessee, USA",
        privacy: String = "https://your-co.com/privacy",
        terms: String = "https://your-co.com/terms",
    ){
        license.company(name, jurisdiction, privacy, terms)
    }

    /**
     * Configures license keys required for services.
     *
     * @param tikiPublishingID The TIKI publishing ID.
     * @param microblinkLicenseKey The Microblink license key.
     * @param productIntelligenceKey The product intelligence key.
     */
    fun licenses(
        tikiPublishingID: String = "be19730a-00d5-45f5-b18e-2e19eb25f311",
        microblinkLicenseKey: String = "sRwAAAAoY29tLm15dGlraS5zZGsuY2FwdHVyZS5yZWNlaXB0LmNhcGFjaXRvcgY6SQlVDCCrMOCc/jLI1A3BmOhqNvtZLzShMcb3/OLQLiqgWjuHuFiqGfg4fnAiPtRcc5uRJ6bCBRkg8EsKabMQkEsMOuVjvEOejVD497WkMgobMbk/X+bdfhPPGdcAHWn5Vnz86SmGdHX5xs6RgYe5jmJCSLiPmB7cjWmxY5ihkCG12Q==",
        productIntelligenceKey: String = "wSNX3mu+YGc/2I1DDd0NmrYHS6zS1BQt2geMUH7DDowER43JGeJRUErOHVwU2tz6xHDXia8BuvXQI3j37I0uYw=="
    ){
        license.licenses(tikiPublishingID, microblinkLicenseKey, productIntelligenceKey)
    }

    /**
     * Configures OAuth keys for 3rd party services.
     *
     * @param gmailAPIKey The API key for Gmail.
     * @param outlookAPIKey The API key for Outlook.
     * @param context The application context.
     * @param userId The unique identifier of the user.
     */
    fun oauth(
        context: Context? = null,
        userId: String? = null,
        googleClientID: String?,
        outlookClientID: String?,
    ){
        capture.oauth(context, userId, googleClientID, outlookClientID )
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
        theme.theme(primaryTextColor, secondaryTextColor, primaryBackgroundColor, secondaryBackgroundColor, accentColor, fontFamily)
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
        licenses(tikiPublishingID, microblinkLicenseKey, productIntelligenceKey)
        oauth(context, userId, gmailClientID, outlookClientID)
        theme(primaryTextColor, secondaryTextColor, primaryBackgroundColor, secondaryBackgroundColor, accentColor, fontFamily)
        show(context, userId)
    }

    /**
     * Logs out the user from the account.
     *
     * @param context The application context.
     */
    fun logout(context: Context){

    }

    /**
     * Adds one or more user cards.
     *
     * @param cards The cards to be added.
     * @return List of cards after addition.
     */
    fun cards(vararg cards: Card): List<Card>{
        card.addCard(cards.toList())
        return card.getCards()
    }

    /**
     * Adds a list of user cards.
     *
     * @param cards The list of cards to be added.
     * @return List of cards after addition.
     */
    fun cards(cards: List<Card>): List<Card>{
        card.addCard(cards)
        return card.getCards()
    }

    /**
     * Retrieves the list of user cards.
     *
     * @return List of user cards.
     */
    fun cards() = card.getCards()

    /**
     * Removes a user card.
     *
     * @param card The card to be removed.
     */
    fun removeCard(card: Card){
        this.card.removeCard(card)
    }


    /**
     * Displays an error dialog with the given message and title.
     *
     * @param context The application context.
     * @param message The error message to display.
     * @param title The title of the error dialog (optional).
     */
     private fun onError(context: Context, message: String, title: String? = null){

        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
            .show()
    }
}
