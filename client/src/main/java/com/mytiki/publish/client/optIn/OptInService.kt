/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.optIn

import android.content.Context
import android.content.Intent
import com.mytiki.publish.client.offer.Offer

class OptInService() {
  internal var offer: Offer? = null
    get() {
      val respOffer = field
      field = null
      return respOffer
    }
    private set

  fun show(context: Context, offer: Offer) {
    val intent = Intent(context, OptInActivity::class.java)
    this.offer = offer
    context.startActivity(intent)
  }
}
