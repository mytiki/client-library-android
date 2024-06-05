package com.mytiki.publish.client.optIn.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.offer.Offer

@Composable
fun SettingsOffer(offer: Offer, callbackOffer: (Pair<Offer, Boolean>) -> Unit) {

  val context = LocalContext.current
  var isActive by remember { mutableStateOf<Boolean?>(null) }
  LaunchedEffect(Unit) {
    isActive =
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
          if (offer.mutable && isActive != null) {
            Spacer(modifier = Modifier.size(9.5.dp))
            Switch(
                checked = isActive!!,
                onCheckedChange = {
                  isActive = it
                  if (it) {
                    TikiClient.offer.accept(context, offer)
                  } else {
                    TikiClient.offer.decline(context, offer)
                  }
                  callbackOffer(offer to isActive!!)
                },
                colors =
                    SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00B272),
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedTrackColor = Color.Transparent,
                    ))
          }
        }
      }
}
