package com.mytiki.publish.client.ui.email

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.ui.account.Account
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async

class EmailViewModel(): ViewModel() {
    private val _accounts = mutableStateOf(listOf<Account>())
    val accounts = _accounts



    @RequiresApi(Build.VERSION_CODES.N)
    fun updateAccounts(context: Context, emailProvider: EmailProviderEnum){
        val list = mutableListOf<Account>()
        TikiClient.email.accounts(context, emailProvider).forEach{username ->
            MainScope().async {
                val status = Account.getStatus(context, username, emailProvider).await()
                val account = Account(username, emailProvider, status)
                list.add(account)
                _accounts.value= list
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun login(context: Context, emailProvider: EmailProviderEnum){
        TikiClient.license.redirectUri?.let {
            TikiClient.email.login(
                context,
                emailProvider,
                TikiClient.email.googleKeys!!,
                it
            ){
                updateAccounts(context, emailProvider)
            }
        } ?: throw Exception("set the redirect uri")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun logout(context: Context, account: Account){
        TikiClient.email.logout(context, account.username)
        val list = _accounts.value.toMutableList()
        list.remove(account)
        _accounts.value = list
    }
}