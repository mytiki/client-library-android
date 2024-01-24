package com.mytiki.publish.client

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import com.mytiki.publish.client.auth.AuthToken
import com.mytiki.publish.client.theme.TikiClientTheme
import com.mytiki.sdk.capture.receipt.capacitor.MainButton
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.util.Date

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
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
                        MainButton(text = "Token repository") {
                            TikiClient.email.emailAccountRepository.saveToken(
                                this@MainActivity,
                                "tiki@email.com",
                                AuthToken("auth", "refresh", Date.from(LocalDateTime.of(2024,7,30,12,30).toInstant(ZoneOffset.UTC)))
                            )
                            loginOutput = TikiClient.email.emailAccountRepository.getToken(this@MainActivity,"tiki@email.com").toString()
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

