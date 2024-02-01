/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.email.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mytiki.apps_receipt_rewards.R

@Composable
fun EmailGoogleBtn(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.google_sign_in),
            contentDescription = "google sign In button",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .requiredWidth(280.dp)
                .clickable { onClick() },
        )
    }
}