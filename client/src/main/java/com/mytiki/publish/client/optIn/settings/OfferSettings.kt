package com.mytiki.publish.client.optIn.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.offer.Offer

@Composable
fun OfferSettings(offer: Offer, callbackOffer: (Pair<Offer, Boolean>) -> Unit) {

  val context = LocalContext.current
  var isAccepted by remember { mutableStateOf<Boolean?>(null) }
  LaunchedEffect(Unit) {
    isAccepted =
        if (TikiClient.offer.isAccepted(offer.ptr).await()) {
          TikiClient.offer.checkForPermissions(context, offer)
        } else false
  }
  Card(
      shape = RoundedCornerShape(15),
      border = BorderStroke(3.8.dp, MaterialTheme.colorScheme.primary),
      modifier = Modifier.fillMaxWidth(),
      colors =
          CardColors(Color.Transparent, Color.Transparent, Color.Transparent, Color.Transparent)) {
        Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
          Text(
              text = offer.description,
              style = MaterialTheme.typography.displayMedium,
              color = MaterialTheme.colorScheme.primary,
              textAlign = TextAlign.Center,
          )
          Spacer(modifier = Modifier.size(3.dp))
          Text(
              text = "${offer.offerRewards[0].amount} pts each month",
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.primary,
              textAlign = TextAlign.Center,
          )
          if (offer.mutable && isAccepted != null) {
            Spacer(modifier = Modifier.size(9.5.dp))
            Card(
                onClick = {
                  if (!isAccepted!!) {
                    TikiClient.offer.accept(context, offer)
                    isAccepted = true
                  } else {
                    TikiClient.offer.decline(context, offer)
                    isAccepted = false
                  }
                  callbackOffer(offer to isAccepted!!)
                },
                shape = RoundedCornerShape(50),
                border = BorderStroke(3.8.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.height(40.dp).width(200.dp),
                colors =
                    CardColors(
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent,
                        Color.Transparent)) {
                  Column(
                      modifier = Modifier.fillMaxSize(),
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.Center) {
                        Text(
                            text = if (!isAccepted!!) "Accept" else "Decline",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                        )
                      }
                }
          }
        }
      }
}
