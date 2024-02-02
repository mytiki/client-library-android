/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mytiki.apps_receipt_rewards.Rewards
import com.mytiki.apps_receipt_rewards.utils.components.DisplayCard
import com.mytiki.apps_receipt_rewards.utils.components.RewardsChart

@Composable
fun HomeCard(onMore: () -> Unit) {
    val earnings = Rewards.license.earnings()
    DisplayCard(height = 183.dp, horizontalPadding = 24.dp) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 31.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Month", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "$${earnings.monthCurrent} / $${earnings.monthTotal}",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    Text(text = "Lifetime", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "$${earnings.lifetime}",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }


                Text(
                    text = "Show More",
                    modifier = Modifier
                        .clickable { onMore() },
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )

            }
            RewardsChart(listOf((earnings.monthCurrent / earnings.monthTotal)))
        }
    }
}