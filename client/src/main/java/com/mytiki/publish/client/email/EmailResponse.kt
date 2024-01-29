package com.mytiki.publish.client.email

data class EmailResponse (
    val sub: String,
    val name: String,
    val given_name: String,
    val family_name: String,
    val picture: String,
    val email: String,
    val email_verified: Boolean,
    val locale: String,
)