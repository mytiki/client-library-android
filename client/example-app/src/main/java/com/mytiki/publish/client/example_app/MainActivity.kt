package com.mytiki.publish.client.example_app

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
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
            providerId = "2b08b660-84cd-410c-9c92-836993e90c93",
            publicKey = "MIIBCgKCAQEAoHhIrvp0aY+VhRquH6dW3fgwg+n7QqSICVvjoWceSFnGiAGCmI661BQp8QYpTqdHgkaehWMgADtFTuaHJvbG88NY1Ah9wbKf2uGzu+uIaXTfBFojc/9hvwPe+U5bJ6O9rFcpoAhxqcR0qC8h17Q/fRIPNrNFm8u0pfK/kLRQzsnH7rrZYYOjFqzpazSdmKuSUxVoKOGlAddlqQpYH8oEdNmJKj7aWmMXgZMScaEd2JqQihgULXGTuA23iTaBFmmBXSp2iCLxLRLEAjh+zLQhNg00bk/jW3pU1A+36ktCAwOBa8vX6sl5xMO57+iGAJgvBVyA61U1t9QPVOdsjjjg9QIDAQAB",
            companyName = "gabriel",
            companyJurisdiction = "US",
            tosUrl = "https://mytiki.com",
            privacyUrl = "https://mytiki.com"
        )
        TikiClient.configure(config)
        TikiClient.initialize("gabriel@gmail.com")


        setContent {
            var loginOutput by remember {
                mutableStateOf("")
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
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = loginOutput,
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Register Address") {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val resp = TikiClient.auth.registerAddress().await()

                                    loginOutput = resp.address
                                }catch (e: Throwable) {
                                    loginOutput = e.message.toString()
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Get TikiToken") {
                            CoroutineScope(Dispatchers.IO).launch {
                                try{
                                    val resp =  TikiClient.auth.providerToken().await()
                                    loginOutput = resp
                                }catch (e: Throwable) {
                                    loginOutput = e.message.toString()
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Get Address Token") {
                            CoroutineScope(Dispatchers.IO).launch {
                                try{
                                    val resp =  TikiClient.auth.addressToken().await()
                                    loginOutput = resp
                                }catch (e: Throwable) {
                                    loginOutput = e.message.toString()
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Create license") {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                val license = TikiClient.license.create(this@MainActivity)
                                loginOutput = license.toString()
                                }catch (e: Throwable) {
                                    loginOutput = e.message.toString()
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Verify license") {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val license = TikiClient.license.verify()
                                    Log.d("************", license.toString())
                                    loginOutput = license.toString()
                                }catch (e: Throwable) {
                                loginOutput = e.message.toString()
                        }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Get Tiki key") {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val keyPair = TikiClient.auth.getKey()
                                    loginOutput = keyPair?.public?.encoded?.let { Base64.Default.encode(it) }.toString()
                                }catch (e: Throwable) {
                                    loginOutput = e.message.toString()
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Get address") {
                            CoroutineScope(Dispatchers.IO).launch {
                                try{
                                    val keyPair = TikiClient.auth.getKey()
                                    val address = keyPair?.let { TikiClient.auth.address(it) }
                                    loginOutput = address.toString()
                                    Log.d("**** address ****", address.toString())
                                }catch (e: Throwable) {
                                    loginOutput = e.message.toString()
                                }
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
