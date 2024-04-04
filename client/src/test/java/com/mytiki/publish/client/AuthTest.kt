package com.mytiki.publish.client

import com.mytiki.publish.client.auth.AuthService
import com.mytiki.publish.client.email.EmailProviderEnum
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import java.security.KeyPair
import java.util.*

class AuthTest {
    @Test
    fun toStringFromStringTest(){
        val auth = AuthToken("email test","auth test", "refresh test", Date(1722345780000L), EmailProviderEnum.GOOGLE)

        val string = auth.toString()
        val newAuth = AuthToken.fromString(string, "email test")
        assert(auth.equals(newAuth))
    }

    @Test
    fun test_Address_Passing(){
        val mockPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5+WkJoKVztWrBdsbbpYdU004laU5u7rTyi5ktiQSwfpus1ug5hb8jm8jreb9RcQuMMO9by0C8Lto0iLXoTg6BvFVjnib5n9h3g35b8G1UCjfYOvU1Dgayy6JxFx4F7R+wxTyzkImCdSo/hl1WaC2yHM/69FKB/FEabiLzi8GLrTW2smRQ0d704zmw3g0qy0n5iI9M7LJXbHJaGVJUyvyqeDWJ34PbpJ/8FRvEVuuCd2bFa//Rm1AMrVopU6qKoWz5CF29PobA59b9PpggxoZpNbeJaRtqYdAKQd85VvJcaxMAAK4/vuoPVCj7ROaPAZ6wsrDiF/afb9x7JNwiON9jQIDAQAB".toByteArray()
        val mockAddress = "OLpxy2iCCmfGVrLr2BHKAsoE6cDFXMOm6tOpZEJAz0I"
        val keyPair = mockk<KeyPair>()
        every { keyPair.public.encoded } returns mockPubKey

        val address = AuthService().address(keyPair)
        assert(address == mockAddress)
    }
}
