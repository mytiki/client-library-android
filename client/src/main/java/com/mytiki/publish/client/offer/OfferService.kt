package com.mytiki.publish.client.offer

import android.content.Context
import com.mytiki.publish.client.TikiClient
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

class OfferService {

  fun accept(context: Context, offer: Offer): Offer {
    MainScope().async { TikiClient.license.create(context, offer.use, offer.tags) }
    return offer.activate()
  }

  fun decline(context: Context, offer: Offer): Offer {
    MainScope().async { TikiClient.license.create(context, null, offer.tags) }
    return offer.deactivate()
  }
}
