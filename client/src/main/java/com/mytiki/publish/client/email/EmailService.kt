package com.mytiki.publish.client.email

import android.content.Context
import com.mytiki.publish.client.repository.EmailAccountRepository

class EmailService() {
    val emailAccountRepository = EmailAccountRepository()
    /**
     * Authenticates with OAuth and adds an email account for scraping receipts.
     * @param provider The email provider (GOOGLE or OUTLOOK).
     */
    fun login(provider: EmailProviderEnum){

    }

    /**
     * Retrieves the list of connected email accounts.
     * @return List of connected email accounts.
     */
    fun accounts(): List<String>{
        return listOf()
    }

    /**
     * Removes a previously added email account.
     * @param email The email account to be removed.
     */
    fun logout(email: String){}
}