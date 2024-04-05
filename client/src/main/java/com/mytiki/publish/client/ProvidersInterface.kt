package com.mytiki.publish.client

interface ProvidersInterface{


    fun displayName() = this.toString().replace("_", " ").lowercase().replaceFirstChar(Char::titlecase)
    fun resId(): Int
}
