import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import android.content.Context
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.config.Config
import com.mytiki.publish.client.terms
import io.mockk.*
import junit.framework.TestCase.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.After
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MainCoroutineRule(val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()): TestRule {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun apply(base: Statement, description: Description) = object : Statement() {
        override fun evaluate() {
            Dispatchers.setMain(dispatcher)
            base.evaluate()
            Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
            dispatcher.cleanupTestCoroutines()
        }
    }
}

class TikiClientTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private val mockConfig = Config(
        providerId = "validProviderID",
        publicKey = "validPublicKey",
        companyName = "validCompanyName",
        companyJurisdiction = "validCompanyJurisdiction",
        tosUrl = "validTosUrl",
        privacyUrl = "validPrivacyUrl"
    )
    private val mockActivity = mockk<ComponentActivity>()
    private val mockBitmap = mockk<Bitmap>()
    private val mockContext = mockk<Context>()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initializeSuccessfully() {
        clearAllMocks()
        TikiClient.configure(mockConfig)
        val userID = "validUserID"
        clearAllMocks()

        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

        assertEquals(userID, TikiClient.userID)
    }

    @Test
    fun initializeWithoutConfig() {
        try {
            val userID = "validUserID"
            clearAllMocks()
            TikiClient.initialize(userID)
            fail("Expected an Exception to be thrown")
        } catch (e: Exception) {
            assertEquals("TIKI Client is not configured. Use the TikiClient.configure method to add a configuration.", e.message)
        }
    }

    @Test
    fun scanSuccessfully() {
        clearAllMocks()
        TikiClient.configure(mockConfig)

        val userID = "validUserID"
        clearAllMocks()
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

        every { mockActivity.startActivity(any()) } just Runs

        TikiClient.scan(mockActivity) {}
    }

  @Test
fun scanWithoutInitialization() {
    try {
        clearAllMocks()
        TikiClient.configure(mockConfig)
        TikiClient.scan(mockActivity) {}
        fail("Expected an Exception to be thrown")
    } catch (e: Exception) {
        assertEquals("User ID cannot be empty. Use the TikiClient.initialize method to set the user ID.", e.message)
    }
}

    @Test
    fun publishSingleBitmapSuccessfully() {
        clearAllMocks()
        TikiClient.configure(mockConfig)
        val userID = "validUserID"
        clearAllMocks()
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.publish(mockBitmap) }
    }

    @Test
    fun publishSingleBitmapWithoutInitialization() {
        try{
            clearAllMocks()
            TikiClient.configure(mockConfig)
            mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.publish(mockBitmap) }
        } catch (e: Exception) {
            assertEquals("User ID cannot be empty. Use the TikiClient.initialize method to set the user ID.", e.message)
        }
    }

    @Test
    fun publishArrayOfBitmapsSuccessfully() {
        clearAllMocks()
        TikiClient.configure(mockConfig)
        val userID = "validUserID"
        val bitmapArray = arrayOf(mockBitmap)
        clearAllMocks()
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.publish(bitmapArray) }
    }

    @Test
    fun publishArrayOfBitmapsWithoutInitialization() {
        try{
            clearAllMocks()
            TikiClient.configure(mockConfig)
            val bitmapArray = arrayOf(mockBitmap)
            mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.publish(bitmapArray) }

        } catch (e: Exception) {
            assertEquals("User ID cannot be empty. Use the TikiClient.initialize method to set the user ID.", e.message)
        }
    }

    @Test
    fun createLicenseSuccessfully() {
        clearAllMocks()
        TikiClient.configure(mockConfig)
        val userID = "validUserID"
        clearAllMocks()
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.createLicense(mockActivity) }
    }

    @Test
    fun createLicenseWithoutInitialization() {
        try{
            clearAllMocks()
            TikiClient.configure(mockConfig)
            mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.createLicense(mockActivity) }
        } catch (e: Exception) {
            assertEquals("User ID cannot be empty. Use the TikiClient.initialize method to set the user ID.", e.message)
        }
    }

    @Test
    fun retrieveLicenseTermsWithoutInitialization() {
        try{
            clearAllMocks()
            TikiClient.configure(mockConfig)
            TikiClient.terms(mockContext)
        } catch (e: Exception) {
            assertEquals("User ID cannot be empty. Use the TikiClient.initialize method to set the user ID.", e.message)
        }
    }
}
