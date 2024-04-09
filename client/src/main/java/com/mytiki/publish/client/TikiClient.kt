package com.mytiki.publish.client

import android.content.Context
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
     *
     * This property is a getter that returns an instance of AuthService. It checks if the client is
     * properly configured and the user ID is set before returning the AuthService instance.
     *
     * @throws Exception if the client is not configured or the user ID is not set.
     * @return An instance of AuthService.
     */
    val auth = AuthService()
        get() {
            check()
            return field
        }

    /**
     * CaptureService instance for handling receipt capture.
     *
     * This property is a getter that returns an instance of CaptureService. It checks if the client is
     * properly configured and the user ID is set before returning the CaptureService instance.
     *
     * @throws Exception if the client is not configured or the user ID is not set.
     * @return An instance of CaptureService.
     */
    val capture = CaptureService()
        get() {
            check()
            return field
        }

    /**
     * LicenseService instance for managing licensing.
     *
     * This property is a getter that returns an instance of LicenseService. It checks if the client is
     * properly configured and the user ID is set before returning the LicenseService instance.
     *
     * @throws Exception if the client is not configured or the user ID is not set.
     * @return An instance of LicenseService.
     */
    val license = LicenseService()
        get() {
            check()
            return field
        }

    /**
     * User ID for the client.
     *
     * This property is a lateinit variable that holds the user ID for the client. It is private and
     * can only be set within the TikiClient object.
     */
    lateinit var userID: String
        private set

    /**
     * Configuration for the client.
     *
     * This property is a lateinit variable that holds the configuration for the client. It is private
     * and can only be set within the TikiClient object.
     */
    lateinit var config: Config
        private set

    /**
     * Configures the TikiClient with the provided configuration.
     *
     * This function is used to set the configuration for the TikiClient. The configuration includes
     * all the necessary parameters and settings that the TikiClient needs to operate correctly.
     *
     * @param config The Config object that contains the configuration settings for the TikiClient.
     */
    fun configure(config: Config){
        this.config = config
    }

    /**
     * Initializes the TikiClient with the provided user ID.
     *
     * This function is used to set the user ID for the TikiClient. The user ID is a unique identifier
     * for the user and is required for the TikiClient to operate correctly. The function checks if
     * the TikiClient is configured and if the user ID is not empty before setting the user ID.
     *
     * @param userID The unique identifier for the user.
     * @throws Exception if the TikiClient is not configured or the user ID is empty.
     * @return A CompletableDeferred object that completes when the TikiClient is initialized. The
     * CompletableDeferred object does not contain any value and is only used to signal that the
     * initialization process is complete.
     */
    fun initialize(userID: String):CompletableDeferred<Unit>{
        if (!this::config.isInitialized) throw Exception(
            "TIKI Client is not configured. Use the TikiClient.configure method to add a configuration."
        ) else if (userID.isNotEmpty()){
            val isInitialized = CompletableDeferred<Unit>()
            MainScope().async {
                this@TikiClient.userID = userID
                auth.registerAddress().await()
            }
            return isInitialized
        } else throw Exception(
            "User ID cannot be empty. Use the TikiClient.initialize method to set the user ID."
        )
    }

    /**
     * Initiates the process of scanning a physical receipt and returns the receipt ID.
     *
     * This function initiates the process of scanning a physical receipt. It uses the provided
     * ComponentActivity instance to start the scanning process. The result of the scanning process
     * is returned through the provided callback function.
     *
     * @param activity The ComponentActivity instance. This is typically the current activity from
     * which this function is called. It is used to provide context for the scanning process.
     * @param scanCallback The callback function that will be called with the scanned receipt data
     * when the scanning process is finished.
     */
    fun scan(activity: ComponentActivity, scanCallback: (Bitmap) -> Unit){
        capture.scan(activity, scanCallback)
    }

    /**
     * Publishes a single bitmap image for receipt data extraction.
     *
     * This function publishes a single bitmap image for receipt data extraction. The provided bitmap
     * image data is sent to the capture service for processing. The function is asynchronous and
     * returns a CompletableDeferred object that will be completed when the data has been published.
     *
     * @param data The bitmap image data to be published.
     * @return A CompletableDeferred object that will be completed when the data has been published.
     */
    fun publish(data: Bitmap): CompletableDeferred<Unit> {
        return capture.publish(data)
    }

    /**
     * Publishes an array of bitmap images for receipt data extraction.
     *
     * This function publishes an array of bitmap images for receipt data extraction. The provided
     * array of bitmap image data is sent to the capture service for processing. The function is
     * asynchronous and returns a CompletableDeferred object that will be completed when all the data
     * has been published.
     *
     * @param data The array of bitmap image data to be published.
     * @return A CompletableDeferred object that will be completed when all the data has been published.
     */
    fun publish(data: Array<Bitmap>): CompletableDeferred<Unit> {
        return capture.publish(data)
    }

    /**
     * Creates a license for the user.
     *
     * This function is asynchronous and returns a CompletableDeferred object that will be completed
     * when the license creation process is finished. The result of the license creation process
     * is a Boolean value indicating whether the license was successfully created or not.
     *
     * @param activity The ComponentActivity instance. This is typically the current activity from
     * which this function is called. It is used to provide context for the license creation process.
     *
     * @return A CompletableDeferred object that will be completed with a Boolean value when the
     * license creation process is finished. The Boolean value indicates whether the license was
     * successfully created (true) or not (false).
     */
    fun createLicense(activity: ComponentActivity): CompletableDeferred<Boolean> {
        val license = CompletableDeferred<Boolean>()
        MainScope().async {
            val resp = this@TikiClient.license.create(activity)
            license.complete(resp)
        }
        return license
    }

    /**
     * Retrieves the terms of the license.
     *
     * This function retrieves the terms of the license from the LicenseService. It uses the provided
     * Context instance to get the resources necessary for retrieving the terms. The function is
     * synchronous and returns a String containing the terms of the license.
     *
     * @param context The Context instance. This is typically the current activity or application context
     * from which this function is called. It is used to provide context for retrieving the license terms.
     * @return A String containing the terms of the license.
     */
    fun terms(context: Context) = license.terms(context)

    /**
     * Checks if the client is properly configured and the user ID is set.
     *
     * This function checks if the client is properly configured and the user ID is set. It throws an
     * exception if the client is not configured or the user ID is not set.
     *
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
}
