/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.ui.account

import android.content.Context
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.email.EmailProviderEnum
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import java.util.Date

/**
 * [Account] data class represents an account with a email, provider, and status.
 *
 * @property username The email associated with the account.
 * @property provider The [AccountProvider] of the account.
 * @property status The [AccountStatus] of the account.
 */
class Account(
    val username: String,
    val provider: EmailProviderEnum,
    val status: AccountStatus
) {

    companion object {
        fun getStatus(
            context: Context,
            username: String,
            provider: EmailProviderEnum,
        ): CompletableDeferred<AccountStatus> {
            val status = CompletableDeferred<AccountStatus>()
            val token = TikiClient.auth.authRepository.get(context, username)
            MainScope().async {
                if (token != null) {
                    if (token.expiration.after(Date())) {
                        status.complete(AccountStatus.VERIFIED)
                    } else {
                        try {
                            TikiClient.auth.refresh(
                                context,
                                username,
                                if (provider == EmailProviderEnum.GOOGLE) TikiClient.email.googleKeys!!.clientId else TikiClient.email.outlookKeys!!.clientId
                            ).await()
                            status.complete(AccountStatus.VERIFIED)

                        } catch (error: Exception) {
                            status.complete(AccountStatus.UNVERIFIED)
                        }
                    }
                } else status.complete(AccountStatus.UNVERIFIED)
            }
            return status
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Account

        if (username != other.username) return false
        return provider == other.provider
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + provider.hashCode()
        return result
    }
}
