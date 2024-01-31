package com.mytiki.publish.client.example_app

import android.os.Bundle
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.example_app.theme.TikiClientTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = loginOutput,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Justify
                        )

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "Login") {
                            TikiClient.email.login(
                                this@MainActivity,
                                EmailProviderEnum.GOOGLE,
                                "1079849396355-q687vpf16ovveafo6robcgi1kaoaem3e.apps.googleusercontent.com",
                                "com.googleusercontent.apps.1079849396355-q687vpf16ovveafo6robcgi1kaoaem3e:/oauth2redirect"
                            )
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                        MainButton(text = "get token") {
                            val tokenList = TikiClient.email.emailAccountRepository.getToken(this@MainActivity, "gabrielschuler3@gmail.com")
                            loginOutput = tokenList.toString()
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