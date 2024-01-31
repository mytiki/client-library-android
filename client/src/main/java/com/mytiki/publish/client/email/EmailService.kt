package com.mytiki.publish.client.email

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.mytiki.publish.client.auth.AuthToken
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject


class EmailService() {
    val emailRepository = EmailRepository()
    /**
     * Authenticates with OAuth and adds an email account for scraping receipts.
     * @param provider The email provider (GOOGLE or OUTLOOK).
     */
    fun login(context: Context, provider: EmailProviderEnum, clientID: String, redirectURI: String, clientSecret: String = ""){
        val intent = Intent(context, EmailActivity::class.java)

        intent.putExtra("provider", provider.toString())
        intent.putExtra("clientID", clientID)
        intent.putExtra("clientSecret", clientSecret)
        intent.putExtra("redirectURI", redirectURI)

        context.startActivity(intent)
    }

    fun getEmailResponse(authToken: AuthToken, provider: EmailProviderEnum): CompletableDeferred<EmailResponse> {
        val emailResponse = CompletableDeferred<EmailResponse>()
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
            val request = Request.Builder()
                .url(provider.userInfoEndpoint)
                .addHeader("Authorization", "Bearer ${authToken.auth}")
                .get()
                .build()
            val apiResponse = client.newCall(request).execute()

            if (apiResponse.code in 200..299) {
                emailResponse.complete(EmailResponse.fromJson(JSONObject(apiResponse.body?.string()!!)))
            } else throw Exception("error on user info request")
        }
        return emailResponse
    }

    fun authRequest(context: Context, provider: EmailProviderEnum, clientID: String, redirectURI: String): Pair<Intent?,  AuthorizationService> {
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
        authRequest.setScope("openid email profile")

        val authService = AuthorizationService(context)
        return Pair(authService.getAuthorizationRequestIntent(authRequest.build()), authService)
    }

    /**
     * Retrieves the list of connected email accounts.
     * @return List of connected email accounts.
     */
    fun accounts(context: Context): Set<String>{
        return emailRepository.accounts(context)
    }

    /**
     * Removes a previously added email account.
     * @param email The email account to be removed.
     */
    fun logout(context: Context, email: String){
        emailRepository.remove(context, email)
    }
}