package com.mytiki.publish.client.email

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.mytiki.publish.client.repository.EmailAccountRepository
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationServiceConfiguration


class EmailService() {
    val emailAccountRepository = EmailAccountRepository()
    lateinit var authState: AuthState

    /**
     * Authenticates with OAuth and adds an email account for scraping receipts.
     * @param provider The email provider (GOOGLE or OUTLOOK).
     */
    fun login(context: Context, provider: EmailProviderEnum, clientID: String, redirectURI: String, clientSecret: String = ""){
        val authServiceConfig = AuthorizationServiceConfiguration(
            Uri.parse(provider.authorizationEndpoint),  // authorization endpoint
            Uri.parse(provider.tokenEndpoint) // token endpoint
        )
        authState = AuthState(authServiceConfig)

        val intent = Intent(context, EmailActivity::class.java)

        intent.putExtra("provider", provider.toString())
        intent.putExtra("clientID", clientID)
        intent.putExtra("clientSecret", clientSecret)
        intent.putExtra("redirectURI", redirectURI)

        context.startActivity(intent)
    }

//    fun getEmailResponse(authToken: AuthToken, provider: EmailProviderEnum): CompletableDeferred<EmailResponse?> {
//        val emailResponse = CompletableDeferred<EmailResponse?>()
//       MainScope().async {  }
//            val client = OkHttpClient.Builder()
//                .addInterceptor(HttpLoggingInterceptor().apply {
//                    level = HttpLoggingInterceptor.Level.BODY
//                })
//                .build()
//            val request = Request.Builder()
//                .url(provider.userInfoEndpoint)
//                .addHeader("Authorization", "Bearer (${authToken.auth})")
//                .get()
//                .build()
//            val apiResponse = client.newCall(request).execute()
//            emailResponse.complete(
//                if (apiResponse.code in 200..299) {
//                    Gson().fromJson(
//                        apiResponse.body?.string(),
//                        EmailResponse::class.java
//                    )
//                } else null
//            )
//
//    }

    /**
     * Retrieves the list of connected email accounts.
     * @return List of connected email accounts.
     */
    fun accounts(): List<String>{
        return listOf()
    }

    /**
     * Removes a previously added email account.
     * @param email The email account to be removed.
     */
    fun logout(email: String){}
}