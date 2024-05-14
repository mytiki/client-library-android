package com.mytiki.publish.client

import android.content.Context
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import com.mytiki.publish.client.auth.AuthService
import com.mytiki.publish.client.capture.CaptureService
import com.mytiki.publish.client.capture.ReceiptResponse
import com.mytiki.publish.client.config.Config
import com.mytiki.publish.client.email.Attachment
import com.mytiki.publish.client.email.EmailKeys
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.email.EmailService
import com.mytiki.publish.client.license.LicenseService
import com.mytiki.publish.client.offer.*
import com.mytiki.publish.client.permission.Permission
import com.mytiki.publish.client.permission.PermissionService
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
   * @return An instance of AuthService.
   * @throws Exception if the client is not configured or the user ID is not set.
   */
  val auth = AuthService()
    get() {
      check()
      return field
    }

  /**
   * CaptureService instance for handling receipt capture.
   *
   * This property is a getter that returns an instance of CaptureService. It checks if the client
   * is properly configured and the user ID is set before returning the CaptureService instance.
   *
   * @return An instance of CaptureService.
   * @throws Exception if the client is not configured or the user ID is not set.
   */
  val capture = CaptureService()
    get() {
      check()
      return field
    }

  /**
   * PermissionService instance for handling permissions.
   *
   * This property is a getter that returns an instance of PermissionService. It checks if the
   * client is properly configured and the user ID is set before returning the PermissionService
   * instance.
   *
   * @return An instance of PermissionService.
   * @throws Exception if the client is not configured or the user ID is not set.
   */
  val permission = PermissionService()
    get() {
      check()
      return field
    }

  /**
   * LicenseService instance for managing licensing.
   *
   * This property is a getter that returns an instance of LicenseService. It checks if the client
   * is properly configured and the user ID is set before returning the LicenseService instance.
   *
   * @return An instance of LicenseService.
   * @throws Exception if the client is not configured or the user ID is not set.
   */
  val license = LicenseService()
    get() {
      check()
      return field
    }

  /**
   * EmailService instance for handling email operations.
   *
   * This property is a getter that returns an instance of EmailService. It checks if the client is
   * properly configured and the user ID is set before returning the EmailService instance.
   *
   * @return An instance of EmailService.
   * @throws Exception if the client is not configured or the user ID is not set.
   */
  val email = EmailService()
    get() {
      checkEmail()
      return field
    }

  /**
   * User ID for the client.
   *
   * This property is a lateinit variable that holds the user ID for the client. It is private and
   * can only be set within the TikiClient object.
   */
  var userID: String? = null
    internal set

  /**
   * Configuration for the client.
   *
   * This property is a lateinit variable that holds the configuration for the client. It is private
   * and can only be set within the TikiClient object.
   */
  var config: Config? = null
    internal set

  /**
   * Email keys for the client.
   *
   * This property is a lateinit variable that holds the email keys for the client. It is private
   * and can only be set within the TikiClient object.
   */
  var emailKeys: EmailKeys? = null
    internal set

  /**
   * Configures the TikiClient with the provided configuration.
   *
   * This function is used to set the configuration for the TikiClient. The configuration includes
   * all the necessary parameters and settings that the TikiClient needs to operate correctly.
   *
   * @param config The Config object that contains the configuration settings for the TikiClient.
   */
  fun configure(config: Config) {
    if (config.tosUrl.isEmpty())
        throw Exception(
            "tosUrl property cannot be empty. Use the TikiClient.configure method to add a configuration.")
    else if (config.privacyUrl.isEmpty())
        throw Exception(
            "privacyUrl property cannot be empty. Use the TikiClient.configure method to add a configuration.")
    else if (config.companyJurisdiction.isEmpty())
        throw Exception(
            "companyJurisdiction property cannot be empty. Use the TikiClient.configure method to add a configuration.")
    else if (config.companyName.isEmpty())
        throw Exception(
            "companyName property cannot be empty. Use the TikiClient.configure method to add a configuration.")
    else if (config.publicKey.isEmpty())
        throw Exception(
            "publicKey property cannot be empty. Use the TikiClient.configure method to add a configuration.")
    else if (config.providerId.isEmpty())
        throw Exception(
            "providerId property cannot be empty. Use the TikiClient.configure method to add a configuration.")
    else {
      this.config = config
    }
  }

  /**
   * Configures the email keys for the TikiClient.
   *
   * This function is used to set the email keys for the TikiClient. The email keys include the
   * client ID, client secret, and redirect URI.
   *
   * @param clientID The client ID for the email service.
   * @param clientSecret The client secret for the email service.
   * @param redirectURI The redirect URI for the email service.
   */
  fun emailConfig(clientID: String, clientSecret: String, redirectURI: String) {
    if (clientID.isEmpty())
        throw Exception(
            "clientID property cannot be empty. Use the TikiClient.emailConfig method to add a emailConfig.")
    else if (redirectURI.isEmpty())
        throw Exception(
            "redirectURI property cannot be empty. Use the TikiClient.emailConfig method to add a redirectURI.")
    else {
      this.emailKeys = EmailKeys(clientID, clientSecret, redirectURI)
    }
  }

  /**
   * Initializes the TikiClient with the provided user ID.
   *
   * This function is used to set the user ID for the TikiClient. The user ID is a unique identifier
   * for the user and is required for the TikiClient to operate correctly. The function checks if
   * the TikiClient is configured and if the user ID is not empty before setting the user ID.
   *
   * @param userID The unique identifier for the user.
   * @return A CompletableDeferred object that completes when the TikiClient is initialized. The
   *   CompletableDeferred object does not contain any value and is only used to signal that the
   *   initialization process is complete.
   * @throws Exception if the TikiClient is not configured or the user ID is empty.
   */
  fun initialize(userID: String): CompletableDeferred<Unit> {
    if (config == null)
        throw Exception(
            "TIKI Client is not configured. Use the TikiClient.configure method to add a configuration.")
    else if (userID.isEmpty())
        throw Exception(
            "User ID cannot be empty. Use the TikiClient.initialize method to set the user ID.")
    else {
      val isInitialized = CompletableDeferred<Unit>()
      MainScope().async {
        this@TikiClient.userID = userID
        auth.registerAddress().await()
      }
      return isInitialized
    }
  }

  /**
   * Initiates the process of scanning a physical receipt and returns the receipt ID.
   *
   * This function initiates the process of scanning a physical receipt. It uses the provided
   * ComponentActivity instance to start the scanning process. The result of the scanning process is
   * returned through the provided callback function.
   *
   * @param activity The ComponentActivity instance. This is typically the current activity from
   *   which this function is called. It is used to provide context for the scanning process.
   * @param scanCallback The callback function that will be called with the scanned receipt data
   *   when the scanning process is finished.
   */
  fun scan(activity: ComponentActivity, scanCallback: (Bitmap) -> Unit) {
    capture.scan(activity, scanCallback)
  }

  /**
   * Publishes an array of attachments for receipt data extraction.
   *
   * This function publishes an array of attachments for receipt data extraction. The provided
   * attachments are sent to the capture service for processing. The function is asynchronous and
   * returns a CompletableDeferred object that will be completed when the data has been published.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for the publishing
   *   process.
   * @param attachments The array of Attachment objects to be published.
   * @return A CompletableDeferred object that will be completed when the data has been published.
   */
  fun publish(context: Context, attachments: Array<Attachment>): CompletableDeferred<Unit> {
    return capture.publish(context, attachments)
  }

  /**
   * Retrieve the structured data extracted from the processed receipt image.
   *
   * This method fetches the result of the receipt image processing from the server.
   *
   * @param receiptId The unique identifier for the receipt obtained from the publish method.
   * @param onResult A callback functions that receives the array of ReceiptResponse objects, each
   *   containing the structured data extracted from an image of the receipt, or null if the
   *   retrieval fails.
   */
  suspend fun receipt(receiptId: String, onResult: (Array<ReceiptResponse?>) -> Unit) {
    val token = auth.addressToken().await()
    return capture.receipt(receiptId, token, onResult)
  }

  /**
   * Requests the specified permissions.
   *
   * This function uses the PermissionService to request the specified permissions. The result of
   * the permissions request is returned through the provided callback function.
   *
   * @param activity The ComponentActivity instance. This is typically the current activity from
   *   which this function is called. It is used to provide context for the permissions request.
   * @param permissions The list of permissions to request.
   * @param permissionCallback The callback function that will be called with the result of the
   *   permissions request when it is finished. The result is a map where the keys are the requested
   *   permissions and the values are Booleans indicating whether each permission was granted.
   */
  fun permissions(
      activity: ComponentActivity,
      permissions: List<Permission>,
      permissionCallback: (Map<Permission, Boolean>) -> Unit
  ) {
    permission.requestPermissions(activity, permissions, permissionCallback)
  }

  /**
   * Checks if a specific permission is authorized.
   *
   * This function uses the PermissionService to check if a specific permission is authorized. It
   * returns a Boolean indicating whether the permission is granted.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for the permission
   *   check.
   * @param permission The Permission instance representing the permission to check.
   * @return A Boolean indicating whether the permission is granted.
   */
  fun isPermissionAuthorized(context: Context, permission: Permission): Boolean {
    return this.permission.isAuthorized(context, permission)
  }

  /**
   * Initiates the process of creating a license.
   *
   * This function initiates the process of creating a license. It uses the provided Context
   * instance to start the license creation process. The function is a suspending function, meaning
   * it can be paused and resumed, allowing it to perform long running operations like network
   * requests without blocking the main thread.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for the license
   *   creation process.
   * @return The result of the license creation process.
   */
  fun createLicense(context: Context): CompletableDeferred<Boolean> {
    val isLicenseCreated = CompletableDeferred<Boolean>()
    MainScope().async {
      val license =
          license.create(
              context,
              Use(listOf(Usecase(UsecaseCommon.ATTRIBUTION)), listOf("*")),
              arrayOf(Tag(TagCommon.PURCHASE_HISTORY)))
      isLicenseCreated.complete(license)
    }
    return isLicenseCreated
  }

  /**
   * Retrieves the terms of the license.
   *
   * This function retrieves the terms of the license from the LicenseService. It uses the provided
   * Context instance to get the resources necessary for retrieving the terms. The function is
   * synchronous and returns a String containing the terms of the license.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for retrieving the
   *   license terms.
   * @return A String containing the terms of the license.
   */
  fun terms(context: Context) = license.terms(context)

  /**
   * Initiates the login process for a user.
   *
   * This function initiates the login process for a user. It uses the provided Context instance,
   * EmailProviderEnum instance, and a callback function. The function checks if the email keys are
   * set before starting the login process.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for the login
   *   process.
   * @param provider The EmailProviderEnum instance representing the email provider to use for the
   *   login process.
   * @param loginCallback The callback function that will be called with the result of the login
   *   process. The result is a string representing the login status.
   */
  fun login(context: Context, provider: EmailProviderEnum, loginCallback: (String) -> Unit) {
    checkEmail()
    email.login(context, provider, emailKeys!!, loginCallback)
  }

  /**
   * Initiates the logout process for a user.
   *
   * This function initiates the logout process for a user. It uses the provided Context instance
   * and a string representing the email of the user. The function checks if the email keys are set
   * before starting the logout process.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for the logout
   *   process.
   * @param email The string representing the email of the user to logout.
   */
  fun logout(context: Context, email: String) {
    checkEmail()
    this.email.logout(context, email)
  }

  /**
   * Retrieves the list of accounts for a user.
   *
   * This function retrieves the list of accounts for a user. It uses the provided Context instance.
   * The function checks if the email keys are set before retrieving the accounts.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for retrieving the
   *   accounts.
   * @return A list of strings representing the accounts of the user.
   */
  fun accounts(context: Context): List<String> {
    checkEmail()
    return email.accounts(context)
  }

  /**
   * Initiates the process of scraping emails for a user.
   *
   * This function initiates the process of scraping emails for a user. It uses the provided Context
   * instance and a string representing the email of the user. The function checks if the email keys
   * are set before starting the scraping process.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for the scraping
   *   process.
   * @param email The string representing the email of the user to scrape.
   */
  fun scrape(context: Context, email: String) {
    checkEmail()
    this.email.scrape(context, email)
  }

  /**
   * Checks if the client is properly configured and the user ID is set.
   *
   * This function checks if the client is properly configured and the user ID is set. It throws an
   * exception if the client is not configured or the user ID is not set.
   *
   * @return true if the client is properly configured and the user ID is set.
   * @throws Exception if the client is not configured or the user ID is not set.
   */
  private fun check(): Boolean {
    if (config == null)
        throw Exception(
            "TIKI Client is not configured. Use the TikiClient.configure method to add a configuration.")
    else if (userID.isNullOrEmpty())
        throw Exception(
            "User ID cannot be empty. Use the TikiClient.initialize method to set the user ID.")
    else return true
  }

  /**
   * Checks if the client is properly configured and the email keys are set.
   *
   * This function first calls the check() function to ensure that the client is properly configured
   * and the user ID is set. Then, it checks if the email keys are set. If the email keys are not
   * set, it throws an exception with a message instructing the user to use the
   * TikiClient.emailConfig method to add the clientID and clientSecret. If the email keys are set,
   * it returns true.
   *
   * @return true if the client is properly configured, the user ID is set, and the email keys are
   *   set.
   * @throws Exception if the client is not configured, the user ID is not set, or the email keys
   *   are not set.
   */
  private fun checkEmail(): Boolean {
    check()
    if (emailKeys == null)
        throw Exception(
            "Email is not configured. Use the TikiClient.emailConfig method to add clientID and clientSecret.")
    else return true
  }
}
