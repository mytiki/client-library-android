/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.optIn.navigation.ui

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.offer.Offer
import com.mytiki.publish.client.optIn.navigation.NavigationRoute
import com.mytiki.publish.client.optIn.offers.OffersScreen
import com.mytiki.publish.client.optIn.permissions.PermissionsScreen

@Composable
fun NavigationHost(
    activity: ComponentActivity,
    offer: Offer,
    navController: NavHostController = rememberNavController(),
    close: () -> Unit
) {
  var finish by remember { mutableStateOf(false) }
  val context = LocalContext.current
  navController.addOnDestinationChangedListener { _, _, _ ->
    if (finish) (context as Activity).finish()
  }

  var initialRoute = NavigationRoute.OFFERS.name

  offer.permissions?.forEach { permission ->
    if (!TikiClient.permission.isAuthorized(activity, permission)) {
      initialRoute = NavigationRoute.PERMISSIONS.name
    }
  }

  NavHost(navController, initialRoute) {
    composable(
        NavigationRoute.OFFERS.name,
        enterTransition = {
          slideInVertically(animationSpec = tween(700), initialOffsetY = { it })
        },
        exitTransition = { slideOutVertically(animationSpec = tween(700), targetOffsetY = { it }) },
        popEnterTransition = {
          slideInVertically(animationSpec = tween(700), initialOffsetY = { it })
        },
        popExitTransition = {
          slideOutVertically(animationSpec = tween(700), targetOffsetY = { it })
        }) {
          OffersScreen(offer, close)
        }
    composable(
        NavigationRoute.PERMISSIONS.name,
        enterTransition = {
          slideInVertically(animationSpec = tween(700), initialOffsetY = { it })
        },
        exitTransition = { slideOutVertically(animationSpec = tween(700), targetOffsetY = { it }) },
        popEnterTransition = {
          slideInVertically(animationSpec = tween(700), initialOffsetY = { it })
        },
        popExitTransition = {
          slideOutVertically(animationSpec = tween(700), targetOffsetY = { it })
        }) {
          PermissionsScreen(activity, offer, navController, close)
        }
  }
}
