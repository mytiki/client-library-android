package com.mytiki.publish.client.example_app

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
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
import com.mytiki.publish.client.permission.Permission
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config =
            Config(
                providerId = "2b08b660-84cd-410c-9c92-836993e90c93",
                publicKey =
                "MIIBCgKCAQEAoHhIrvp0aY+VhRquH6dW3fgwg+n7QqSICVvjoWceSFnGiAGCmI661BQp8QYpTqdHgkaehWMgADtFTuaHJvbG88NY1Ah9wbKf2uGzu+uIaXTfBFojc/9hvwPe+U5bJ6O9rFcpoAhxqcR0qC8h17Q/fRIPNrNFm8u0pfK/kLRQzsnH7rrZYYOjFqzpazSdmKuSUxVoKOGlAddlqQpYH8oEdNmJKj7aWmMXgZMScaEd2JqQihgULXGTuA23iTaBFmmBXSp2iCLxLRLEAjh+zLQhNg00bk/jW3pU1A+36ktCAwOBa8vX6sl5xMO57+iGAJgvBVyA61U1t9QPVOdsjjjg9QIDAQAB",
                companyName = "gabriel",
                companyJurisdiction = "US",
                tosUrl = "https://mytiki.com",
                privacyUrl = "https://mytiki.com")
        TikiClient.configure(config)

        val userID = "example_user_id"
        val md5 = MessageDigest.getInstance("MD5")
        val hashID = BigInteger(1, md5.digest(userID.toByteArray())).toString(16).padStart(32, '0')

        TikiClient.initialize(hashID)

        setContent {
            var userIdInput by remember { mutableStateOf("") }
            var loginOutput by remember { mutableStateOf("") }
            var image by remember { mutableStateOf<Bitmap?>(null) }

            TikiClientTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier.padding(horizontal = 15.dp).verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = "Tiki Example",
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = loginOutput,
                        )

                        Spacer(modifier = Modifier.height(30.dp))
                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Creatre License") {
                            MainScope().async {
                                loginOutput = TikiClient.createLicense(this@MainActivity).await().toString()
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Scan") {
                            TikiClient.scan(this@MainActivity) {
                                image = it
                                loginOutput = "Worked"
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Publish") {
                            if (image != null) {
                                TikiClient.publish(image!!)
                                loginOutput = "image published"
                            } else loginOutput = "No image to publish"
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "request permissions") {
                            TikiClient.permissions(this@MainActivity, Permission.entries) {
                                loginOutput = it.toString()
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "login") {
                            TikiClient.email.login(
                                this@MainActivity,
                                EmailProviderEnum.GOOGLE,
                                EmailKeys("1079849396355-q687vpf16ovveafo6robcgi1kaoaem3e.apps.googleusercontent.com", ""),
                                "com.googleusercontent.apps.1079849396355-q687vpf16ovveafo6robcgi1kaoaem3e:/oauth2redirect"
                            ){}
                        }
                    }
                }
            }
        }
    }
}
