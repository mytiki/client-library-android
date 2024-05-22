package com.mytiki.publish.client.optIn.offers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun OffersScreen() {

  Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.Start) {
      Text(text = "Share your purchase history:")
    }
  }
}
