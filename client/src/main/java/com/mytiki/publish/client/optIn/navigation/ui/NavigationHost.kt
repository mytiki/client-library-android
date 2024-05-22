/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.optIn.navigation.ui

import android.app.Activity
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
import com.mytiki.apps_receipt_rewards.navigation.NavigationRoute
import com.mytiki.publish.client.optIn.offers.OffersScreen

@Composable
fun NavigationHost(navController: NavHostController = rememberNavController()) {
  var finish by remember { mutableStateOf(false) }
  val context = LocalContext.current
  navController.addOnDestinationChangedListener { _, _, _ ->
    if (finish) (context as Activity).finish()
  }

  NavHost(navController, NavigationRoute.OFFERS.name) {
    navigation(
        startDestination = NavigationRoute.OFFERS.name, route = NavigationRoute.OFFERS.name) {
          composable(
              NavigationRoute.OFFERS.name,
              enterTransition = {
                slideInVertically(animationSpec = tween(700), initialOffsetY = { it })
              },
              exitTransition = {
                slideOutVertically(animationSpec = tween(700), targetOffsetY = { it })
              },
              popEnterTransition = {
                slideInVertically(animationSpec = tween(700), initialOffsetY = { it })
              },
              popExitTransition = {
                slideOutVertically(animationSpec = tween(700), targetOffsetY = { it })
              }) {
                OffersScreen()
              }
        }
  }
}
