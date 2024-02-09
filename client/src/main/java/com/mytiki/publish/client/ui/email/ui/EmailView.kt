/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.email.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.ui.account.ui.AccountCard
import com.mytiki.publish.client.ui.account.ui.AccountDisplay
import com.mytiki.publish.client.ui.components.Header
import com.mytiki.publish.client.ui.email.EmailViewModel

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun EmailView(
    activity: AppCompatActivity,
    emailViewModel: EmailViewModel,
    emailProvider: EmailProviderEnum,
    onBackButton: () -> Unit
) {
    val context = LocalContext.current

    val accounts = emailViewModel.accounts

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()

        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                ) {
                    Spacer(modifier = Modifier.height(64.dp))
                    Header(text = "") {
                        onBackButton()
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(28.dp))
                AccountDisplay(
                    emailProvider,
                    275.dp,
                    "When you connect your Gmail account, we auto-identify receipts and process available cashback rewards",
                )
            }

            if (accounts.value.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Accounts",
                        modifier = Modifier.padding(horizontal = 21.dp),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
                items(items = accounts.value.toList()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    AccountCard(it, false) { emailViewModel.logout(context, it) }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            item {
                Spacer(modifier = Modifier.height(38.dp))
                Text(
                    text = "Add Account",
                    modifier = Modifier.padding(horizontal = 21.dp),
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(46.dp))
                if (emailProvider == EmailProviderEnum.GOOGLE) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmailGoogleBtn {
                           emailViewModel.login(context, emailProvider)
                        }
                    }
                }
                if (emailProvider == EmailProviderEnum.OUTLOOK) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmailOutlookBtn {
                           emailViewModel.login(context, emailProvider)
                        }
                    }
                }
            }
        }
    }
}