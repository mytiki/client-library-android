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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async


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

class TikiClient{

    companion object {
        val auth = AuthService()
        val capture = CaptureService()
        val clo = CloService()
        val email = EmailService()
        val license = LicenseService()
        lateinit var ui: TikiUI
            private set
        fun tikiUI(tikiUI: TikiUI){
            ui = tikiUI
        }
    }


    /**
     * Initiates the process of scanning a physical receipt and returns the receipt ID.
     * @return The scanned receipt data or an empty string if the scan is unsuccessful.
     */
    fun scan(activity: ComponentActivity): String {
        return ""
    }

    /**
     * Initiates the process of login to email.
     * @param provider The email provider (GOOGLE or OUTLOOK).
     */
    fun login(context: Context, provider: EmailProviderEnum, emailKeys: EmailKeys, redirectURI: String, loginCallback: ((String) -> Unit)? = null) {
        email.login(context, provider, emailKeys, redirectURI){ email ->
            TikiClient.email.messagesIndex(context, email)
            if (loginCallback != null) {
                loginCallback(email)
            }
        }
    }

    /**
     * Removes a previously added email account.
     * @param email The email account to be removed.
     */
    fun logout(context: Context, email: String) {
        TikiClient.email.logout(context, email)
    }

    /**
     * Retrieves the list of connected email accountsPerProvider.
     * @return List of connected email accountsPerProvider.
     */
    fun accounts(context: Context): List<String> {
        val emailList = mutableListOf<String>()
        EmailProviderEnum.entries.forEach { provider ->
            val list = email.accountsPerProvider(context, provider)
            if(list.isNotEmpty()) emailList.addAll(list)
        }
        return emailList
    }

    /**
     * Initiates the process of scraping receipts from emails.
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
     * Displays the widget for pre-built UIs with a custom themeObj.
     * @param theme The custom themeObj for the widget.
     */
    fun widget(theme: Theme?) {
    }
}