package com.mytiki.publish.client.email

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.email.messageResponse.Message
import com.mytiki.publish.client.email.messageResponse.MessagePart
import com.mytiki.publish.client.email.messageResponse.MessagePartBody
import com.mytiki.publish.client.email.messageResponse.MessageResponse
import com.mytiki.publish.client.utils.apiService.ApiService
import kotlinx.coroutines.*
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import okhttp3.ResponseBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class EmailService() {
    var googleKeys: EmailKeys? = null
    var outlookKeys: EmailKeys? = null
    val emailRepository = EmailRepository()

    var loginCallback: (String) -> Unit = {}
        private set

    /**
     * Authenticates with OAuth and adds an email account for scraping receipts.
     * @param provider The email provider (GOOGLE or OUTLOOK).
     */
    fun login(context: Context, provider: EmailProviderEnum, emailKeys: EmailKeys, redirectURI: String, loginCallback: (String) -> Unit){
        this.loginCallback = loginCallback
        val intent = Intent(context, EmailActivity::class.java)

        intent.putExtra("provider", provider.toString())
        intent.putExtra("clientID", emailKeys.clientId)
        intent.putExtra("clientSecret", emailKeys.clientSecret)
        intent.putExtra("redirectURI", redirectURI)

        context.startActivity(intent)
    }

    fun authRequest(context: Context, provider: EmailProviderEnum,  clientID: String, redirectURI: String): Pair<Intent?,  AuthorizationService> {
        val authServiceConfig = AuthorizationServiceConfiguration(
            Uri.parse(provider.authorizationEndpoint),
            Uri.parse(provider.tokenEndpoint)
        )
        val authRequest = AuthorizationRequest.Builder(
            authServiceConfig,
            clientID,
            ResponseTypeValues.CODE,
            Uri.parse(redirectURI)
        )
        authRequest.setScope(provider.scopes)
        val authService = AuthorizationService(context)
        return Pair(authService.getAuthorizationRequestIntent(authRequest.build()), authService)
    }

    fun getEmailResponse(provider: EmailProviderEnum, auth: String): CompletableDeferred<EmailResponse> {
        val emailResponse = CompletableDeferred<EmailResponse>()
        MainScope().async {
            val response = ApiService.get(
                mapOf("Authorization" to "Bearer $auth"),
                provider.userInfoEndpoint,
                Exception("error on user info request")
            ).await()
            emailResponse.complete(EmailResponse.fromJson(JSONObject(response?.string()!!)))
        }
        return emailResponse
    }

    /**
     * Retrieves the list of connected email accountsPerProvider.
     * @return List of connected email accountsPerProvider.
     */
    fun accountsPerProvider(context: Context, emailProvider: EmailProviderEnum): List<String> {
        return TikiClient.auth.authRepository.accounts(context, emailProvider)
    }

    fun accounts(context: Context): List<String> {
        val emailList = mutableListOf<String>()
        EmailProviderEnum.entries.forEach { provider ->
            val list = TikiClient.email.accountsPerProvider(context, provider)
            if(list.isNotEmpty()) emailList.addAll(list)
        }
        return emailList
    }

    fun messagesIndex(context: Context, email: String, nextPageToken: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val indexData = emailRepository.getData(context, email)
            val token = TikiClient.auth.authRepository.get(context, email)?: throw Exception("this email is not logged")
            val provider = token.provider
            val auth = token.auth

            val isFirst = mutableStateOf(nextPageToken.isNullOrEmpty())
            val isWorking = mutableStateOf(true)
            val endpoint = mutableStateOf(
                if (nextPageToken.isNullOrEmpty()) {
                    if (indexData == null) provider.messagesIndexListEndpoint
                    else provider.messagesIndexListEndpoint +"&q=after:${indexData.date.year}/${indexData.date.month}/${indexData.date.day}"
                }
                else provider.messagesIndexListEndpoint + "&pageToken=$nextPageToken"
            )
            while (isWorking.value){
                val response = ApiService.get(
                    mapOf("Authorization" to "Bearer $auth"),
                    endpoint.value,
                    Exception("error on getting messagesIndex")
                ).await()
                val messageResponse = MessageResponse.fromJson(JSONObject(response?.string()!!))
                if (!messageResponse.messages.isNullOrEmpty()) {
                    emailRepository.saveIndexes(context, email, messageResponse.messages)
                    if (isFirst.value) {
                        emailRepository.saveData(
                            context,
                            IndexData(email, Date(), messageResponse.nextPageToken)
                        )
                    } else {
                        emailRepository.updateNextPageToken(context, email, messageResponse.nextPageToken)
                    }
                    if (!messageResponse.nextPageToken.isNullOrEmpty()) {
                        endpoint.value =
                            provider.messagesIndexListEndpoint + "&pageToken=${messageResponse.nextPageToken}"
                    } else {
                        isWorking.value = false
                    }
                    isFirst.value = false
                }
            }
        }
    }


    fun checkIndexes(context: Context){
        val accounts = accounts(context)
        accounts.forEach{
            val indexData = emailRepository.getData(context, it)
            if (!indexData?.nextPageToken.isNullOrEmpty()){
                val token = TikiClient.auth.authRepository.get(context, it)
                if (token != null){
                    messagesIndex(context, token.email, indexData?.nextPageToken)
                }
            }
        }
    }

    fun scrape(context: Context, email: String, clientID: String): CompletableDeferred<Boolean>{
        TikiClient.auth.refresh(context, email, clientID)
        val scrape = CompletableDeferred<Boolean>()
        CoroutineScope(Dispatchers.IO).launch {
            var isWorking = true
            while (isWorking){
                isWorking = scrapeInChunks(context, email, numberOfItems = 15).await()
            }
        }.invokeOnCompletion { scrape.complete(true) }
        return scrape
    }

    fun scrapeInChunks(context: Context, email: String, numberOfItems: Int): CompletableDeferred<Boolean>{
        val token = TikiClient.auth.authRepository.get(context, email)?: throw Exception("this email is not logged")
        val provider = token.provider
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
                        Exception("error on getting messagesIndex")
                    ).await()
                }
                messageRequestArray.add(apiRequest)
            }
            val messageResponseList = awaitAll(*messageRequestArray.toTypedArray())
            messageResponseList.forEach { apiResponse ->
                val attachmentDeferred = async {
                    val message = (Message.fromJson(JSONObject(apiResponse?.string()!!)))
                    val attachmentList = mutableListOf<Any>()
                    decodeByMimiType(context, email, message, attachmentList).await()
                    val published = TikiClient.capture.publish(message, attachmentList)
                    if (!published) scrapeInChunks.completeExceptionally(Exception("error updating index list"))
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

    private fun decodeByMimiType(context: Context, email: String, message: Message, attachmentList: MutableList<Any>): CompletableDeferred<Unit>{
        val decodeByMimiType = CompletableDeferred<Unit>()
        CoroutineScope(Dispatchers.IO).launch {
            if (!message.payload?.mimeType.isNullOrEmpty()) {

                val text = message.payload?.let { getAllText(it).await() }
                if (!text.isNullOrEmpty()) attachmentList.add(text)

                val image = message.payload?.let { getAllImage(it).await() }
                if (image != null) attachmentList.add(image)

                val pdf = message.payload?.let { getAllPdf(context, it).await() }
                if (pdf != null) attachmentList.add(pdf)

                val multiPart = message.payload?.let { getAllMultipart(context, message, email).await() }
                if (multiPart != null) attachmentList.addAll(multiPart)

                if (!message.payload?.body?.attachmentId.isNullOrEmpty()) {
                    val attachment = getAttachments(
                        context,
                        email,
                        message.id,
                        message.payload?.body?.attachmentId!!
                    ).await()
                    val byAttachmentId = message.payload?.let { getAllByAttachmentId(context, it, attachment).await() }
                    if (byAttachmentId != null) attachmentList.addAll(byAttachmentId)
                }
            }
        }
        return decodeByMimiType
    }

    private fun getAllByAttachmentId(context: Context, messagePart: MessagePart, attachment: ByteArray): CompletableDeferred<List<Any>?>{
        val getAllByAttachmentId =  CompletableDeferred<List<Any>?>()
        CoroutineScope(Dispatchers.IO).launch {
            if (messagePart.mimeType?.substringBefore("/") == "multipart") {
                val array = mutableListOf<Any>()
                messagePart.parts?.forEach { part ->
                    if (part != null) {
                        val text = getAllText(part, attachment).await()
                        if(text != null) array.add(text)

                        val image = getAllImage(part, attachment).await()
                        if(image != null) array.add(image)

                        val pdf = getAllPdf(context, part, attachment).await()
                        if(pdf != null) array.add(pdf)
                    }
                }
                getAllByAttachmentId.complete(array)
            }else getAllByAttachmentId.complete(null)
        }
        return getAllByAttachmentId
    }

    private fun getAllMultipart(context: Context, message: Message, email: String,): CompletableDeferred<List<Any>?>{
        val getAllMultipart = CompletableDeferred<List<Any>?>()
        val messagePart = message.payload
        CoroutineScope(Dispatchers.IO).launch {
            if (messagePart?.mimeType?.substringBefore("/") == "multipart") {
                val list = mutableListOf<Any>()
                messagePart.parts?.forEach { part ->
                    if (part != null) {
                        val text = getAllText(part).await()
                        if(text != null) list.add(text)

                        val image = getAllImage(part).await()
                        if(image != null) list.add(image)

                        val pdf = getAllPdf(context, part).await()
                        if(pdf != null) list.add(pdf)

                        if (!part.body?.attachmentId.isNullOrEmpty()) {
                            val attachment = getAttachments(
                                context,
                                email,
                                message.id,
                                part.body?.attachmentId!!
                            ).await()
                            val byAttachmentId = getAllByAttachmentId(context, part, attachment).await()
                            if (byAttachmentId != null) list.addAll(byAttachmentId)
                        }
                    }
                }
                getAllMultipart.complete(list)
            }else getAllMultipart.complete(null)
        }
        return getAllMultipart
    }
    @OptIn(ExperimentalEncodingApi::class)
    private fun getAllImage(
        messagePart: MessagePart,
        attachment: ByteArray? = null
    ): CompletableDeferred<Bitmap?>{
        val getAllImage = CompletableDeferred<Bitmap?>()
        CoroutineScope(Dispatchers.IO).launch {
            if (messagePart.mimeType?.substringBefore("/") == "image") {
                val att = attachment ?: Base64.UrlSafe.decode(messagePart.body?.data!!)
                val resp = BitmapFactory.decodeByteArray(
                    attachment,
                    0,
                    att.size
                )
                getAllImage.complete(resp)
            }else getAllImage.complete(null)
        }
        return getAllImage
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun getAllText(
        messagePart: MessagePart,
        attachment: ByteArray? = null
    ): CompletableDeferred<String?>{
        val getAllText =  CompletableDeferred<String?>()
        CoroutineScope(Dispatchers.IO).launch {
            if (messagePart.mimeType?.substringBefore("/") == "text") {
                val att = attachment?: Base64.UrlSafe.decode(messagePart.body?.data!!)
                getAllText.complete(String(att))
            }else getAllText.complete(null)
        }
        return getAllText
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun getAllPdf(
        context: Context,
        messagePart: MessagePart,
        attachment: ByteArray? = null
    ): CompletableDeferred<File?>{
        val getAllPdf =  CompletableDeferred<File?>()
        CoroutineScope(Dispatchers.IO).launch {
            if (messagePart.mimeType == "application/pdf") {
                val att = attachment ?: Base64.UrlSafe.decode(messagePart.body?.data!!)
                val filePDF =
                    File(context.filesDir, messagePart.body?.attachmentId + ".pdf")
                val pdfOutputStream = FileOutputStream(filePDF, false)
                pdfOutputStream.write(att)
                pdfOutputStream.flush()
                pdfOutputStream.close()
                getAllPdf.complete(filePDF)
            }else getAllPdf.complete(null)
        }
        return getAllPdf
    }


    @OptIn(ExperimentalEncodingApi::class)
    private fun getAttachments(context: Context, email: String, messageID: String, attachmentID: String): CompletableDeferred<ByteArray>{
        val getAttachments = CompletableDeferred<ByteArray>()
        val token = TikiClient.auth.authRepository.get(context, email)?: throw Exception("this email is not logged")
        val provider = token.provider
        val auth = token.auth

        MainScope().async {
            val response = ApiService.get(
                mapOf("Authorization" to "Bearer $auth"),
                provider.attachmentEndpoint(messageID, attachmentID),
                Exception("error on getting messagesIndex")
            ).await()
            val attachmentResponse = MessagePartBody.fromJson(JSONObject(response?.string()!!))
            val attachmentBytes = Base64.UrlSafe.decode(attachmentResponse.data!!)
            getAttachments.complete(attachmentBytes)
        }
        return getAttachments
    }

    /**
     * Removes a previously added email account.
     * @param email The email account to be removed.
     */
    fun logout(context: Context, email: String){
        TikiClient.auth.authRepository.remove(context, email)
        TikiClient.email.emailRepository.deleteIndexes(context, email)
        TikiClient.email.emailRepository.removeData(context, email)
    }

    fun googleKeys( clientId: String, clientSecrete: String) {
        googleKeys = EmailKeys(clientId, clientSecrete)

    }

    fun outlookKeys( clientId: String, clientSecrete: String) {
        outlookKeys = EmailKeys(clientId, clientSecrete)
    }
}
