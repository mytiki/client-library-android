package com.mytiki.publish.client

import android.bluetooth.le.ScanCallback
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import com.mytiki.publish.client.auth.AuthService
import com.mytiki.publish.client.capture.CaptureService
import com.mytiki.publish.client.config.Config
import com.mytiki.publish.client.license.LicenseService
import kotlinx.coroutines.CompletableDeferred
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
     * LicenseService instance for managing licensing.
     */
    val license = LicenseService()
        get() {
            check()
            return field
        }

    // User ID for the client
    lateinit var userID: String
        private set
    // Configuration for the client
    lateinit var config: Config
        private set

    /**
     * Checks if the client is properly configured and the user ID is set.
     * @throws Exception if the client is not configured or the user ID is not set.
     * @return true if the client is properly configured and the user ID is set.
     */
    private fun check(): Boolean {
        if (!this::config.isInitialized) throw Exception(
            "TIKI Client is not configured. Use the TikiClient.configure method to add a configuration."
        ) else if (userID.isEmpty() || !this::userID.isInitialized) throw Exception(
            "User ID is not set. Use the TikiClient.initialize method to set the user ID."
        ) else return true
    }

    /**
     * Configures the client with the provided configuration.
     * @param config The configuration to set.
     */
    fun configure(config: Config){
        this.config = config
    }

    /**
     * Initializes the client with the provided user ID.
     * @param userID The user ID to set.
     * @throws Exception if the client is not configured or the user ID is empty.
     * @return A CompletableDeferred that completes when the client is initialized.
     */
    fun initialize(userID: String):CompletableDeferred<Unit>{
        if (!this::config.isInitialized) throw Exception(
            "TIKI Client is not configured. Use the TikiClient.configure method to add a configuration."
        ) else if (userID.isNotEmpty()){
            val isInitialized = CompletableDeferred<Unit>()
            MainScope().async {
                this@TikiClient.userID = userID
                auth.registerAddress().await()
                isInitialized.complete(Unit)
            }
            return isInitialized
        } else throw Exception(
            "User ID cannot be empty. Use the TikiClient.initialize method to set the user ID."
        )
    }

    /**
     * Initiates the process of scanning a physical receipt and returns the receipt ID.
     * @param activity The ComponentActivity instance.
     * @return The scanned receipt data or an empty string if the scan is unsuccessful.
     */
    fun scan(activity: ComponentActivity, scanCallback: (Bitmap) -> Unit){
        capture.camera(activity, scanCallback)
    }

     /**
     * Publishes a single bitmap image for receipt data extraction.
     * @param data The bitmap image data.
     * @return A CompletableDeferred object that will resolve when the data has been published.
     */
    fun publish(data: Bitmap): CompletableDeferred<Unit> {
        return capture.publish(data)
    }

    /**
     * Publishes an array of bitmap images for receipt data extraction.
     * @param data The array of bitmap image data.
     * @return A CompletableDeferred object that will resolve when all the data has been published.
     */
    fun publish(data: Array<Bitmap>): CompletableDeferred<Unit> {
        return capture.publish(data)
    }
}
