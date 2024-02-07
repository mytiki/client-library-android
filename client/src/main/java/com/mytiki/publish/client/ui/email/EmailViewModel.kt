package com.mytiki.publish.client.ui.email

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.ui.account.Account
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class EmailViewModel(): ViewModel() {
    private val _accounts = MutableStateFlow(listOf<Account>())
    val accounts = _accounts.asStateFlow()



    @RequiresApi(Build.VERSION_CODES.N)
    fun updateAccounts(context: Context, emailProvider: EmailProviderEnum){
        val list = mutableListOf<Account>()
        TikiClient.email.accounts(context, emailProvider).forEach{
            list.add(Account(context, it, emailProvider))
        }
        _accounts.value = list
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun login(context: Context, emailProvider: EmailProviderEnum){
        TikiClient.license.redirectUri?.let {
            TikiClient.email.login(context, emailProvider, TikiClient.email.googleKeys!!,
                it
            )
        } ?: throw Exception("set the redirect uri")
        updateAccounts(context, emailProvider)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun logout(context: Context, username: String, emailProvider: EmailProviderEnum){
        TikiClient.email.logout(context, username)
        updateAccounts(context, emailProvider)
    }
}