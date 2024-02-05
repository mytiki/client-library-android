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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mytiki.apps_receipt_rewards.utils.components.BottomSheetHeader
import com.mytiki.publish.client.ProvidersInterface

val isExpanded = mutableStateOf(false)

var providers by mutableStateOf<List<ProvidersInterface>?>(null)
//fun updateAccounts(context: Context){
//    MainScope().async {
//        providers = TikiUI.account.providers(context)
//        Log.e("************", providers.toString())
//    }
//}

@Composable
fun HomeView(
    onProvider: (ProvidersInterface) -> Unit,
    onMore: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
//    if(providers == null) updateAccounts(context)

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
            HomeCard(onMore)
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Increase Earnings",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(24.dp))
            if(providers != null) {
                if (isExpanded.value) {
                    HomeGrid(providers, onProvider)
                } else {
                    HomeCarousel(providers, onProvider)
                }
            }
        }
    }
}
