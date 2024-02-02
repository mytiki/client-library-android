/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.more.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mytiki.apps_receipt_rewards.more.MoreContributor
import com.mytiki.apps_receipt_rewards.utils.components.RewardsChart

@Composable
fun MoreEstimate(data: List<MoreContributor>) {
    Text(
        "Monthly Estimate",
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
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Largest Contributors",
                            style = MaterialTheme.typography.displaySmall,
                            modifier = Modifier.height(18.dp)
                        )
                        data.forEach { item ->
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.name}: ${
                                    String.format(
                                        "%.0f",
                                        item.percentage.times(100)
                                    )
                                }%",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.height(18.dp)
                            )
                        }
                    }
                }
                RewardsChart(values = data.map { it.percentage }.toList())
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Estimate calculated based on your spending history and available offers from eligible retailers.",
        modifier = Modifier.padding(horizontal = 21.dp),
        style = MaterialTheme.typography.titleSmall
    )
}