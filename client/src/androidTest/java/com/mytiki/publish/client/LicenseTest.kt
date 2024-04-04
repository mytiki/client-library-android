package com.mytiki.publish.client

import android.content.Context
import android.content.ContextParams
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mytiki.publish.client.license.LicenseService
import com.mytiki.tiki_sdk_android.trail.LicenseRecord
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LicenseTest {

    private lateinit var context: Context
    private val service = LicenseService()

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }
    @Test
    fun test_verify_passing() = runTest {
        val license = service.create(context, "Test UserID", "Test ProviderID", "Test Terms")
        val getLicense = service.get()
        assert(license.id == getLicense?.id)
    }

    @Test
    fun verify() = runTest {
        service.create(context, "Test UserID", "Test ProviderID", "Test Terms")
        assert(service.verify("Test UserID"))
    }
}
