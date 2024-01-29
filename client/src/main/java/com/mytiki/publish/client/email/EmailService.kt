package com.mytiki.publish.client.email

import android.content.Context

class EmailService() {
    val emailRepository = EmailRepository()
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
    fun accounts(context: Context): Set<String>{
        return emailRepository.accounts(context)
    }

    /**
     * Removes a previously added email account.
     * @param email The email account to be removed.
     */
    fun logout(email: String){}
}