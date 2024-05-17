package com.mytiki.publish.client.offer

import android.content.Context
import com.mytiki.publish.client.TikiClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

/** This class provides services related to offers. */
class OfferService {

  /**
   * Accepts an offer.
   *
   * This function accepts an offer. It uses the provided Context instance and an Offer object. The
   * function calls the create method of the TikiClient's license instance asynchronously and
   * completes the CompletableDeferred object with the result.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for the offer
   *   acceptance process.
   * @param offer The Offer object containing the details of the offer to be accepted.
   * @return A CompletableDeferred object that will be completed when the offer has been accepted.
   *   The CompletableDeferred object contains a Boolean indicating the success of the offer
   *   acceptance process. It returns true if the offer was successfully accepted, false otherwise.
   */
  fun accept(context: Context, offer: Offer): CompletableDeferred<Boolean> {
    val isAccepted = CompletableDeferred<Boolean>()
    MainScope().async { isAccepted.complete(TikiClient.license.create(context, offer)) }
    return isAccepted
  }

  /**
   * Declines an offer.
   *
   * This function declines an offer. It uses the provided Context instance and an Offer object. The
   * function calls the revoke method of the TikiClient's license instance asynchronously and
   * completes the CompletableDeferred object with the result.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for the offer
   *   decline process.
   * @param offer The Offer object containing the details of the offer to be declined.
   * @return A CompletableDeferred object that will be completed when the offer has been declined.
   *   The CompletableDeferred object contains a Boolean indicating the success of the offer decline
   *   process. It returns true if the offer was successfully declined, false otherwise.
   */
  fun decline(context: Context, offer: Offer): CompletableDeferred<Boolean> {
    val isAccepted = CompletableDeferred<Boolean>()
    MainScope().async { isAccepted.complete(TikiClient.license.revoke(context, offer)) }
    return isAccepted
  }

  /**
   * Checks if an offer is accepted.
   *
   * This function checks if an offer is accepted. It uses the provided string pointer which
   * represents the license record. The function calls the verify method of the TikiClient's license
   * instance asynchronously and completes the CompletableDeferred object with the result.
   *
   * @param ptr The string pointer representing the license record.
   * @return A CompletableDeferred object that will be completed when the verification process has
   *   been done. The CompletableDeferred object contains a Boolean indicating the acceptance status
   *   of the offer. It returns true if the offer was accepted, false otherwise.
   */
  fun isAccepted(ptr: String): CompletableDeferred<Boolean> {
    val isAccepted = CompletableDeferred<Boolean>()
    MainScope().async { isAccepted.complete(TikiClient.license.verify()) }
    return isAccepted
  }
}
