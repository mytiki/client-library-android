package com.mytiki.publish.client.email

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.mytiki.publish.client.auth.AuthToken
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

import java.util.Date

class AuthRepositoryTest {
    private lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }
//    @Test
//    fun saveOneAuthToken() {
//        val authRepository = AuthRepository()
//        val key = "test@gmail.com"
//        val token = AuthToken(
//            "test@gmail.com",
//            "a", "b", Date()
//        )
//        authRepository.saveIndexes(instrumentationContext, key, token)
//        val returnedToken = authRepository.get(instrumentationContext, key)
//        assertEquals("a", returnedToken!!.auth)
//    }
//
//    @Test
//    fun saveMultipleTokensAndRetrieve() {
//        val authRepository = AuthRepository()
//        val key1 = "test1@gmail.com"
//        val token1 = AuthToken("a", "b", Date())
//        val key2 = "test2@gmail.com"
//        val token2 = AuthToken("c", "d", Date())
//
//        // Save multiple tokens
//        authRepository.saveIndexes(instrumentationContext, key1, token1)
//        authRepository.saveIndexes(instrumentationContext, key2, token2)
//
//        // Retrieve and checkIndexes if they match
//        val returnedToken1 = authRepository.get(instrumentationContext, key1)
//        assertEquals("a", returnedToken1!!.auth)
//        val returnedToken2 = authRepository.get(instrumentationContext, key2)
//        assertEquals("c", returnedToken2!!.auth)
//    }
//
//    @Test
//    fun saveOneTokenAndRemove() {
//        val authRepository = AuthRepository()
//        val key = "test@gmail.com"
//        val token = AuthToken("a", "b", Date())
//
//        // Save one token
//        authRepository.saveIndexes(instrumentationContext, key, token)
//
//        // Remove the token
//        authRepository.remove(instrumentationContext, key)
//
//        // Try to retrieve the token and assert it is null
//        val returnedToken = authRepository.get(instrumentationContext, key)
//        assertNull(returnedToken)
//    }
//
//    @Test
//    fun saveMultipleTokensAndRemoveOne() {
//        val authRepository = AuthRepository()
//        val key1 = "test1@gmail.com"
//        val token1 = AuthToken("a", "b", Date())
//        val key2 = "test2@gmail.com"
//        val token2 = AuthToken("c", "d", Date())
//
//        // Save multiple tokens
//        authRepository.saveIndexes(instrumentationContext, key1, token1)
//        authRepository.saveIndexes(instrumentationContext, key2, token2)
//
//        // Remove one token
//        authRepository.remove(instrumentationContext, key1)
//
//        // Try to retrieve the removed token and assert it is null
//        val returnedToken1 = authRepository.get(instrumentationContext, key1)
//        assertNull(returnedToken1)
//
//        // Check if the other token still exists
//        val returnedToken2 = authRepository.get(instrumentationContext, key2)
//        assertEquals("c", returnedToken2!!.auth)
//    }
//
//    @Test
//    fun saveMultipleTokensAndReturnAll() {
//        val authRepository = AuthRepository()
//        val key1 = "test1@gmail.com"
//        val token1 = AuthToken("a", "b", Date())
//        val key2 = "test2@gmail.com"
//        val token2 = AuthToken("c", "d", Date())
//
//        // Save multiple tokens
//        authRepository.saveIndexes(instrumentationContext, key1, token1)
//        authRepository.saveIndexes(instrumentationContext, key2, token2)
//
//        // Remove one token
//        val accountsPerProvider = authRepository.accountsPerProvider(instrumentationContext)
//        assert(accountsPerProvider.contains(key1))
//        assert(accountsPerProvider.contains(key2))
//    }
}