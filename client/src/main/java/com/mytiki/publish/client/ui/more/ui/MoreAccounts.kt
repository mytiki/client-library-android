/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.more.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.ui.account.AccountStatus
import com.mytiki.publish.client.ui.account.ui.AccountTile

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoreAccounts(
    accountsList: List<AccountProvider>,
    onClick: (AccountProvider) -> Unit
) {
    Text(
        "Accounts",
        modifier = Modifier.padding(horizontal = 21.dp),
        style = MaterialTheme.typography.headlineLarge
    )

    Spacer(modifier = Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .padding(21.dp, 0.dp, 17.dp, 0.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 4.dp),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                accountsList.forEach { accountProvider ->
                    AccountTile(
                        provider = accountProvider,
                        accountStatus = AccountStatus.UNLINKED,
                        padding = PaddingValues(horizontal = 4.dp, vertical = 12.dp),
                        onClick = { onClick(accountProvider) }
                    )
                }
            }
        }
    }
}