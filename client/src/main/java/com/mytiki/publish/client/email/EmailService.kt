package com.mytiki.publish.client.email

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.email.messageResponse.Message
import com.mytiki.publish.client.email.messageResponse.MessagePart
import com.mytiki.publish.client.email.messageResponse.MessagePartBody
import com.mytiki.publish.client.email.messageResponse.MessageResponse
import com.mytiki.publish.client.utils.apiService.ApiService
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Service for managing email-related operations such as authentication, retrieval, and processing.
 */
class EmailService {

  val emailRepository = EmailRepository()

  internal var loginCallback: (String) -> Unit = {}
    private set

  /**
   * Authenticates with OAuth and adds an email account for scraping receipts.
   *
   * @param context The context.
   * @param provider The email provider (GOOGLE or OUTLOOK).
   * @param emailKeys The email authentication keys.
   * @param redirectURI The redirect URI.
   * @param loginCallback The callback function invoked after successful login.
   */
  fun login(
      context: Context,
      provider: EmailProviderEnum,
      emailKeys: EmailKeys,
      redirectURI: String,
      loginCallback: (String) -> Unit
  ) {
    this.loginCallback = loginCallback
    val intent = Intent(context, EmailActivity::class.java)

    intent.putExtra("provider", provider.toString())
    intent.putExtra("clientID", emailKeys.clientId)
    intent.putExtra("clientSecret", emailKeys.clientSecret)
    intent.putExtra("redirectURI", redirectURI)

    context.startActivity(intent)
  }

  /**
   * Retrieves the email response after authentication.
   *
   * @param provider The email provider (GOOGLE or OUTLOOK).
   * @param auth The authentication token.
   * @return A CompletableDeferred containing the email response.
   */
  fun getEmailResponse(
      provider: EmailProviderEnum,
      auth: String
  ): CompletableDeferred<EmailResponse> {
    val emailResponse = CompletableDeferred<EmailResponse>()
    MainScope().async {
      val response =
          ApiService.get(
                  mapOf("Authorization" to "Bearer $auth"),
                  provider.userInfoEndpoint,
                  Exception("error on user info request"))
              .await()
      emailResponse.complete(EmailResponse.fromJson(JSONObject(response?.string()!!)))
    }
    return emailResponse
  }

  /**
   * Retrieves the list of connected email accounts for the specified provider.
   *
   * @param context The context.
   * @param emailProvider The email provider (GOOGLE or OUTLOOK).
   * @return List of connected email accounts.
   */
  fun accountsPerProvider(context: Context, emailProvider: EmailProviderEnum): List<String> {
    return TikiClient.auth.repository.accounts(context, emailProvider)
  }

  /**
   * Retrieves the list of connected email accounts for all supported providers.
   *
   * @param context The context.
   * @return List of connected email accounts.
   */
  fun accounts(context: Context): List<String> {
    val emailList = mutableListOf<String>()
    EmailProviderEnum.entries.forEach { provider ->
      val list = TikiClient.email.accountsPerProvider(context, provider)
      if (list.isNotEmpty()) emailList.addAll(list)
    }
    return emailList
  }

  /**
   * Retrieves the list of messages for the specified email account.
   *
   * @param context The context.
   * @param email The email account.
   * @param nextPageToken The token for pagination.
   */
  fun messagesIndex(context: Context, email: String, nextPageToken: String? = null) {
    CoroutineScope(Dispatchers.IO).launch {
      val indexData = emailRepository.getData(context, email)
      val token =
          TikiClient.auth.repository.get(context, email)
              ?: throw Exception("this email is not logged")
      val provider =
          EmailProviderEnum.fromString(token.provider.toString())
              ?: throw Exception("Invalid provider")
      val auth = token.auth

      var isFirst = nextPageToken.isNullOrEmpty()
      var isWorking = true
      var endpoint =
          if (nextPageToken.isNullOrEmpty()) {
            if (indexData == null) provider.messagesIndexListEndpoint
            else
                provider.messagesIndexListEndpoint +
                    "&q=after:${indexData.date.year}/${indexData.date.month}/${indexData.date.day}"
          } else provider.messagesIndexListEndpoint + "&pageToken=$nextPageToken"

      while (isWorking) {
        val response =
            ApiService.get(
                    mapOf("Authorization" to "Bearer $auth"),
                    endpoint,
                    Exception("error on getting messagesIndex"))
                .await()
        val messageResponse = MessageResponse.fromJson(JSONObject(response?.string()!!))
        if (!messageResponse.messages.isNullOrEmpty()) {
          emailRepository.saveIndexes(context, email, messageResponse.messages)
          if (isFirst) {
            emailRepository.saveData(
                context, IndexData(email, Date(), messageResponse.nextPageToken))
          } else {
            emailRepository.updateNextPageToken(context, email, messageResponse.nextPageToken)
          }
          if (!messageResponse.nextPageToken.isNullOrEmpty()) {
            endpoint =
                provider.messagesIndexListEndpoint + "&pageToken=${messageResponse.nextPageToken}"
          } else {
            isWorking = false
          }
          isFirst = false
        }
      }
    }
  }

  /**
   * Checks for any pending indexes to be scraped.
   *
   * @param context The context.
   */
  fun checkIndexes(context: Context) {
    val accounts = accounts(context)
    accounts.forEach {
      val indexData = emailRepository.getData(context, it)
      if (!indexData?.nextPageToken.isNullOrEmpty()) {
        val token = TikiClient.auth.repository.get(context, it)
        if (token != null) {
          messagesIndex(context, token.username, indexData?.nextPageToken)
        }
      }
    }
  }

  /**
   * Initiates the process of scraping emails for receipts.
   *
   * @param context The context.
   * @param email The email account.
   * @param clientID The client ID.
   * @return A CompletableDeferred indicating the completion of the scraping process.
   */
  fun scrape(context: Context, email: String, clientID: String): CompletableDeferred<Boolean> {
    TikiClient.auth.emailAuthRefresh(context, email, clientID)
    val scrape = CompletableDeferred<Boolean>()
    CoroutineScope(Dispatchers.IO)
        .launch {
          var isWorking = true
          while (isWorking) {
            isWorking = scrapeInChunks(context, email, numberOfItems = 15).await()
          }
        }
        .invokeOnCompletion { scrape.complete(true) }
    return scrape
  }

  /**
   * Scrapes emails in chunks for processing.
   *
   * @param context The context.
   * @param email The email account.
   * @param numberOfItems The number of items to process in each chunk.
   * @return A CompletableDeferred indicating whether further scraping is required.
   */
  fun scrapeInChunks(
      context: Context,
      email: String,
      numberOfItems: Int
  ): CompletableDeferred<Boolean> {
    val token =
        TikiClient.auth.repository.get(context, email)
            ?: throw Exception("this email is not logged")
    val provider =
        EmailProviderEnum.fromString(token.provider.toString())
            ?: throw Exception("Invalid provider")
    val auth = token.auth
    val scrapeInChunks = CompletableDeferred<Boolean>()
    val indexes = emailRepository.readIndexes(context, email, numberOfItems)
    val messageRequestArray = mutableListOf<Deferred<ResponseBody?>>()
    val attachmentRequestArray = mutableListOf<Deferred<Unit>>()

    CoroutineScope(Dispatchers.IO).launch {
      indexes.forEachIndexed { index, messageID ->
        val apiRequest = async {
          ApiService.get(
                  mapOf("Authorization" to "Bearer $auth"),
                  provider.messageEndpoint(messageID),
                  Exception("error on getting messagesIndex"))
              .await()
        }
        messageRequestArray.add(apiRequest)
      }
      val messageResponseList = awaitAll(*messageRequestArray.toTypedArray())
      messageResponseList.forEach { apiResponse ->
        val attachmentDeferred = async {
          val message = (Message.fromJson(JSONObject(apiResponse?.string()!!)))
          val attachmentList = mutableListOf<Any>()
          decodeByMimiType(context, email, message, attachmentList).await()
          val published = TikiClient.capture.publish(message.toJson(), attachmentList)
          if (!published)
              scrapeInChunks.completeExceptionally(Exception("error updating index list"))
        }
        attachmentRequestArray.add(attachmentDeferred)
      }
      awaitAll(*attachmentRequestArray.toTypedArray())
      val removed = emailRepository.removeIndex(context, email, *indexes)
      if (!removed) scrapeInChunks.completeExceptionally(Exception("error updating index list"))
      scrapeInChunks.complete(numberOfItems >= messageRequestArray.size)
    }
    return scrapeInChunks
  }

  /**
   * Decodes MIME types of email message parts and adds them to the attachment list.
   *
   * @param context The context.
   * @param email The email account.
   * @param message The message to decode.
   * @param attachmentList The list to store decoded attachments.
   * @return A CompletableDeferred indicating completion.
   */
  private fun decodeByMimiType(
      context: Context,
      email: String,
      message: Message,
      attachmentList: MutableList<Any>
  ): CompletableDeferred<Unit> {
    val decodeByMimeType = CompletableDeferred<Unit>()
    CoroutineScope(Dispatchers.IO).launch {
      if (!message.payload?.mimeType.isNullOrEmpty()) {
        if (!message.payload?.body?.data.isNullOrEmpty()) {
          val text = message.payload?.let { getAllText(it).await() }
          if (!text.isNullOrEmpty()) attachmentList.add(text)

          val image = message.payload?.let { getAllImage(it).await() }
          if (image != null) attachmentList.add(image)

          val pdf = message.payload?.let { getAllPdf(context, it).await() }
          if (pdf != null) attachmentList.add(pdf)
        }

        val multipart = getAllMultipart(context, message, email).await()
        if (multipart != null) attachmentList.addAll(multipart)

        if (!message.payload?.body?.attachmentId.isNullOrEmpty()) {
          val attachment =
              getAttachments(context, email, message.id, message.payload?.body?.attachmentId!!)
                  .await()
          val byAttachmentId =
              message.payload.let { getAllByAttachmentId(context, it, attachment).await() }
          if (byAttachmentId != null) attachmentList.addAll(byAttachmentId)
        }
      }
      decodeByMimeType.complete(Unit)
    }
    return decodeByMimeType
  }

  /**
   * Retrieves and decodes MIME types of email message parts using attachment IDs.
   *
   * @param context The context.
   * @param messagePart The message part.
   * @param attachment The attachment data.
   * @return A CompletableDeferred containing the list of retrieved parts.
   */
  private fun getAllByAttachmentId(
      context: Context,
      messagePart: MessagePart,
      attachment: ByteArray
  ): CompletableDeferred<List<Any>?> {
    val getAllByAttachmentId = CompletableDeferred<List<Any>?>()
    CoroutineScope(Dispatchers.IO).launch {
      if (messagePart.mimeType?.substringBefore("/") == "multipart") {
        val array = mutableListOf<Any>()
        messagePart.parts?.forEach { part ->
          if (part != null && !part.body?.data.isNullOrEmpty()) {
            val text = getAllText(part, attachment).await()
            if (text != null) array.add(text)

            val image = getAllImage(part, attachment).await()
            if (image != null) array.add(image)

            val pdf = getAllPdf(context, part, attachment).await()
            if (pdf != null) array.add(pdf)
          }
        }
        getAllByAttachmentId.complete(array)
      } else getAllByAttachmentId.complete(null)
    }
    return getAllByAttachmentId
  }

  /**
   * Retrieves and decodes MIME types of multipart email message parts.
   *
   * @param context The context.
   * @param message The message.
   * @param email The email account.
   * @return A CompletableDeferred containing the list of retrieved parts.
   */
  private fun getAllMultipart(
      context: Context,
      message: Message,
      email: String
  ): CompletableDeferred<List<Any>?> {
    val getAllMultipart = CompletableDeferred<List<Any>?>()
    val messagePart = message.payload
    CoroutineScope(Dispatchers.IO).launch {
      if (messagePart?.mimeType?.substringBefore("/") == "multipart") {
        val list = mutableListOf<Any>()
        messagePart.parts?.forEach { part ->
          if (part != null) {
            if (!part.body?.data.isNullOrEmpty()) {
              val text = getAllText(part).await()
              if (text != null) list.add(text)

              val image = getAllImage(part).await()
              if (image != null) list.add(image)

              val pdf = getAllPdf(context, part).await()
              if (pdf != null) list.add(pdf)
            }
            if (!part.body?.attachmentId.isNullOrEmpty()) {
              val attachment =
                  getAttachments(context, email, message.id, part.body?.attachmentId!!).await()
              val byAttachmentId = getAllByAttachmentId(context, part, attachment).await()
              if (byAttachmentId != null) list.addAll(byAttachmentId)
            }
          }
        }
        getAllMultipart.complete(list)
      } else getAllMultipart.complete(null)
    }
    return getAllMultipart
  }

  /**
   * Retrieves and decodes image attachments from email message parts.
   *
   * @param messagePart The message part.
   * @param attachment Optional attachment data.
   * @return A CompletableDeferred containing the decoded bitmap.
   */
  @OptIn(ExperimentalEncodingApi::class)
  private fun getAllImage(
      messagePart: MessagePart,
      attachment: ByteArray? = null
  ): CompletableDeferred<Bitmap?> {
    val getAllImage = CompletableDeferred<Bitmap?>()
    CoroutineScope(Dispatchers.IO).launch {
      if (messagePart.mimeType?.substringBefore("/") == "image") {
        val att = attachment ?: Base64.UrlSafe.decode(messagePart.body?.data!!)
        val resp = BitmapFactory.decodeByteArray(att, 0, att.size)
        getAllImage.complete(resp)
      } else getAllImage.complete(null)
    }
    return getAllImage
  }

  /**
   * Retrieves and decodes text attachments from email message parts.
   *
   * @param messagePart The message part.
   * @param attachment Optional attachment data.
   * @return A CompletableDeferred containing the decoded text.
   */
  @OptIn(ExperimentalEncodingApi::class)
  private fun getAllText(
      messagePart: MessagePart,
      attachment: ByteArray? = null
  ): CompletableDeferred<String?> {
    val getAllText = CompletableDeferred<String?>()
    CoroutineScope(Dispatchers.IO).launch {
      if (messagePart.mimeType?.substringBefore("/") == "text") {
        val att = attachment ?: Base64.UrlSafe.decode(messagePart.body?.data!!)
        getAllText.complete(String(att))
      } else getAllText.complete(null)
    }
    return getAllText
  }

  /**
   * Retrieves and decodes PDF attachments from email message parts.
   *
   * @param context The context.
   * @param messagePart The message part.
   * @param attachment Optional attachment data.
   * @return A CompletableDeferred containing the decoded PDF file.
   */
  @OptIn(ExperimentalEncodingApi::class)
  private fun getAllPdf(
      context: Context,
      messagePart: MessagePart,
      attachment: ByteArray? = null
  ): CompletableDeferred<File?> {
    val getAllPdf = CompletableDeferred<File?>()
    CoroutineScope(Dispatchers.IO).launch {
      if (messagePart.mimeType == "application/pdf") {
        val att = attachment ?: Base64.UrlSafe.decode(messagePart.body?.data!!)
        val filePDF = File(context.filesDir, messagePart.body?.attachmentId + ".pdf")
        val pdfOutputStream = FileOutputStream(filePDF, false)
        pdfOutputStream.write(att)
        pdfOutputStream.flush()
        pdfOutputStream.close()
        getAllPdf.complete(filePDF)
      } else getAllPdf.complete(null)
    }
    return getAllPdf
  }

  /**
   * Retrieves attachments from email messages.
   *
   * @param context The context.
   * @param email The email account.
   * @param messageID The ID of the message.
   * @param attachmentID The ID of the attachment.
   * @return A CompletableDeferred containing the attachment data.
   */
  @OptIn(ExperimentalEncodingApi::class)
  private fun getAttachments(
      context: Context,
      email: String,
      messageID: String,
      attachmentID: String
  ): CompletableDeferred<ByteArray> {
    val getAttachments = CompletableDeferred<ByteArray>()
    val token =
        TikiClient.auth.repository.get(context, email)
            ?: throw Exception("this email is not logged")
    val provider =
        EmailProviderEnum.fromString(token.provider.toString())
            ?: throw Exception("Invalid provider")
    val auth = token.auth

    MainScope().async {
      val response =
          ApiService.get(
                  mapOf("Authorization" to "Bearer $auth"),
                  provider.attachmentEndpoint(messageID, attachmentID),
                  Exception("error on getting messagesIndex"))
              .await()
      val attachmentResponse = MessagePartBody.fromJson(JSONObject(response?.string()!!))
      val attachmentBytes = Base64.UrlSafe.decode(attachmentResponse.data!!)
      getAttachments.complete(attachmentBytes)
    }
    return getAttachments
  }

  /**
   * Removes a previously added email account.
   *
   * @param context The context.
   * @param email The email account to be removed.
   */
  fun logout(context: Context, email: String) {
    TikiClient.auth.repository.remove(context, email)
    TikiClient.email.emailRepository.deleteIndexes(context, email)
    TikiClient.email.emailRepository.removeData(context, email)
  }
}
