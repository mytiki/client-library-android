/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.license.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mytiki.apps_receipt_rewards.R
import com.mytiki.apps_receipt_rewards.retailer.RetailerOffer

@Composable
fun OfferCard(retailerOffer: RetailerOffer, onClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 29.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = MaterialTheme.shapes.extraSmall,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.padding(end = 4.dp),
                ) {
                    Image(
                        painter = painterResource(
                            id = retailerOffer.accountProvider.resId()
                        ),
                        contentDescription = "${retailerOffer.accountProvider.displayName()} logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                    )
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                modifier = Modifier.widthIn(max = (configuration.screenWidthDp - 196).dp),
                text = retailerOffer.discount,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.outlineVariant,
                softWrap = true
            )

        }
        Spacer(modifier = Modifier.width(20.dp))

        Box(
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_rigth),
                contentDescription = "Go to discount",
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onClick() },
                tint = Color.Unspecified,
            )
        }
    }
}