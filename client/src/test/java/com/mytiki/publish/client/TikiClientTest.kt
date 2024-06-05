package com.mytiki.publish.client

import android.content.Context
import androidx.activity.ComponentActivity
import com.mytiki.publish.client.config.Config
import com.mytiki.publish.client.email.EmailAttachment
import com.mytiki.publish.client.offer.*
import com.mytiki.publish.client.permission.Permission
import io.mockk.*
import java.io.File
import junit.framework.TestCase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MainCoroutineRule(val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()) :
    TestRule {

  @OptIn(ExperimentalCoroutinesApi::class)
  override fun apply(base: Statement, description: Description) =
      object : Statement() {
        override fun evaluate() {
          Dispatchers.setMain(dispatcher)
          base.evaluate()
          Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
          dispatcher.cleanupTestCoroutines()
        }
      }
}

class TikiClientTest {
  @get:Rule var mainCoroutineRule = MainCoroutineRule()

  private val mockConfig =
      Config(
          providerId = "validProviderID",
          publicKey = "validPublicKey",
          companyName = "validCompanyName",
          companyJurisdiction = "validCompanyJurisdiction",
          tosUrl = "validTosUrl",
          privacyUrl = "validPrivacyUrl")
  private val mockActivity = mockk<ComponentActivity>()
  private val mockEmailAttachmentArray = arrayOf(mockk<EmailAttachment>())
  private val mockContext = mockk<Context>()
  private val userID = "validUserID"
  private val offer =
      Offer.Builder()
          .description("description")
          .rewards(
              listOf(
                  OfferReward("description", mockk<OfferRewardType>(), "amount"),
              ))
          .use(listOf(OfferUse(listOf(OfferUsecase.ATTRIBUTION), listOf("*"))))
          .tags(listOf(OfferTag.PURCHASE_HISTORY))
          .ptr("ptr")
          .permissions(listOf(Permission.CAMERA))
          .build()

  @After
  fun teardown() {
    clearAllMocks()
    TikiClient.userID = null
    TikiClient.config = null
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun initializeSuccessfully() {
    TikiClient.configure(mockConfig)
    clearAllMocks()

    mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

    assertEquals(userID, TikiClient.userID)
  }

  @Test
  fun initializeWithoutConfig() {
    try {
      TikiClient.initialize(userID)
      fail("Expected an Exception to be thrown")
    } catch (e: Exception) {
      assertEquals(
          "TIKI Client is not configured. OfferUse the TikiClient.configure method to add a configuration.",
          e.message)
    }
  }

  @Test
  fun scanSuccessfully() {
    TikiClient.configure(mockConfig)

    clearAllMocks()
    mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

    every { mockActivity.startActivity(any()) } just Runs

    TikiClient.scan(mockActivity) {}
  }

  @Test
  fun scanWithoutInitialization() {
    try {
      TikiClient.configure(mockConfig)
      TikiClient.scan(mockActivity) {}
      fail("Expected an Exception to be thrown")
    } catch (e: Exception) {
      assertEquals(
          "User ID cannot be empty. OfferUse the TikiClient.initialize method to set the user ID.",
          e.message)
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun publishWithAttachmentArray() {
    TikiClient.configure(mockConfig)
    clearAllMocks()

    every { mockk<EmailAttachment>().toPdf(mockActivity) } returns mockk<File>()

    mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }
    mainCoroutineRule.dispatcher.runBlockingTest {
      TikiClient.publish(mockActivity, mockEmailAttachmentArray)
    }
  }

  @Test
  fun createLicenseSuccessfully() {
    TikiClient.configure(mockConfig)
    clearAllMocks()
    mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

    mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.createLicense(mockActivity, offer) }
  }

  @Test
  fun createLicenseWithoutInitialization() {
    try {
      TikiClient.configure(mockConfig)
      mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.createLicense(mockActivity, offer) }
    } catch (e: Exception) {
      assertEquals(
          "User ID cannot be empty. OfferUse the TikiClient.initialize method to set the user ID.",
          e.message)
    }
  }

  @Test
  fun acceptOfferSuccessfully() {
    TikiClient.configure(mockConfig)
    clearAllMocks()
    mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

    mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.acceptOffer(mockActivity, offer) }
  }

  @Test
  fun declineOfferSuccessfully() {
    TikiClient.configure(mockConfig)
    clearAllMocks()
    mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.initialize(userID) }

    mainCoroutineRule.dispatcher.runBlockingTest { TikiClient.declineOffer(mockActivity, offer) }
  }

  @Test
  fun retrieveLicenseTermsWithoutInitialization() {
    try {
      TikiClient.configure(mockConfig)
      TikiClient.terms(mockContext)
    } catch (e: Exception) {
      assertEquals(
          "User ID cannot be empty. OfferUse the TikiClient.initialize method to set the user ID.",
          e.message)
    }
  }
}
