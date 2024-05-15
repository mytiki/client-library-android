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
    MainScope().async { TikiClient.license.create(context, null, offer.tags) }
    return offer.deactivate()
  }

  /**
   * Updates the status of an offer. If the offer is active, it will be declined. If the offer is
   * inactive, it will be accepted. However, if the offer is not mutable, the status of the offer
   * will not be changed.
   *
   * @param context The context. This is typically the application context or the current activity.
   * @param offer The offer whose status is to be updated. This is an instance of the Offer class.
   * @return The updated offer. If the offer was mutable and active, the returned offer will be
   *   inactive, and vice versa. If the offer was not mutable, the same offer will be returned
   *   without any changes.
   */
  fun updateOfferStatus(context: Context, offer: Offer): Offer {
    return if (offer.active) decline(context, offer) else accept(context, offer)
  }
}
