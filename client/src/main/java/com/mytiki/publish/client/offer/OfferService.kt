package com.mytiki.publish.client.offer

import android.content.Context
import com.mytiki.publish.client.TikiClient
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

class OfferService {
  val repository = OfferRepository()

  fun accept(context: Context, offer: Offer) {
    MainScope().async { TikiClient.license.create(context, offer.use, offer.tags) }
  }

  fun decline(context: Context, offer: Offer) {
    MainScope().async { TikiClient.license.create(context, null, offer.tags) }
  }

  fun get(context: Context, ptr: String): Offer? {
    return repository.get(context, ptr)
  }

  fun getAll(context: Context): List<Offer> {
    return repository.getAll(context)
  }
}
