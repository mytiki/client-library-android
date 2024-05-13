package com.mytiki.publish.client.email

import android.content.Context
import android.content.Intent
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.email.messageResponse.Message
import com.mytiki.publish.client.email.messageResponse.MessagePart
import com.mytiki.publish.client.email.messageResponse.MessagePartBody
import com.mytiki.publish.client.email.messageResponse.MessageResponse
import com.mytiki.publish.client.utils.apiService.ApiService
import java.time.LocalDateTime
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import org.json.JSONObject

/**
 * Service for managing email-related operations such as authentication, retrieval, and processing.
 */
class EmailService {

  /** Repository for managing email data. */
  val repository = EmailRepository()

  /** Callback function for handling login events. */
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
      loginCallback: (String) -> Unit
  ) {
    this.loginCallback = loginCallback
    val intent = Intent(context, EmailActivity::class.java)

    intent.putExtra("provider", provider.toString())
    intent.putExtra("clientID", emailKeys.clientId)
    intent.putExtra("clientSecret", emailKeys.clientSecret)
    intent.putExtra("redirectURI", emailKeys.redirectUri)

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

  fun scrape(context: Context, email: String) {
    MainScope().async {
      TikiClient.auth.emailToken(context, email).await()

      var indexData = repository.getData(context, email)

      if (indexData != null && indexData.lastDate == null) {
        repository.updateData(
            context,
            IndexData(
                email, LocalDateTime.now(), indexData.historicDate, indexData.downloadInProgress))
        messagesIndex(context, email, before = LocalDateTime.now()).await()
        indexData = repository.getData(context, email)
      }

      downloadEmails(context, email) { attachmentArray ->
        TikiClient.capture.publish(context, attachmentArray)
      }

      if (indexData?.lastDate != null &&
          indexData.lastDate!!.isBefore(LocalDateTime.now().minusHours(6L))) {
        messagesIndex(context, email, after = indexData.lastDate).await()
        indexData = repository.getData(context, email)
      }
      messagesIndex(context, email, before = indexData!!.historicDate).await()
    }
  }

  /**
   * Retrieves the list of messages for the specified email account.
   *
   * @param context The context.
   * @param email The email account.
   */
  fun messagesIndex(
      context: Context,
      email: String,
      after: LocalDateTime? = null,
      before: LocalDateTime? = null
  ): CompletableDeferred<Unit> {
    val messagesIndex = CompletableDeferred<Unit>()
    val indexRequestArray = mutableListOf<Deferred<ResponseBody?>>()
    if (after == null && before == null) {
      throw Exception("You must provide one date to filter messages")
    }
    if (after != null && before != null) {
      throw Exception("You must provide only one date to filter messages")
    }
    CoroutineScope(Dispatchers.IO).launch {
      val indexData = repository.getData(context, email)
      val token = TikiClient.auth.emailToken(context, email).await()
      val provider =
          EmailProviderEnum.fromString(token.provider.toString())
              ?: throw Exception("Invalid provider")
      val auth = token.auth

      val endpoint = provider.messagesIndexListEndpoint
      val queryList = Sender.toQuery(after, before)
      queryList.forEach { query ->
        val response = async {
          ApiService.get(
                  mapOf("Authorization" to "Bearer $auth"),
                  endpoint + query,
                  Exception("error on getting messagesIndex"))
              .await()
        }
        indexRequestArray.add(response)
      }
      val responseList = awaitAll(*indexRequestArray.toTypedArray())
      responseList.forEach() { response ->
        val messageResponse = MessageResponse.fromJson(JSONObject(response?.string()!!))
        if (!messageResponse.messages.isNullOrEmpty()) {
          repository.saveIndexes(context, email, messageResponse.messages, append = before != null)
          if (before != null) {
            val lastIndex =
                messageResponse.messages.last() ?: throw Exception("error on getting messagesIndex")
            val apiResponse =
                ApiService.get(
                        mapOf("Authorization" to "Bearer $auth"),
                        provider.messageEndpoint(lastIndex.id),
                        Exception("error on getting messagesIndex"))
                    .await()
            val message = Message.fromJson(JSONObject(apiResponse?.string()!!))
            if (message.internalDate != null && indexData != null) {
              if (indexData.historicDate == null ||
                  indexData.historicDate.isBefore(message.internalDate)) {
                val data =
                    IndexData(
                        email,
                        indexData.lastDate,
                        message.internalDate,
                        indexData.downloadInProgress)
                repository.updateData(context, data)
              }
              messagesIndex.complete(Unit)
            } else messagesIndex.complete(Unit)
          } else messagesIndex.complete(Unit)
        }
      }
    }
    return messagesIndex
  }

  /**
   * Initiates the process of scraping emails for receipts.
   *
   * @param context The context.
   * @param email The email account.
   * @param clientID The client ID.
   * @return A CompletableDeferred indicating the completion of the scraping process.
   */
  fun downloadEmails(
      context: Context,
      email: String,
      onDownload: (attachmentList: Array<Attachment>) -> CompletableDeferred<Unit>
  ): CompletableDeferred<Unit> {
    val scrape = CompletableDeferred<Unit>()
    val indexData = repository.getData(context, email)
    if (indexData!!.downloadInProgress) {
      scrape.complete(Unit)
      return scrape
    } else {
      val data = IndexData(email, indexData.lastDate, indexData.historicDate, true)
      repository.updateData(context, data)
      CoroutineScope(Dispatchers.IO)
          .launch {
            var isWorking = true
            while (isWorking) {
              isWorking =
                  downloadEmailsInChunks(context, email, numberOfItems = 10, onDownload).await()
            }
          }
          .invokeOnCompletion {
            repository.updateData(
                context, IndexData(email, indexData.lastDate, indexData.historicDate, false))
            scrape.complete(Unit)
          }
      return scrape
    }
  }

  /**
   * Scrapes emails in chunks for processing.
   *
   * @param context The context.
   * @param email The email account.
   * @param numberOfItems The number of items to process in each chunk.
   * @return A CompletableDeferred indicating whether further scraping is required.
   */
  fun downloadEmailsInChunks(
      context: Context,
      email: String,
      numberOfItems: Int,
      onDownload: (attachmentList: Array<Attachment>) -> CompletableDeferred<Unit>
  ): CompletableDeferred<Boolean> {
    val scrapeInChunks = CompletableDeferred<Boolean>()
    val indexes = repository.readIndexes(context, email, numberOfItems)
    val messageRequestArray = mutableListOf<Deferred<ResponseBody?>>()
    val attachmentRequestArray = mutableListOf<Deferred<Unit>>()

    CoroutineScope(Dispatchers.IO).launch {
      val token = TikiClient.auth.emailToken(context, email).await()
      val provider =
          EmailProviderEnum.fromString(token.provider.toString())
              ?: throw Exception("Invalid provider")
      val auth = token.auth

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
          val attachmentList = mutableListOf<Attachment>()
          decodeByMimiType(context, email, message, attachmentList).await()
          onDownload(attachmentList.toTypedArray()).await()
        }
        attachmentRequestArray.add(attachmentDeferred)
      }
      awaitAll(*attachmentRequestArray.toTypedArray())
      val removed = repository.removeIndex(context, email, *indexes)
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
      attachmentList: MutableList<Attachment>
  ): CompletableDeferred<Unit> {
    val decodeByMimeType = CompletableDeferred<Unit>()
    CoroutineScope(Dispatchers.IO).launch {
      if (!message.payload?.mimeType.isNullOrEmpty()) {
        if (!message.payload?.body?.data.isNullOrEmpty()) {
          val att = message.payload?.let { sortAttachment(it).await() }
          if (att != null) attachmentList.add(att)
        }

        val multipart = getAllMultipart(context, message, email).await()
        if (multipart != null) attachmentList.addAll(multipart)

        if (!message.payload?.body?.attachmentId.isNullOrEmpty()) {
          val attachment =
              getAttachments(context, email, message.id, message.payload?.body?.attachmentId!!)
                  .await()
          val byAttachmentId = message.payload.let { getAllByAttachmentId(it, attachment).await() }
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
   * @param messagePart The message part.
   * @param attachment The attachment data.
   * @return A CompletableDeferred containing the list of retrieved parts.
   */
  private fun getAllByAttachmentId(
      messagePart: MessagePart,
      attachment: ByteArray
  ): CompletableDeferred<List<Attachment>?> {
    val getAllByAttachmentId = CompletableDeferred<List<Attachment>?>()
    CoroutineScope(Dispatchers.IO).launch {
      if (messagePart.mimeType?.substringBefore("/") == "multipart") {
        val list = mutableListOf<Attachment>()
        messagePart.parts?.forEach { part ->
          if (part != null && !part.body?.data.isNullOrEmpty()) {
            val att = sortAttachment(part, attachment).await()
            if (att != null) list.add(att)
          }
        }
        getAllByAttachmentId.complete(list)
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
      email: String,
  ): CompletableDeferred<List<Attachment>?> {
    val getAllMultipart = CompletableDeferred<List<Attachment>?>()
    val messagePart = message.payload
    CoroutineScope(Dispatchers.IO).launch {
      // Check if the MIME type of the message part is multipart
      if (messagePart?.mimeType?.substringBefore("/") == "multipart") {
        val list = mutableListOf<Attachment>()
        // Iterate over each part of the message
        messagePart.parts?.forEach { part ->
          if (part != null) {
            // If the part has data, decode it based on its MIME type
            if (!part.body?.data.isNullOrEmpty()) {
              val attachment = sortAttachment(part).await()
              if (attachment != null) list.add(attachment)
            }
            // If the part has an attachment ID, retrieve and decode the attachment
            if (!part.body?.attachmentId.isNullOrEmpty()) {
              val attachment =
                  getAttachments(context, email, message.id, part.body?.attachmentId!!).await()
              val byAttachmentId = getAllByAttachmentId(part, attachment).await()
              if (byAttachmentId != null) list.addAll(byAttachmentId)
            }
          }
        }
        getAllMultipart.complete(list)
      } else getAllMultipart.complete(null)
    }
    return getAllMultipart
  }

  @OptIn(ExperimentalEncodingApi::class)
  private fun sortAttachment(
      messagePart: MessagePart,
      attachment: ByteArray? = null
  ): CompletableDeferred<Attachment?> {
    val sortAttachment = CompletableDeferred<Attachment?>()
    CoroutineScope(Dispatchers.IO).launch {
      val attachmentType =
          if (messagePart.mimeType?.substringBefore("/") == "image") AttachmentType.IMAGE
          else if (messagePart.mimeType?.substringBefore("/") == "text") AttachmentType.TEXT
          else if (messagePart.mimeType == "application/pdf") AttachmentType.PDF else null
      if (attachmentType != null) {
        val att = attachment ?: Base64.UrlSafe.decode(messagePart.body?.data!!)
        sortAttachment.complete(Attachment(attachmentType, att))
      } else sortAttachment.complete(null)
    }
    return sortAttachment
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
      // Retrieve the attachment from the email message
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
    // Remove the email account from the authentication repository
    TikiClient.auth.repository.remove(context, email)
    // Delete the indexes associated with the email account
    TikiClient.email.repository.deleteIndexes(context, email)
    // Remove the data associated with the email account
    TikiClient.email.repository.removeData(context, email)
  }
}
