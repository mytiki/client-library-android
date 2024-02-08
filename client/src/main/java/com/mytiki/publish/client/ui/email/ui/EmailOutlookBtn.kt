/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.email.ui

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
import com.mytiki.publish.client.R

@Composable
fun EmailOutlookBtn(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.outlook_sign_is),
            contentDescription = "outlook sign In button",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .requiredWidth(280.dp)
                .clickable { onClick() },
        )
    }
}