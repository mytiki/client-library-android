package com.mytiki.publish.client.optIn.permissions

import androidx.activity.ComponentActivity
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
import androidx.navigation.NavHostController
import com.mytiki.publish.client.R
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.offer.Offer
import com.mytiki.publish.client.optIn.components.Button
import com.mytiki.publish.client.optIn.navigation.NavigationRoute
import com.mytiki.publish.client.optIn.theme.Colors

@Composable
fun PermissionsScreen(
    activity: ComponentActivity,
    offer: Offer,
    navController: NavHostController,
    close: () -> Unit
) {
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
              modifier = Modifier.fillMaxSize(),
              horizontalAlignment = Alignment.Start,
              verticalArrangement = Arrangement.Center) {
                Column(
                    modifier = Modifier.padding(horizontal = 21.98.dp),
                    horizontalAlignment = Alignment.Start) {
                      Text(
                          modifier = Modifier.padding(horizontal = 14.dp),
                          text = "Allow tracking on the next screen for:",
                          style = MaterialTheme.typography.displayLarge)
                      Spacer(modifier = Modifier.size(39.dp))
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(14.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_present),
                            contentDescription = "present",
                            modifier = Modifier.size(43.61.dp))
                        Spacer(modifier = Modifier.width(16.68.dp))
                        Text(
                            "Data offers and promotions just for you",
                            style = MaterialTheme.typography.displayMedium)
                      }
                      Spacer(modifier = Modifier.size(35.dp))
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(14.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_hand),
                            contentDescription = "hand",
                            modifier = Modifier.size(43.61.dp))
                        Spacer(modifier = Modifier.width(16.68.dp))
                        Text(
                            "Advertisements that match your interests",
                            style = MaterialTheme.typography.displayMedium)
                      }
                      Spacer(modifier = Modifier.size(35.dp))
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(14.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = "person",
                            modifier = Modifier.size(43.61.dp))
                        Spacer(modifier = Modifier.width(17.38.dp))
                        Text(
                            "An improved personalized experience over time",
                            style = MaterialTheme.typography.displayMedium)
                      }
                      Spacer(modifier = Modifier.size(45.dp))
                      Text(
                          "You can change this option later in the Settings app.",
                          style = MaterialTheme.typography.displayMedium,
                          modifier = Modifier.padding(horizontal = 14.dp))
                    }
                Spacer(modifier = Modifier.size(37.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 21.98.dp),
                    horizontalAlignment = Alignment.Start) {
                      Button("Continue") {
                        offer.permissions?.let {
                          TikiClient.permission.requestPermissions(activity, it) { map ->
                            map.forEach { (_, granted) ->
                              if (!granted) {
                                close()
                              }
                            }
                            navController.navigate(NavigationRoute.OFFERS.name)
                          }
                        }
                      }
                    }
              }
        }
  }
}
