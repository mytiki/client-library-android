/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.optIn.navigation.ui

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
import com.mytiki.publish.client.optIn.settings.SettingsScreen

@Composable
fun NavigationHost(
    activity: ComponentActivity,
    navController: NavHostController = rememberNavController(),
    close: () -> Unit
) {
  navController.addOnDestinationChangedListener { _, _, _ -> }
  TikiClient.optIn.initialRoute?.name?.let {
    NavHost(navController, it) {
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
            TikiClient.optIn.offer?.let { offer ->
              OffersScreen(offer, close) { map: Map<Offer, Boolean> ->
                TikiClient.optIn.callback?.invoke(map)
              }
            }
          }
      composable(
          NavigationRoute.PERMISSIONS.name,
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
            TikiClient.optIn.offer?.let { offer ->
              PermissionsScreen(activity, offer, navController, close) { map: Map<Offer, Boolean> ->
                TikiClient.optIn.callback?.invoke(map)
              }
            }
          }
      composable(
          NavigationRoute.SETTINGS.name,
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
            TikiClient.optIn.offerList?.let { offerList ->
              SettingsScreen(offerList, close) { map: Map<Offer, Boolean> ->
                TikiClient.optIn.callback?.invoke(map)
              }
            }
          }
    }
  }
}
