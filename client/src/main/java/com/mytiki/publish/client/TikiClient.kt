package com.mytiki.publish.client

import android.content.Context
import androidx.activity.ComponentActivity
import com.mytiki.publish.client.auth.AuthService
import com.mytiki.publish.client.capture.CaptureService
import com.mytiki.publish.client.clo.CloService
import com.mytiki.publish.client.clo.Offer
import com.mytiki.publish.client.clo.Reward
import com.mytiki.publish.client.clo.Transaction
import com.mytiki.publish.client.email.EmailKeys
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.email.EmailService
import com.mytiki.publish.client.license.LicenseService
import com.mytiki.publish.client.ui.Theme
import com.mytiki.publish.client.ui.TikiUI

/**
 * Tiki Client Library
 *
 * The TIKI APIs comprise a set of HTTP REST APIs designed for seamless integration with any
 * standard HTTP client. The Client Libraries serve as a user-friendly layer around the TIKI APIs,
 * introducing methods for common operations such as authorization, licensing, capture, card-linked
 * offers, and rewards. It is a collection of pre-existing code with minimal dependencies, offering
 * a streamlined integration process with TIKI Rest APIs, which reduces the amount of code necessary
 * for integration.
 *
 * TikiClient is the top-level entry point for of the TIKI Client Library. It offers simple methods
 * that calls the underlying libraries to perform common operations. Programmers can use it to
 * simplify the integration process or opt for individual libraries based on their specific needs.
 */
class TikiClient {

    companion object {
        /**
         * AuthService instance for handling authentication.
         */
        val auth = AuthService()

        /**
         * CaptureService instance for handling receipt capture.
         */
        val capture = CaptureService()

        /**
         * CloService instance for managing card-linked offers.
         */
        val clo = CloService()

        /**
         * EmailService instance for managing email operations.
         */
        val email = EmailService()

        /**
         * LicenseService instance for managing licensing.
         */
        val license = LicenseService()

        /**
         * TikiUI instance for managing UI.
         */
        lateinit var ui: TikiUI
            private set

        /**
         * Sets the TikiUI instance.
         * @param tikiUI The TikiUI instance to be set.
         */
        fun tikiUI(tikiUI: TikiUI) {
            ui = tikiUI
        }
    }

    /**
     * Initiates the process of scanning a physical receipt and returns the receipt ID.
     * @param activity The ComponentActivity instance.
     * @return The scanned receipt data or an empty string if the scan is unsuccessful.
     */
    fun scan(activity: ComponentActivity): String {
        return ""
    }

    /**
     * Initiates the process of logging in to an email account.
     * @param context The Context instance.
     * @param provider The email provider (GOOGLE or OUTLOOK).
     * @param emailKeys The EmailKeys instance.
     * @param redirectURI The redirect URI.
     * @param loginCallback Optional callback function after login.
     */
    fun login(
        context: Context,
        provider: EmailProviderEnum,
        emailKeys: EmailKeys,
        redirectURI: String,
        loginCallback: ((String) -> Unit)? = null
    ) {
        email.login(context, provider, emailKeys, redirectURI) { email ->
            TikiClient.email.messagesIndex(context, email)
            loginCallback?.invoke(email)
        }
    }

    /**
     * Removes a previously added email account.
     * @param context The Context instance.
     * @param email The email account to be removed.
     */
    fun logout(context: Context, email: String) {
        TikiClient.email.logout(context, email)
    }

    /**
     * Retrieves the list of connected email accounts.
     * @param context The Context instance.
     * @return List of connected email accounts.
     */
    fun accounts(context: Context): List<String> {
        val emailList = mutableListOf<String>()
        EmailProviderEnum.values().forEach { provider ->
            val list = email.accountsPerProvider(context, provider)
            if (list.isNotEmpty()) emailList.addAll(list)
        }
        return emailList
    }

    /**
     * Initiates the process of scraping receipts from emails.
     * @param context The Context instance.
     * @param email The email account.
     * @param clientID The client ID.
     */
    fun scrape(context: Context, email: String, clientID: String) {
        TikiClient.email.scrape(context, email, clientID)
    }

    /**
     * Adds a card for card-linked offers.
     * @param last4 Last 4 digits of the card.
     * @param bin Bank Identification Number.
     * @param issuer Card issuer.
     * @param network Card network (VISA, MASTERCARD, AMERICAN EXPRESS, or DISCOVERY).
     */
    fun card(last4: String, bin: String, issuer: String, network: String) {
    }

    /**
     * Retrieves card-linked offers for the user.
     * @return List of card-linked offers.
     */
    fun offers(): List<Offer> {
        return listOf()
    }

    /**
     * Submits a transaction for card-linked offer matching.
     * @param transaction The transaction information.
     */
    fun transaction(transaction: Transaction) {
    }

    /**
     * Retrieves information about the user's rewards.
     * @return List of user rewards.
     */
    fun rewards(): List<Reward> {
        return listOf()
    }

    /**
     * Displays the widget for pre-built UIs with a custom theme.
     * @param theme The custom theme for the widget.
     */
    fun widget(theme: Theme?) {
    }
}
