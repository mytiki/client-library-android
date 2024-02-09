/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.home.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.ProvidersInterface
import com.mytiki.publish.client.ui.account.ui.AccountTile


@Composable
fun HomeCarousel(providers: Set<ProvidersInterface>, navigateTo: (ProvidersInterface) -> Unit) {
    LazyRow(modifier = Modifier.fillMaxSize()) {
        items(providers.toList()) { provider ->
            AccountTile(
                provider = provider,
                padding = PaddingValues(horizontal = 10.dp)
            ) {
                navigateTo(it)
            }
        }
    }
}