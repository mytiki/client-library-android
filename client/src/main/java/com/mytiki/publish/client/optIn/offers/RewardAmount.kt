package com.mytiki.publish.client.optIn.offers

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.offer.OfferReward

@Composable
fun RewardAmount(reward: OfferReward) {
  Surface(
      modifier = Modifier.size(132.52.dp),
      shape = CircleShape,
      border = BorderStroke(6.44.dp, MaterialTheme.colorScheme.primary),
      color = Color.Transparent) {
        Column(
            modifier = Modifier.size(132.52.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Row(verticalAlignment = Alignment.Bottom) {
                Text(reward.amount, style = MaterialTheme.typography.titleLarge)
                Text(
                    "pts",
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier.padding(bottom = 4.dp))
              }
              Text("each month", style = MaterialTheme.typography.titleSmall)
            }
      }
}
