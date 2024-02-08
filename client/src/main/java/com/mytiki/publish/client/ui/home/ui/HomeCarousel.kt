/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.home.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.ProvidersInterface
import com.mytiki.publish.client.ui.account.AccountStatus
import com.mytiki.publish.client.ui.account.ui.AccountTile


@Composable
fun HomeCarousel(providers: List<ProvidersInterface>, navigateTo: (ProvidersInterface) -> Unit) {
    val context = LocalContext.current
    LazyRow {
        items(providers.size) { index ->
            val provider = providers[index]
            AccountTile(
                provider = provider,
                accountStatus = AccountStatus.VERIFIED,
                padding = PaddingValues(horizontal = 10.dp)
            ) {
                navigateTo(it)
            }
        }
    }
}