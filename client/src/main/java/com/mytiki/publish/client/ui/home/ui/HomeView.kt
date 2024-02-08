/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.home.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.ui.components.BottomSheetHeader
import com.mytiki.publish.client.ProvidersInterface
import com.mytiki.publish.client.ui.components.bottomSheet.HideableBottomSheetValue
import com.mytiki.publish.client.ui.components.bottomSheet.rememberHideableBottomSheetState
import com.mytiki.publish.client.ui.components.bottomSheet.ui.HideableBottomSheetScaffold
import com.mytiki.publish.client.ui.home.HomeViewModel

@Composable
fun HomeView(
    activity: AppCompatActivity,
    homeViewModel: HomeViewModel,
    onProvider: (ProvidersInterface) -> Unit,
    onMore: () -> Unit,
) {
    val providers = homeViewModel.providers()
    val sheetState = mutableStateOf(HideableBottomSheetValue.HalfExpanded)
    val bottomSheetState = rememberHideableBottomSheetState(
        initialValue = HideableBottomSheetValue.HalfExpanded,
        confirmValueChange = {sheetValue ->
//            if (sheetValue ==  HideableBottomSheetValue.HalfExpanded){
//                sheetState.value = sheetValue
//            }
//            if (sheetValue ==  HideableBottomSheetValue.Expanded){
//                sheetState.value = sheetValue
//            }
            return@rememberHideableBottomSheetState true
        }
    ){
        activity.finish()
    }

    HideableBottomSheetScaffold(
        bottomSheetState = bottomSheetState,
        modifier = Modifier.fillMaxSize(),
        bottomSheetContent = {
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
                Spacer(modifier = Modifier.height(24.dp))


                if (bottomSheetState.currentValue == HideableBottomSheetValue.Expanded) {
                    HomeGrid(providers, onProvider)
                } else {
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = "Increase Earnings",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    HomeCarousel(providers, onProvider)
                    Spacer(modifier = Modifier.height(200.dp))
                }
            }
        }
    ){}
}
