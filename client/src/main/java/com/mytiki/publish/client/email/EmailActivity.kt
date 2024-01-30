package com.mytiki.publish.client.email

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.auth.AuthToken
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.ResponseTypeValues
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.Date


const val REQUEST_AUTH = 876357434
class EmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        if (extras != null) {
            val provider = extras.getString("provider")?.let { EmailProviderEnum.fromString(it) }
            val clientID = extras.getString("clientID")
            val clientSecret = extras.getString("clientSecret")
            val redirectURI = extras.getString("redirectURI")


            if (TikiClient.email.authState.authorizationServiceConfiguration != null && clientID != null) {
                val authRequest = AuthorizationRequest.Builder(
                    TikiClient.email.authState.authorizationServiceConfiguration!!,
                    clientID,
                    ResponseTypeValues.CODE,
                    Uri.parse(redirectURI)
                )
                authRequest.setScope("openid email profile")

                val authService = AuthorizationService(this)
                val authIntent = authService.getAuthorizationRequestIntent(authRequest.build())


                val startForResult = registerForActivityResult(
                    ActivityResultContracts.StartActivityForResult()
                ) { result: ActivityResult ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val authorizationResponse = AuthorizationResponse.fromIntent(result.data!!)
                        val error = AuthorizationException.fromIntent(result.data!!)
                        if (authorizationResponse != null) {
                            authService.performTokenRequest(
                                authorizationResponse.createTokenExchangeRequest()
                            ) { authResponse, ex ->
                                if (authResponse != null) {
                                    val authToken = AuthToken(
                                        authResponse.accessToken!!,
                                        authResponse.refreshToken!!,
                                        Date(authResponse.accessTokenExpirationTime!!)
                                    )
                                    TikiClient.email.authState.update(authResponse, ex)
                                    lifecycleScope.launch {
                                       val client = OkHttpClient.Builder()
                                           .addInterceptor(HttpLoggingInterceptor().apply {
                                               level = HttpLoggingInterceptor.Level.BODY
                                           })
                                           .build()
                                       val request = Request.Builder()
                                           .url(provider?.userInfoEndpoint!!)
                                           .addHeader("Authorization", "Bearer (${authToken.auth})")
                                           .get()
                                           .build()
                                       val apiResponse = client.newCall(request).execute()
                                       if (apiResponse.code in 200..299) {
                                           val gson = Gson()
                                           val data = gson.fromJson(
                                               apiResponse.body?.string(),
                                               EmailResponse::class.java
                                           )

                                           TikiClient.email.emailAccountRepository.saveToken(
                                               this@EmailActivity,
                                               data.email,
                                               authToken
                                           )
                                           this@EmailActivity.finish()

                                       } else {
                                           this@EmailActivity.finish()
                                       }
                                   }
                            }
                            }
                        }
                    }
                }
                startForResult.launch(authIntent)
            }

        }

    }

}