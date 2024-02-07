package com.mytiki.publish.client.ui.merchant

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.clo.merchant.MerchantEnum

class MerchantViewModel(): ViewModel() {
    fun offers(merchant: MerchantEnum) = TikiClient.clo.offers(merchant)

    fun webView(url:String, activity: ComponentActivity){
        val webIntent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        activity.startActivity(webIntent)
    }
}