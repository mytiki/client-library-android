package com.mytiki.publish.client.email

import android.app.Activity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.auth.AuthToken
import com.mytiki.publish.client.auth.AuthProviderEnum
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.ClientSecretBasic

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
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(authResponse.accessTokenExpirationTime!!),
                                    ZoneId.systemDefault()),
                                AuthProviderEnum.fromString(provider.toString())
                                    ?: throw Exception("Invalid provider"))
                        val authResp =
                            TikiClient.auth.repository.save(this@EmailActivity, authToken)
                        val emailResp =
                            TikiClient.email.repository.saveData(
                                this@EmailActivity,
                                IndexData(emailResponse.email, null, null, false))
                        if (authResp && emailResp) {
                          TikiClient.email.loginCallback(authToken.username)
                        } else throw Exception("unable to save data")

                        this@EmailActivity.finish()
                      }
                    } else throw Exception("unable to get access token")
                  }
            } else throw Exception("unable to get access token")
          }
      startForResult.launch(authIntent)
    }
  }
}
