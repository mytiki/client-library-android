package com.mytiki.publish.client.example_app

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.config.Config
import com.mytiki.publish.client.email.EmailKeys
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.example_app.theme.TikiClientTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class MainActivity : AppCompatActivity() {
    @OptIn(ExperimentalEncodingApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = Config(
            providerId = "fd861faf-aab9-4fa8-9c31-8160d98c74f0",
            publicKey = "MIIBCgKCAQEA09WSEv5TMj4k/dYNdq74t2BPrFcq+jTVyfFy142Abik+ucscJ0mPunUVGBFrXK+vNnxVsklNRA5p6hsMNNYPL+WyhXG4VdXMiAaQVSR5fUZ8voTrGrPSAk5v9Cshagk7CcSKLDtyHYtPAziRvbDtKC7yB7evcdiCzN+7kDUw0L3me89pz1o4rb7dllP6PtcZE9koHxje6EUB31pT+nXz/fqzIf5dCkfM19H1pqW6QZmvjRuQjKJijEXmBwUtrJXEw2fcWICktGhGyzAOado+oXaNzSVvIgNN7FVtd8JqjWu+K0xrW7V+h/Y8tF217yJtlE41T7WPABoikRQ+PYYoqQIDAQAB",
            companyName = "MyTiki",
            companyJurisdiction = "US",
            tosUrl = "https://mytiki.com",
            privacyUrl = "https://mytiki.com"
        )
        TikiClient.configure(config)
        TikiClient.initialize("test@gmail.com")


        setContent {
            var loginOutput by remember {
                mutableStateOf("")
            }

            fun onError(error: Throwable) {
                error.message?.let { loginOutput = it }
            }

            TikiClientTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = "Tiki Example",
                        )

                        Spacer(modifier = Modifier.height(60.dp))
                        MainButton(text = "Login") {
                            TikiClient.login(
                                this@MainActivity,
                                EmailProviderEnum.GOOGLE,
                                EmailKeys("1079849396355-q687vpf16ovveafo6robcgi1kaoaem3e.apps.googleusercontent.com", ""),
                                "com.googleusercontent.apps.1079849396355-q687vpf16ovveafo6robcgi1kaoaem3e:/oauth2redirect"
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Logout") {
                            TikiClient.logout(
                                this@MainActivity,
                                "gabrielschuler3@gmail.com",
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Get Token") {
                            val tokenList = TikiClient.auth.authRepository.get(this@MainActivity, "gabrielschuler3@gmail.com")
                            loginOutput = tokenList.toString()
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Refresh Token") {
                            val token = TikiClient.auth.emailAuthRefresh(
                                this@MainActivity,
                                "gabrielschuler3@gmail.com",
                                "1079849396355-q687vpf16ovveafo6robcgi1kaoaem3e.apps.googleusercontent.com"
                            )
                            loginOutput = token.toString()
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Messages") {
                            TikiClient.scrape(
                                this@MainActivity,
                                "gabrielschuler3@gmail.com",
                                "1079849396355-q687vpf16ovveafo6robcgi1kaoaem3e.apps.googleusercontent.com"
                            )
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Register Address") {
                            CoroutineScope(Dispatchers.IO).launch {
                                val resp =  TikiClient.auth.registerAddress().await()

                                loginOutput = resp.address
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Get TikiToken") {
                            CoroutineScope(Dispatchers.IO).launch {
                                val resp =  TikiClient.auth.token().await()
                                loginOutput = resp
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Get Tiki key") {
                            CoroutineScope(Dispatchers.IO).launch {
                                val keyPair = TikiClient.auth.getKey()
                                loginOutput = keyPair?.public?.encoded?.let { Base64.Default.encode(it) }.toString()
                                Log.d("**** keyPair ****", loginOutput)
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Get address") {
                            CoroutineScope(Dispatchers.IO).launch {
                                val keyPair = TikiClient.auth.getKey()
                                val address = keyPair?.let { TikiClient.auth.address(it) }
                                loginOutput = address.toString()
                                Log.d("**** address ****", address.toString())
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Camera") {
                            TikiClient.capture.camera(this@MainActivity)
                        }
                    }
                }
            }
        }
    }
}
