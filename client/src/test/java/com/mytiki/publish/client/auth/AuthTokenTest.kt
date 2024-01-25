package com.mytiki.publish.client.auth

import org.junit.Test
import java.util.Date

class AuthTokenTest {
    @Test
    fun toStringFromStringTest(){
        val auth = AuthToken("auth test", "refresh test", Date(1722345780000L))

        val string = auth.toString()
        val newAuth = AuthToken.fromString(string)
        assert(auth.equals(newAuth))
    }
}