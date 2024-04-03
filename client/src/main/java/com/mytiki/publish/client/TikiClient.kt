package com.mytiki.publish.client

import android.content.Context
import androidx.activity.ComponentActivity
import com.mytiki.publish.client.auth.AuthService
import com.mytiki.publish.client.capture.CaptureService
import com.mytiki.publish.client.clo.CloService
import com.mytiki.publish.client.clo.Offer
import com.mytiki.publish.client.clo.Reward
import com.mytiki.publish.client.clo.Transaction
import com.mytiki.publish.client.config.Config
import com.mytiki.publish.client.email.EmailKeys
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.email.EmailService
import com.mytiki.publish.client.license.LicenseService

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
object TikiClient {
    /**
     * AuthService instance for handling authentication.
     */
    val auth = AuthService()
        get() {
            check()
            return field
        }

    /**
     * CaptureService instance for handling receipt capture.
     */
    val capture = CaptureService()
        get() {
            check()
            return field
        }

    /**
     * CloService instance for managing card-linked offers.
     */
    val clo = CloService()
        get() {
            check()
            return field
        }

    /**
     * EmailService instance for managing email operations.
     */
    val email = EmailService()
        get() {
            check()
            return field
        }

    /**
     * LicenseService instance for managing licensing.
     */
    val license = LicenseService()
        get() {
            check()
            return field
        }


    lateinit var userID: String
        private set
    lateinit var config: Config
        private set

    private fun check(): Boolean {
        if (this::config.isInitialized) throw Exception(
            "TIKI Client is not configured. Use the TikiClient.configure method to add a configuration."
        ) else if (userID.isEmpty() || this::userID.isInitialized) throw Exception(
            "User ID is not set. Use the TikiClient.initialize method to set the user ID."
        ) else return true
    }

    fun configure(config: Config){
        this.config = config
    }
    fun initialize(userID: String){
        if (this::config.isInitialized) throw Exception(
            "TIKI Client is not configured. Use the TikiClient.configure method to add a configuration."
        ) else if (userID.isNotEmpty()){
            this.userID = userID
        } else throw Exception(
            "User ID cannot be empty. Use the TikiClient.initialize method to set the user ID."
        )
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
}
