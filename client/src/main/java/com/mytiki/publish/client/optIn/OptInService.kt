/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.optIn

import android.content.Context
import android.content.Intent
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.offer.Offer
import com.mytiki.publish.client.optIn.navigation.NavigationRoute

/**
 * OptInService class for managing opt-in operations.
 *
 * This class provides methods to show an offer and settings to the user. It maintains the state of
 * the current offer and offer list. It also keeps track of the initial navigation route.
 *
 * @property offer The current offer. This is the offer that is being shown to the user.
 * @property offerList The list of offers. This is used when showing the settings to the user.
 * @property initialRoute The initial navigation route. This is used to determine the first screen
 *   that the user sees when the OptInActivity is started.
 */
class OptInService() {
  internal var offer: Offer? = null
    get() {
      val respOffer = field
      field = null
      return respOffer
    }
    private set

  internal var offerList: List<Offer>? = null
    get() {
      val respOfferList = field
      field = null
      return respOfferList
    }
    private set

  internal var callback: ((Map<Offer, Boolean>) -> Unit)? = null
    get() {
      val respCallback = field
      field = null
      return respCallback
    }
    private set

  internal var initialRoute: NavigationRoute? = null
    private set

  /**
   * Shows an offer to the user.
   *
   * This method starts the OptInActivity and sets the initial navigation route based on the
   * permissions of the offer. If the user is not authorized for a permission, the initial route is
   * set to PERMISSIONS. Otherwise, it is set to OFFERS. The offer is then shown to the user.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this method is called. It is used to start the OptInActivity.
   * @param offer The Offer object containing the details of the offer to be shown.
   */
  fun showOffer(context: Context, offer: Offer, callback: (Map<Offer, Boolean>) -> Unit) {
    val intent = Intent(context, OptInActivity::class.java)
    initialRoute = NavigationRoute.OFFERS
    offer.permissions?.forEach { permission ->
      if (!TikiClient.permission.isAuthorized(context, permission)) {
        initialRoute = NavigationRoute.PERMISSIONS
      }
    }
    this.offer = offer
    this.callback = callback
    context.startActivity(intent)
  }

  /**
   * Shows the settings to the user.
   *
   * This method starts the OptInActivity and sets the initial navigation route to SETTINGS. The
   * settings, which include a list of offers, are then shown to the user.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this method is called. It is used to start the OptInActivity.
   * @param offerList The list of Offer objects to be shown in the settings.
   */
  fun showSettings(
      context: Context,
      offerList: List<Offer>,
      callback: (Map<Offer, Boolean>) -> Unit
  ) {
    val intent = Intent(context, OptInActivity::class.java)
    initialRoute = NavigationRoute.SETTINGS
    this.offerList = offerList
    this.callback = callback
    context.startActivity(intent)
  }
}
