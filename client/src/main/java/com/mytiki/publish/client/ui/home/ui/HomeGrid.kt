/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.ProvidersInterface
import com.mytiki.publish.client.ui.account.AccountStatus
import com.mytiki.publish.client.ui.account.ui.AccountTile

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeGrid(providers: Set<ProvidersInterface>, onAccountProvider: (ProvidersInterface) -> Unit) {

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Spacer(modifier = Modifier.height(49.dp))
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Increase Earnings",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                maxItemsInEachRow = 3
            ) {
                providers.forEach { provider ->
                    AccountTile(
                        provider = provider,
                        padding = PaddingValues(horizontal = 10.dp)
                    ) { onAccountProvider(provider) }
                }
            }
        }
    }
}