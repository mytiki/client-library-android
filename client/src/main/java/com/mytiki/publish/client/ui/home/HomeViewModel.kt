package com.mytiki.publish.client.ui.home

import androidx.lifecycle.ViewModel
import com.mytiki.publish.client.ProvidersInterface
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.clo.merchant.MerchantEnum
import com.mytiki.publish.client.email.EmailProviderEnum

class HomeViewModel(): ViewModel() {
    fun providers(): Set<ProvidersInterface> {
        val providersList = mutableListOf<ProvidersInterface>()
        providersList.addAll(EmailProviderEnum.entries.toList())
        providersList.addAll(MerchantEnum.entries.toList())
        return providersList.toSet()
    }

    fun earnings() = TikiClient.license.earnings()

}