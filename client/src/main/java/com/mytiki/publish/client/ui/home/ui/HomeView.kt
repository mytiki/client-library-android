/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.home.ui

import BottomSheet
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.ui.components.BottomSheetHeader
import com.mytiki.publish.client.ProvidersInterface
import com.mytiki.publish.client.ui.home.HomeViewModel

@Composable
fun HomeView(
    homeViewModel: HomeViewModel,
    onProvider: (ProvidersInterface) -> Unit,
    onMore: () -> Unit,
) {
    val context = LocalContext.current
    val providers = homeViewModel.providers()

    BottomSheet {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            BottomSheetHeader(
                title = "CASHBACK CONNECTIONS",
                subTitle = "Share data. Earn cash.",
            )
            Spacer(modifier = Modifier.height(48.dp))
            HomeCard(homeViewModel, onMore)
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Increase Earnings",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(24.dp))

//            if (isExpanded.value) {
                HomeGrid(providers, onProvider)
//            } else {
//                HomeCarousel(providers, onProvider)
//            }

        }
    }
}
