package com.mytiki.publish.client.auth

import java.util.Date

data class AuthToken (
    val auth: String,
    val refresh: String,
    val expiration: Date
)