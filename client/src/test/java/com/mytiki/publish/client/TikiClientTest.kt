import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import android.content.Context
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.config.Config
import io.mockk.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.fail
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
import java.security.KeyPair

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

    private val mockConfig = mockk<Config>()
    private val mockActivity = mockk<ComponentActivity>()
    private val mockBitmap = mockk<Bitmap>()
    private val mockContext = mockk<Context>()

    @Before
    fun setup() {
        clearAllMocks()
        TikiClient.configure(mockConfig)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun initializeSuccessfully() {
        val userID = "validUserID"
        clearAllMocks()

        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

        assertEquals(userID, TikiClient.userID)
    }

    @Test
    fun scanSuccessfully() {
        val userID = "validUserID"
        clearAllMocks()
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

        every { mockActivity.startActivity(any()) } just Runs

        TikiClient.scan(mockActivity) {}
    }

  @Test
fun scanWithoutInitialization() {
    try {
        TikiClient.scan(mockActivity) {}
        fail("Expected an Exception to be thrown")
    } catch (e: Exception) {
        assertEquals("TIKI Client is not configured. Use the TikiClient.configure method to add a configuration.", e.message)
    }
}

    @Test
    fun publishSingleBitmapSuccessfully() {
        val userID = "validUserID"
        clearAllMocks()
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.publish(mockBitmap) }
    }

    @Test(expected = Exception::class)
    fun publishSingleBitmapWithoutInitialization() {
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.publish(mockBitmap) }
    }

    @Test
    fun publishArrayOfBitmapsSuccessfully() {
        val userID = "validUserID"
        val bitmapArray = arrayOf(mockBitmap)
        clearAllMocks()
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.publish(bitmapArray) }
    }

    @Test(expected = Exception::class)
    fun publishArrayOfBitmapsWithoutInitialization() {
        val bitmapArray = arrayOf(mockBitmap)

        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.publish(bitmapArray) }
    }

    @Test
    fun createLicenseSuccessfully() {
        val userID = "validUserID"
        clearAllMocks()
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.createLicense(mockActivity) }
    }

    @Test(expected = Exception::class)
    fun createLicenseWithoutInitialization() {
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.createLicense(mockActivity) }
    }

    @Test
    fun retrieveLicenseTermsSuccessfully() {
        val userID = "validUserID"
        clearAllMocks()
        mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

        TikiClient.terms(mockContext)
    }

    @Test(expected = Exception::class)
    fun retrieveLicenseTermsWithoutInitialization() {
        TikiClient.terms(mockContext)
    }
}
