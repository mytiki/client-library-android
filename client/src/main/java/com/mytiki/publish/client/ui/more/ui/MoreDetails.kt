/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.more.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mytiki.apps_receipt_rewards.R
import com.mytiki.apps_receipt_rewards.Rewards

@Composable
fun MoreDetails(
    onTerms: () -> Unit,
    onDecline: () -> Unit,
) {
    Text(
        "Program Details",
        modifier = Modifier.padding(horizontal = 21.dp),
        style = MaterialTheme.typography.headlineLarge
    )

    Spacer(modifier = Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .padding(21.dp, 0.dp, 17.dp, 0.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 4.dp),
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_account),
                    contentDescription = "Account icon",
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "What data do we collect?",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Learn about how your data powers your",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center,
                )
                Text(
                    "Cashback Connections",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 49.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column {
                        IconTitle(
                            Modifier
                                .width(90.dp)
                                .height(20.dp),
                            painter = painterResource(id = R.drawable.ic_purchase),
                            text = "Purchases"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        IconTitle(
                            Modifier
                                .width(90.dp)
                                .height(20.dp),
                            painter = painterResource(id = R.drawable.ic_user_id),
                            text = "User ID"
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Column {
                        IconTitle(
                            Modifier
                                .width(90.dp)
                                .height(20.dp),
                            painter = painterResource(id = R.drawable.ic_receipt),
                            text = "Receipts"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
                Text(
                    "How is that data used?",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                IconTitle(
                    painter = painterResource(id = R.drawable.ic_cashback),
                    text = "Find cashback rewards"
                )

                Spacer(modifier = Modifier.height(12.dp))
                IconTitle(
                    painter = painterResource(id = R.drawable.ic_offers),
                    text = "Relevant ads and offers"
                )

                Spacer(modifier = Modifier.height(12.dp))
                IconTitle(
                    painter = painterResource(id = R.drawable.ic_chart),
                    text = "Create shopper insights"
                )

                Spacer(modifier = Modifier.height(48.dp))
                Divider(
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clickable { },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Report an issue", style = MaterialTheme.typography.labelLarge)
                    Image(
                        painter = painterResource(id = R.drawable.ic_issue),
                        contentDescription = "",
                        modifier = Modifier.size(18.dp)
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clickable {
                            onTerms()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Data licensing agreement", style = MaterialTheme.typography.labelLarge)
                    Image(
                        painter = painterResource(id = R.drawable.ic_union),
                        contentDescription = "",
                        modifier = Modifier.size(18.dp)
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 1.dp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clickable {
                            Rewards.license.decline()
                            onDecline()
                        },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Opt out of Cashback Connections",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_block),
                        contentDescription = "",
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

        }
    }
}