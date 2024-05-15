package com.mytiki.publish.client.offer

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.json.JSONObject

class OfferRepository {

  private var masterKey: MasterKey? = null
  private var sharedPreferences: SharedPreferences? = null
  private var editor: SharedPreferences.Editor? = null

  private fun check(context: Context) {
    if (masterKey == null) {
      masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }
    if (sharedPreferences == null) {
      sharedPreferences =
          EncryptedSharedPreferences.create(
              context,
              "auth_repository",
              masterKey!!,
              EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
              EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
      editor = sharedPreferences!!.edit()
    }
  }

  fun save(context: Context, offer: Offer): Boolean {
    check(context)
    val offerJson = offer.toJson().toString()
    return editor!!.putString(offer.ptr, offerJson).commit()
  }

  fun get(context: Context, ptr: String): Offer? {
    check(context)
    val offer = sharedPreferences!!.getString(ptr, null)?.let { JSONObject(it) }
    return if (offer != null) {
      Offer.fromJson(offer, ptr)
    } else {
      null
    }
  }

  fun getAll(context: Context): List<Offer> {
    check(context)
    val allEntries: MutableMap<String, *>? = sharedPreferences!!.all
    val offers = mutableListOf<Offer>()
    allEntries?.forEach { (key, value) ->
      if (value is String) {
        get(context, key)?.let { offers.add(it) }
      }
    }
    return offers
  }

  fun remove(context: Context, ptr: String) {
    check(context)
    editor!!.remove(ptr).commit()
  }
}
