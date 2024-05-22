package com.mytiki.publish.client.optIn.offers

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable fun RewardAmount() {

    Card(
        shape = MaterialTheme.shapes.medium,
    ) {
        Column() {
            Text("Reward Amount")
            Text("100")
        }
    }
}
