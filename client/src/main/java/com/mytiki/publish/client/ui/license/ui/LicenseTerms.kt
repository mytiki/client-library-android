/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.license.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mytiki.publish.client.ui.components.Header
import com.mytiki.publish.client.ui.components.MainButton
import com.mytiki.publish.client.ui.license.LicenseViewModel

@Composable
fun LicenseTerms(
    licenseViewModel: LicenseViewModel,
    onBackButton: () -> Unit,
    onAccept: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxSize(),

            topBar = {
                Column {
                    Spacer(modifier = Modifier.height(60.dp))
                    Header(text = "PROGRAM TERMS") {
                        onBackButton()
                    }
                }
            },
            bottomBar = {
                Column {
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(42.dp))
                    if (!licenseViewModel.isLicensed.value) {
                        MainButton(
                            text = "I agree", isfFilled = true
                        ) {
                            licenseViewModel.acceptLicense()
                            onAccept()
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(it),
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = licenseViewModel.terms(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            }

        }
    }
}