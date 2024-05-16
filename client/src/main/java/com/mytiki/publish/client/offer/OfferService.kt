package com.mytiki.publish.client.offer

import android.content.Context
import com.mytiki.publish.client.TikiClient
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

/** This class provides services related to offers. */
class OfferService {

  /**
   * Accepts an offer.
   *
   * @param context The context. This is typically the application context or the current activity.
   * @param offer The offer to be accepted.
   * @return The activated offer.
   */
  fun accept(context: Context, offer: Offer): Offer {
    MainScope().async { TikiClient.license.create(context, offer.use, offer.tags) }
    return offer.activate()
  }

  /**
   * Declines an offer.
   *
   * @param context The context. This is typically the application context or the current activity.
   * @param offer The offer to be declined.
   * @return The deactivated offer.
   */
  fun decline(context: Context, offer: Offer): Offer {
    MainScope().async { TikiClient.license.create(context, null, listOf(), offer.tags) }
    return offer.deactivate()
  }

}
