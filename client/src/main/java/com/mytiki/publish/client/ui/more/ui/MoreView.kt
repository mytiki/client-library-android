/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.more.ui

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.ui.components.Header
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.ui.more.MoreViewModel

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun MoreView(
    moreViewModel: MoreViewModel,
    onProvider: (EmailProviderEnum) -> Unit,
    onTerms: () -> Unit,
    onDecline: () -> Unit,
    onBackButton: () -> Unit
) {
    val context = LocalContext.current

    val emailProvider = moreViewModel.emailProvider(context)
    val largestContributors = moreViewModel.largestContributors()

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

            MoreEstimate(largestContributors)

            Spacer(modifier = Modifier.height(24.dp))

            if (emailProvider.isNotEmpty()) {
                MoreAccounts(emailProvider, onProvider)
                Spacer(modifier = Modifier.height(30.dp))
            }

            MoreDetails(moreViewModel, onTerms, onDecline)

            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}