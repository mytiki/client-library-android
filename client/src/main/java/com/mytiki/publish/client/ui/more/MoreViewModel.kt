package com.mytiki.publish.client.ui.more

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.mytiki.publish.client.ProvidersInterface
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.clo.merchant.MerchantEnum
import com.mytiki.publish.client.email.EmailProviderEnum

class MoreViewModel(): ViewModel() {
    @RequiresApi(Build.VERSION_CODES.N)
    fun emailProvider(context: Context): Set<EmailProviderEnum> {
        val providerSet = mutableSetOf<EmailProviderEnum>()
        EmailProviderEnum.entries.forEach {
            if (TikiClient.email.accounts(context, it).isNotEmpty()) providerSet.add(it)
        }
        return providerSet.toSet()
    }

    fun largestContributors() = TikiClient.clo.largestContributors()

    fun declineLicense() {
        TikiClient.license.decline()
    }

}