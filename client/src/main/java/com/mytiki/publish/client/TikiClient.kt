package com.mytiki.publish.client

import android.content.Context
import com.mytiki.publish.client.auth.AuthService
import com.mytiki.publish.client.capture.CaptureService
import com.mytiki.publish.client.capture.Company
import com.mytiki.publish.client.clo.CloService
import com.mytiki.publish.client.clo.Offer
import com.mytiki.publish.client.clo.Reward
import com.mytiki.publish.client.clo.Transaction
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.email.EmailService
import com.mytiki.publish.client.license.LicenseService
import com.mytiki.publish.client.ui.Theme
import com.mytiki.publish.client.ui.UiService


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
        val auth = AuthService()
        val capture = CaptureService()
        val clo = CloService()
        val email = EmailService()
        val license = LicenseService()
        private val ui = UiService()
    }


    /**
     * Initializes the TikiClient with the application context and sets its parameters.
     * @param context The application context.
     * @param providerId The TIKI Publishing ID of the data provider.
     * @param userId The user identification from the provider.
     * @param company The legal information of the company.
     */
    fun initialize(context: Context, providerId: String, userId: String, company: Company) {
    }

    /**
     * Initiates the process of scanning a physical receipt and returns the receipt ID.
     * @return The scanned receipt data or an empty string if the scan is unsuccessful.
     */
    fun scan(): String {
        return ""
    }

    /**
     * Initiates the process of scraping receipts from emails.
     * @param emailProvider The email provider (GOOGLE or OUTLOOK).
     */
    fun login(emailProvider: EmailProviderEnum) {
    }

    /**
     * Removes a previously added email account.
     * @param email The email account to be removed.
     */
    fun logout(email: String) {
    }

    /**
     * Retrieves the list of connected email accounts.
     * @return List of connected email accounts.
     */
    fun accounts(): List<String> {
        return listOf()
    }

    /**
     * Initiates the process of scraping receipts from emails.
     */
    fun scrape() {
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
