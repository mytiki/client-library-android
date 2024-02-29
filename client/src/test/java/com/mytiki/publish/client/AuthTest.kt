package com.mytiki.publish.client

import com.mytiki.publish.client.auth.AuthService
import com.mytiki.publish.client.auth.AuthToken
import com.mytiki.publish.client.email.EmailProviderEnum
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.security.KeyPair
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class AuthTest {
    @Test
    fun toStringFromStringTest(){
        val auth = AuthToken("email test","auth test", "refresh test", Date(1722345780000L), EmailProviderEnum.GOOGLE)

        val string = auth.toString()
        val newAuth = AuthToken.fromString(string, "email test")
        assert(auth.equals(newAuth))
    }

    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun test_Address_Passing(){
        val pubKey = "pub_key_test".toByteArray()
        val sha3 ="435e9c70afe599801e31cd05129cd139e2cc4204d9e4446d1b625be08afb3397".toByteArray()

        val keyPair = mockk<KeyPair>()
        every { keyPair.public.encoded } returns pubKey

        val addressSHA3 = AuthService().address(keyPair)?.let { Base64.UrlSafe.decode(it) }
        assert(sha3.contentEquals(addressSHA3))
    }
}
