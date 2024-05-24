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
      val respOFunction = field
      field = null
      return respOFunction
    }
    private set

  internal var initialRoute: NavigationRoute? = null
    private set

  fun showOffer(context: Context, offer: Offer) {
    val intent = Intent(context, OptInActivity::class.java)
    initialRoute = NavigationRoute.OFFERS
    offer.permissions?.forEach { permission ->
      if (!TikiClient.permission.isAuthorized(context, permission)) {
        initialRoute = NavigationRoute.PERMISSIONS
      }
    }
    this.offer = offer
    context.startActivity(intent)
  }

  fun showSettings(context: Context, offerList: List<Offer>) {
    val intent = Intent(context, OptInActivity::class.java)
    initialRoute = NavigationRoute.SETTINGS
    this.offerList = offerList
    context.startActivity(intent)
  }
}
