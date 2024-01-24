package com.mytiki.publish.client.auth

import java.util.Date

class AuthToken private constructor(val auth: String, val refresh: String, expiration: Date){
    val exp = expiration.time
}