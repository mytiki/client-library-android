package com.mytiki.publish.client.auth

import android.content.Context
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.email.EmailResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.EMPTY_REQUEST
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.security.PublicKey
import java.util.Date


class AuthService {

    val authRepository = AuthRepository()

    /**
     * Authenticates with TIKI and saves the auth and refresh tokens.
     *
     * @param publishingId
     * @param userId
     * @return The authentication token.
     */
    fun authenticate(publishingId: String, userId: String): String{
        return ""
    }

    /**
     * Retrieves the authentication token, refreshing if necessary.
     * @return The authentication token.
     */
    fun token(context: Context, email: String, providerID: String, publicKey: String): CompletableDeferred<String> {
        val token = CompletableDeferred<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
            val body = FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", "provider:$providerID")
                .add("client_secret", publicKey)
                .add("scope", "account:provider trail publish")
                .add("expires", "600")
                .build();

            val request = Request.Builder()
                .url("https://account.mytiki.com/api/latest/auth/token")
                .addHeader("accept", "application/json")
                .post(body)
                .build()

            val apiResponse = client.newCall(request).execute()
            if (apiResponse.code in 200..299) {
                token.complete(TikiTokenResponse.fromJson(JSONObject(apiResponse.body?.string()!!)).access_token)
            } else token.completeExceptionally(Exception("error on user info request"))
        }
       return token
    }

    /**
     * Revokes the authentication token.
     */
    fun revoke(){}

    /**
     * Refreshes the authentication token.
     * @return The updated authentication token.
     */
    fun refresh(context: Context, email: String, clientID: String): CompletableDeferred<AuthToken>{
        val refreshResponse = CompletableDeferred<AuthToken>()
        val authToken = authRepository.get(context, email)
        if (authToken != null){
            CoroutineScope(Dispatchers.IO).launch {
                val client = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()

                val request = Request.Builder()
                    .url(authToken.provider.refreshTokenEndpoint(authToken.refresh, clientID))
                    .post(EMPTY_REQUEST)
                    .build()
                val apiResponse = client.newCall(request).execute()

                if (apiResponse.code in 200..299) {
                    val json = JSONObject(apiResponse.body?.string()!!)
                    val refreshAuthToken = AuthToken(
                        email,
                        json.getString("access_token"),
                        authToken.refresh,
                        Date(authToken.expiration.time + json.getLong("expires_in")),
                        authToken.provider
                    )
                    authRepository.save(context, refreshAuthToken)
                    refreshResponse.complete(refreshAuthToken)
                } else throw Exception("error on generate refresh token")
            }
            return refreshResponse
        } else throw Exception("unable to retrieve refresh token")
    }
}
