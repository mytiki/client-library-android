package com.mytiki.publish.client.email

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.mytiki.publish.client.auth.AuthToken
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

import java.util.Date

class EmailRepositoryTest {
    private lateinit var instrumentationContext: Context

    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().context
    }
    @Test
    fun shouldReturnBlankForNonExistentSetting() {
        val emailRepository = EmailRepository()
        val key = "test@gmail.com"
        val token = AuthToken(
            "a", "b", Date()
        )
        emailRepository.saveToken(instrumentationContext, key, token)
        val returnedToken = emailRepository.getToken(instrumentationContext, key)
        assertEquals("a", returnedToken!!.auth)
    }
}