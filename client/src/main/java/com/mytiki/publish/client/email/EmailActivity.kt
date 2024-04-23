package com.mytiki.publish.client.email

import android.app.Activity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.auth.AuthToken
import com.mytiki.publish.client.auth.TokenProviderEnum
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.ClientSecretBasic
import java.util.*

const val REQUEST_AUTH = 876357434

class EmailActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val extras = intent.extras
    if (extras != null) {
      val provider =
          extras.getString("provider")?.let { EmailProviderEnum.fromString(it) }
              ?: throw Exception("provider not identified")
      val clientID = extras.getString("clientID") ?: throw Exception("clientID not identified")
      val clientSecret =
          extras.getString("clientSecret") ?: throw Exception("clientSecret not identified")
      val redirectURI =
          extras.getString("redirectURI") ?: throw Exception("redirectURI not identified")
      val (authIntent, authService) =
          TikiClient.auth.emailAuthRequest(this@EmailActivity, provider, clientID, redirectURI)

      val startForResult =
          registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
              result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
              val authorizationResponse =
                  AuthorizationResponse.fromIntent(result.data!!)
                      ?: throw Exception("error on AuthorizationResponse")
              authService.performTokenRequest(
                  authorizationResponse.createTokenExchangeRequest(),
                  ClientSecretBasic(clientSecret)) { authResponse, _ ->
                    if (authResponse != null) {
                      MainScope().async {
                        val emailResponse =
                            TikiClient.email
                                .getEmailResponse(provider, authResponse.accessToken!!)
                                .await()
                        val authToken =
                            AuthToken(
                                emailResponse.email,
                                authResponse.accessToken!!,
                                authResponse.refreshToken!!,
                                Date(authResponse.accessTokenExpirationTime!!),
                                TokenProviderEnum.fromString(provider.toString())
                                    ?: throw Exception("Invalid provider"))
                        val resp = TikiClient.auth.repository.save(this@EmailActivity, authToken)
                        if (resp) {
                          TikiClient.email.loginCallback(authToken.username)
                        }
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
