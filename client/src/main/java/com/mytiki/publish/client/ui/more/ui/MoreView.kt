/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.more.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mytiki.apps_receipt_rewards.Rewards
import com.mytiki.apps_receipt_rewards.utils.components.Header
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

var providers by mutableStateOf<List<AccountProvider>?>(null)
fun updateProviders(context: Context, provider: AccountProvider){
    MainScope().async {
        providers = Rewards.account.providers(context)
    }
}
@Composable
fun MoreView(
    onProvider: (AccountProvider) -> Unit,
    onTerms: () -> Unit,
    onDecline: () -> Unit,
    onBackButton: () -> Unit
) {
    val context = LocalContext.current
    var accList by mutableStateOf<List<AccountProvider>?>(null)
    MainScope().async {
        accList = Rewards.account.accounts(context).map { it.provider }.distinctBy{it.toString()}
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Box(modifier = Modifier.padding(horizontal = 21.dp)) {
                Header(text = "BACK") {
                    onBackButton()
                }
            }

            Spacer(modifier = Modifier.height(34.dp))

            MoreEstimate(Rewards.capture.largestContributors())

            Spacer(modifier = Modifier.height(24.dp))

            if (!accList.isNullOrEmpty()) {
                MoreAccounts(accList!!, onProvider)
                Spacer(modifier = Modifier.height(30.dp))
            }

            MoreDetails(onTerms, onDecline)

            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}