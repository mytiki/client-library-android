package com.mytiki.publish.client.email

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.mytiki.publish.client.auth.AuthToken
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

import java.util.Date

class EmailRepositoryTest {
    private lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }
    @Test
    fun saveOneAuthToken() {
        val emailRepository = EmailRepository()
        val key = "test@gmail.com"
        val token = AuthToken(
            "a", "b", Date()
        )
        emailRepository.save(instrumentationContext, key, token)
        val returnedToken = emailRepository.get(instrumentationContext, key)
        assertEquals("a", returnedToken!!.auth)
    }

    @Test
    fun saveMultipleTokensAndRetrieve() {
        val emailRepository = EmailRepository()
        val key1 = "test1@gmail.com"
        val token1 = AuthToken("a", "b", Date())
        val key2 = "test2@gmail.com"
        val token2 = AuthToken("c", "d", Date())

        // Save multiple tokens
        emailRepository.save(instrumentationContext, key1, token1)
        emailRepository.save(instrumentationContext, key2, token2)

        // Retrieve and check if they match
        val returnedToken1 = emailRepository.get(instrumentationContext, key1)
        assertEquals("a", returnedToken1!!.auth)
        val returnedToken2 = emailRepository.get(instrumentationContext, key2)
        assertEquals("c", returnedToken2!!.auth)
    }

    @Test
    fun saveOneTokenAndRemove() {
        val emailRepository = EmailRepository()
        val key = "test@gmail.com"
        val token = AuthToken("a", "b", Date())

        // Save one token
        emailRepository.save(instrumentationContext, key, token)

        // Remove the token
        emailRepository.remove(instrumentationContext, key)

        // Try to retrieve the token and assert it is null
        val returnedToken = emailRepository.get(instrumentationContext, key)
        assertNull(returnedToken)
    }

    @Test
    fun saveMultipleTokensAndRemoveOne() {
        val emailRepository = EmailRepository()
        val key1 = "test1@gmail.com"
        val token1 = AuthToken("a", "b", Date())
        val key2 = "test2@gmail.com"
        val token2 = AuthToken("c", "d", Date())

        // Save multiple tokens
        emailRepository.save(instrumentationContext, key1, token1)
        emailRepository.save(instrumentationContext, key2, token2)

        // Remove one token
        emailRepository.remove(instrumentationContext, key1)

        // Try to retrieve the removed token and assert it is null
        val returnedToken1 = emailRepository.get(instrumentationContext, key1)
        assertNull(returnedToken1)

        // Check if the other token still exists
        val returnedToken2 = emailRepository.get(instrumentationContext, key2)
        assertEquals("c", returnedToken2!!.auth)
    }

    @Test
    fun saveMultipleTokensAndReturnAll() {
        val emailRepository = EmailRepository()
        val key1 = "test1@gmail.com"
        val token1 = AuthToken("a", "b", Date())
        val key2 = "test2@gmail.com"
        val token2 = AuthToken("c", "d", Date())

        // Save multiple tokens
        emailRepository.save(instrumentationContext, key1, token1)
        emailRepository.save(instrumentationContext, key2, token2)

        // Remove one token
        val accounts = emailRepository.accounts(instrumentationContext)
        assert(accounts.contains(key1))
        assert(accounts.contains(key2))
    }
}