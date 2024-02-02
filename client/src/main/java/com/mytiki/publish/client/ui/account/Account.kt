/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.account

import android.content.Context
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.email.EmailProviderEnum
import com.mytiki.publish.client.ui.Rewards
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import java.util.Date

/**
 * [Account] data class represents an account with a username, provider, and status.
 *
 * @property username The username associated with the account.
 * @property provider The [AccountProvider] of the account.
 * @property status The [AccountStatus] of the account.
 */
class Account private constructor(
    val username: String,
    val provider: EmailProviderEnum,
) {
    lateinit var status: AccountStatus
        private set

    constructor(context: Context, username: String, provider: EmailProviderEnum) : this(
        username,
        provider,
    ){
        MainScope().async {
            status = getStatus(context, username, provider)
        }
    }

    companion object {
        private suspend fun getStatus(context: Context, username: String, provider: EmailProviderEnum): AccountStatus {
            val token = TikiClient.email.emailRepository.get(context, username)
            return if (token != null) {
                if (token.expiration.after(Date())) {
                    AccountStatus.VERIFIED
                } else {
                    try {
                        TikiClient.auth.refresh(
                            context,
                            username,
                            if (provider == EmailProviderEnum.GOOGLE) Rewards.googleClientID!! else Rewards.outlookClientID!!
                        ).await()
                        AccountStatus.VERIFIED

                    } catch (error: Exception){
                        AccountStatus.UNVERIFIED
                    }
                }
            } else AccountStatus.UNVERIFIED
        }
    }

    /**
     * Checks if two [Account] objects are equal based on their username and provider.
     *
     * @param other The other [Account] to compare.
     * @return `true` if the accounts are equal, `false` otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Account

        if (username != other.username) return false
        if (provider != other.provider) return false
        return status == other.status
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + provider.hashCode()
        result = 31 * result + status.hashCode()
        return result
    }
}
