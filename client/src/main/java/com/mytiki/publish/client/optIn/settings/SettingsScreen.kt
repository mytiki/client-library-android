package com.mytiki.publish.client.optIn.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.R
import com.mytiki.publish.client.offer.Offer
import com.mytiki.publish.client.optIn.components.Button
import com.mytiki.publish.client.optIn.theme.Colors

@Composable
fun SettingsScreen(offerList: List<Offer>, close: () -> Unit) {
  val context = LocalContext.current

  Surface(modifier = Modifier.fillMaxSize()) {
    Box(
        modifier = Modifier.fillMaxSize().background(Colors().backgroundGradient),
        contentAlignment = Alignment.TopCenter) {
          Image(
              painter = painterResource(id = R.drawable.bg_pineapples),
              contentDescription = "pineapples",
              contentScale = ContentScale.Fit)
          Column(
              modifier = Modifier.fillMaxSize().padding(horizontal = 21.98.dp),
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.Center) {
                Text(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    text = "Change your offers settings:",
                    style = MaterialTheme.typography.displayLarge)
                Spacer(modifier = Modifier.size(39.dp))

                LazyColumn {
                  items(offerList) { offer ->
                    OfferSettings(offer = offer)
                    Spacer(modifier = Modifier.size(9.5.dp))
                  }
                }

                Spacer(modifier = Modifier.size(39.dp))
                Button(title = "Close", true, onClick = { close() })
              }
        }
  }
}
