/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.retailer.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mytiki.apps_receipt_rewards.Rewards
import com.mytiki.publish.client.ui.account.Account
import com.mytiki.publish.client.ui.account.ui.AccountCard
import com.mytiki.publish.client.ui.account.ui.AccountDisplay
import com.mytiki.apps_receipt_rewards.license.ui.OfferCard
import com.mytiki.apps_receipt_rewards.utils.components.Header
import com.mytiki.apps_receipt_rewards.utils.components.LoginForm
import com.mytiki.apps_receipt_rewards.utils.components.MainButton
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

var accounts by mutableStateOf<List<Account>?>(null)
fun updateAccounts(context: Context, provider: AccountProvider){
    MainScope().async {
        accounts = Rewards.account.accounts(context, provider)
    }
}

@Composable
fun RetailerView(
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
                    Header(text = provider.displayName()) {
                        onBackButton()
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(28.dp))
                AccountDisplay(
                    provider,
                    239.dp,
                    "3% cashback on all purchases",
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Account",
                    modifier = Modifier.padding(horizontal = 21.dp),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            if (accounts.isNullOrEmpty()) {
                item {
                    LoginForm(activity, username, password, provider) {
                        updateAccounts(context, provider)
                        Rewards.capture.scrape(context, provider)
                    }
                }
            } else {
                items(accounts!!) {
                    Spacer(modifier = Modifier.height(32.dp))
                    AccountCard(it, false) { Rewards.account.logout(context, it.username, it.provider) }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
                MainButton(
                    modifier = Modifier.padding(horizontal = 21.dp),
                    text = "Scan receipt",
                    isfFilled = false
                ) {
                    MainScope().async {
                        Rewards.capture.scan(activity).await()
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "More Offers",
                    modifier = Modifier
                        .padding(horizontal = 21.dp)
                        .height(36.dp),
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
            items(Rewards.capture.offers(provider)) {
                OfferCard(it) { }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}