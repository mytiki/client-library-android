package com.mytiki.publish.client.optIn.offers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.offer.Offer
import com.mytiki.publish.client.optIn.components.Button
import com.mytiki.publish.client.optIn.theme.Colors

@Composable
fun OffersScreen(offer: Offer, close: () -> Unit, callbackOffer: (Map<Offer, Boolean>) -> Unit) {
  val context = LocalContext.current

  Surface(modifier = Modifier.fillMaxSize()) {
    Box(
        modifier = Modifier.fillMaxSize().background(Colors().backgroundGradient),
        contentAlignment = Alignment.TopCenter) {
          Image(
              painter = painterResource(id = R.drawable.bg_balloons),
              contentDescription = "balloons",
              contentScale = ContentScale.Fit)
          Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.Start) {
            Spacer(modifier = Modifier.size(105.41.dp))
            Column(
                modifier = Modifier.padding(horizontal = 35.31.dp),
                horizontalAlignment = Alignment.Start) {
                  Text(
                      text = "Share your purchase history:",
                      style = MaterialTheme.typography.displayLarge)
                  Spacer(modifier = Modifier.size(27.5.dp))
                  Column(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalAlignment = Alignment.CenterHorizontally) {
                        OffersRewardAmount(reward = offer.offerRewards[0])
                      }
                  Spacer(modifier = Modifier.size(39.5.dp))
                  Text(
                      text = "We use your data for:", style = MaterialTheme.typography.displaySmall)
                  Spacer(modifier = Modifier.size(13.dp))
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_chart),
                        contentDescription = "chart",
                        modifier = Modifier.size(18.9.dp))
                    Spacer(modifier = Modifier.width(9.5.dp))
                    Text("Aggregate market insights", style = MaterialTheme.typography.displaySmall)
                  }
                  Spacer(modifier = Modifier.size(9.5.dp))
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_percentage),
                        contentDescription = "percentage",
                        modifier = Modifier.size(18.9.dp))
                    Spacer(modifier = Modifier.width(9.5.dp))
                    Text(
                        "Exclusive offers just for you",
                        style = MaterialTheme.typography.displaySmall)
                  }
                  Spacer(modifier = Modifier.size(9.5.dp))
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_robot),
                        contentDescription = "robot",
                        modifier = Modifier.size(18.9.dp))
                    Spacer(modifier = Modifier.width(9.5.dp))
                    Text("Improving systems like AI", style = MaterialTheme.typography.displaySmall)
                  }
                  Spacer(modifier = Modifier.size(14.dp))
                  Text(
                      "Learn more or change your mind any time in settings.",
                      style = MaterialTheme.typography.displaySmall)
                }
            Spacer(modifier = Modifier.size(34.5.dp))
            Column(
                modifier = Modifier.padding(horizontal = 21.98.dp),
                horizontalAlignment = Alignment.Start) {
                  Button("Link Card") {
                    TikiClient.offer.accept(context, offer)
                    callbackOffer(mapOf(offer to true))
                    close()
                  }
                  Spacer(modifier = Modifier.size(14.dp))
                  Button("No Thanks", true) {
                    TikiClient.offer.decline(context, offer)
                    callbackOffer(mapOf(offer to true))
                    close()
                  }
                }
          }
        }
  }
}
