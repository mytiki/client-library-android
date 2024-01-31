package com.mytiki.publish.client.email

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.auth.AuthToken
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import java.util.Date


const val REQUEST_AUTH = 876357434
class EmailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        if (extras != null) {
            val provider = extras.getString("provider")?.let { EmailProviderEnum.fromString(it) } ?: throw Exception("provider not identified")
            val clientID = extras.getString("clientID") ?: throw Exception("clientID not identified")
            val redirectURI = extras.getString("redirectURI") ?: throw Exception("redirectURI not identified")
            val (authIntent, authService) = TikiClient.email.authRequest(this@EmailActivity, provider, clientID, redirectURI)


            val startForResult = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val authorizationResponse = AuthorizationResponse.fromIntent(result.data!!) ?: throw Exception("error on AuthorizationResponse")
                    authService.performTokenRequest(
                        authorizationResponse.createTokenExchangeRequest()
                    ) { authResponse, _->
                        if (authResponse != null) {
                            val authToken = AuthToken(
                                authResponse.accessToken!!,
                                authResponse.refreshToken!!,
                                Date(authResponse.accessTokenExpirationTime!!),
                                provider
                            )
                            MainScope().async {
                               val emailResponse = TikiClient.email.getEmailResponse(authToken).await()
                               TikiClient.email.emailRepository.save(
                                   this@EmailActivity,
                                   emailResponse.email,
                                   authToken
                               )
                           }
                            this@EmailActivity.finish()
                        } else throw Exception("unable to get access token")
                    }
                }
            }
            startForResult.launch(authIntent)
        }
    }
}