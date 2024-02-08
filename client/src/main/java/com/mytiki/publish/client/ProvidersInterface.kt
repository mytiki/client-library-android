package com.mytiki.publish.client

import com.mytiki.publish.client.email.EmailProviderEnum

interface ProvidersInterface{


    fun displayName() = this.toString().replace("_", " ").lowercase().replaceFirstChar(Char::titlecase)
    fun resId(): Int
}