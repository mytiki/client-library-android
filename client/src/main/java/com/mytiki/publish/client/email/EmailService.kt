package com.mytiki.publish.client.email

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.auth.AuthRepository
import com.mytiki.publish.client.email.messageResponse.Message
import com.mytiki.publish.client.email.messageResponse.MessageResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject

class EmailService() {
    var googleKeys: EmailKeys? = null
    var outlookKeys: EmailKeys? = null

    var loginCallback: () -> Unit = {}
        private set

    /**
     * Authenticates with OAuth and adds an email account for scraping receipts.
     * @param provider The email provider (GOOGLE or OUTLOOK).
     */
    fun login(context: Context, provider: EmailProviderEnum, emailKeys: EmailKeys, redirectURI: String, loginCallback: () -> Unit){
        this.loginCallback = loginCallback
        val intent = Intent(context, EmailActivity::class.java)

        intent.putExtra("provider", provider.toString())
        intent.putExtra("clientID", emailKeys.clientId)
        intent.putExtra("clientSecret", emailKeys.clientSecret)
        intent.putExtra("redirectURI", redirectURI)

        context.startActivity(intent)
    }

    fun authRequest(context: Context, provider: EmailProviderEnum,  clientID: String, redirectURI: String): Pair<Intent?,  AuthorizationService> {
        val authServiceConfig = AuthorizationServiceConfiguration(
            Uri.parse(provider.authorizationEndpoint),
            Uri.parse(provider.tokenEndpoint)
        )
        val authRequest = AuthorizationRequest.Builder(
            authServiceConfig,
            clientID,
            ResponseTypeValues.CODE,
            Uri.parse(redirectURI)
        )
        authRequest.setScope(provider.scopes)
        val authService = AuthorizationService(context)
        return Pair(authService.getAuthorizationRequestIntent(authRequest.build()), authService)
    }

    fun getEmailResponse(provider: EmailProviderEnum, auth: String): CompletableDeferred<EmailResponse> {
        val emailResponse = CompletableDeferred<EmailResponse>()
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
            val request = Request.Builder()
                .url(provider.userInfoEndpoint)
                .addHeader("Authorization", "Bearer $auth")
                .get()
                .build()
            val apiResponse = client.newCall(request).execute()

            if (apiResponse.code in 200..299) {
                emailResponse.complete(EmailResponse.fromJson(JSONObject(apiResponse.body?.string()!!)))
            } else throw Exception("error on user info request")
        }
        return emailResponse
    }

    /**
     * Retrieves the list of connected email accountsPerProvider.
     * @return List of connected email accountsPerProvider.
     */
    fun accountsPerProvider(context: Context, emailProvider: EmailProviderEnum): List<String> {
        return TikiClient.auth.authRepository.accounts(context, emailProvider)
    }

    fun messages(context: Context, auth: String, provider: EmailProviderEnum, email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val isWorking = mutableStateOf(true)
            val endpoint = mutableStateOf(provider.messagesListEndpoint(email))
            while (isWorking.value) {
                val client = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
                val request = Request.Builder()
                    .url(endpoint.value)
                    .addHeader("Authorization", "Bearer $auth")
                    .get()
                    .build()
                val apiResponse = client.newCall(request).execute()

                if (apiResponse.code in 200..299) {
                    val resp = MessageResponse.fromJson(JSONObject(apiResponse.body?.string()!!))
                    if (!resp.messages.isNullOrEmpty()) {
                        EmailRepository.save(context, email, resp.messages)
                        if (!resp.nextPageToken.isNullOrEmpty()) {
                            endpoint.value =
                                provider.messagesListEndpoint(email) + "&pageToken=${resp.nextPageToken}"
                        } else {
                            isWorking.value = false
                        }
                    }
                } else throw Exception("error on getting messages")
            }
        }
    }



    /**
     * Removes a previously added email account.
     * @param email The email account to be removed.
     */
    fun logout(context: Context, email: String){
        TikiClient.auth.authRepository.remove(context, email)
    }
    fun googleKeys( clientId: String, clientSecrete: String) {
        googleKeys = EmailKeys(clientId, clientSecrete)

    }

    fun outlookKeys( clientId: String, clientSecrete: String) {
        outlookKeys = EmailKeys(clientId, clientSecrete)
    }
}