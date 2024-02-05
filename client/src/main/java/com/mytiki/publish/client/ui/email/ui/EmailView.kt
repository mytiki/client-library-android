/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.email.ui

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mytiki.apps_receipt_rewards.Rewards
import com.mytiki.publish.client.ui.account.Account
import com.mytiki.publish.client.ui.account.ui.AccountCard
import com.mytiki.publish.client.ui.account.ui.AccountDisplay
import com.mytiki.apps_receipt_rewards.utils.components.Header
import com.mytiki.apps_receipt_rewards.utils.components.LoginForm
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

var accounts by mutableStateOf<List<Account>?>(null)
fun updateAccounts(context: Context, provider: AccountProvider){
    MainScope().async {
        accounts = Rewards.account.accounts(context, provider)
    }
}

@Composable
fun EmailView(
    activity: AppCompatActivity,
    provider: AccountProvider,
    onBackButton: () -> Unit
) {
    val context = LocalContext.current

    val username = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }

    if (accounts == null) updateAccounts(context, provider)

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
                    provider,
                    275.dp,
                    "When you connect your Gmail account, we auto-identify receipts and process available cashback rewards",
                )
            }

            if (!accounts.isNullOrEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Accounts",
                        modifier = Modifier.padding(horizontal = 21.dp),
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
                items(accounts!!) {
                    Spacer(modifier = Modifier.height(32.dp))
                    AccountCard(it, false) { Rewards.account.logout(context, it.username, it.provider) }
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
                if (provider == AccountProvider.Email(EmailEnum.GMAIL)) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        EmailGoogleBtn {
//                            TikiUI.account.login(activity,"authKeys@gmail.com", "213", provider)
//                            MainScope().async {
//                                accounts = TikiUI.account.accounts.toList().filter {it.provider.name() == provider.name()}
//                            }
                            val alertDialog = AlertDialog.Builder(activity)
                                .setTitle(null)
                                .setMessage("Functionalities not implemented yet")
                                .setPositiveButton("OK", null)
                                .create()
                            alertDialog.show()
                        }
                    }
                }
                Spacer(modifier = Modifier.height(38.dp))
                Row(
                    modifier = Modifier
                        .padding(horizontal = 21.dp)
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Text(
                        text = "or",
                        modifier = Modifier.padding(horizontal = 15.dp),
                        style = MaterialTheme.typography.displaySmall,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))

                LoginForm(activity, username, password, provider) {
                    updateAccounts(
                        context,
                        provider
                    )
                    Rewards.capture.scrape(context, provider)
                }

            }
        }
    }
}