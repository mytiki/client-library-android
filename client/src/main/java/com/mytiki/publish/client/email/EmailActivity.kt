package com.mytiki.publish.client.email

import android.R.attr.data
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.mytiki.publish.client.R
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.auth.AuthToken
import com.mytiki.tiki_sdk_android.TikiSdk
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.util.Date


const val REQUEST_AUTH = 876357434
class EmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        if (extras != null) {
            val provider = EmailProviderEnum.fromString(extras.getString("provider")!!)
            val clientID = extras.getString("clientID")
            val clientSecret = extras.getString("clientSecret")
            val redirectURI = extras.getString("redirectURI")



            val authRequest = AuthorizationRequest.Builder(
                TikiClient.email.authState.authorizationServiceConfiguration!!,
                clientID!!,
                ResponseTypeValues.CODE,
                Uri.parse(redirectURI)
            )

            val authService = AuthorizationService(this)
            val authIntent = authService.getAuthorizationRequestIntent(authRequest.build())

            val startForResult = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val response = AuthorizationResponse.fromIntent(result.data!!)
                    val error = AuthorizationException.fromIntent(result.data!!)
                    if (response != null) {
                        TikiClient.email.authState.update(response, error)
                        val authToken = AuthToken(
                            response.accessToken!!,
                            response.idToken!!,
                            Date(response.accessTokenExpirationTime!!)
                        )

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
                        val response =  client.newCall(request).execute()
                        if(response.code in 200..299){
                            val gson = Gson()
                            val data = gson.fromJson(response.body?.string(), EmailResponse::class.java)
                            TikiClient.email.emailAccountRepository.saveToken(this@EmailActivity,data.email, authToken)
                            this@EmailActivity.finish()
                        }
                    }
                }
            }
            startForResult.launch(authIntent)
        }



    }

}