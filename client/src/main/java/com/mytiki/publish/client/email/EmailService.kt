package com.mytiki.publish.client.email

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.email.messageResponse.Message
import com.mytiki.publish.client.email.messageResponse.MessagePartBody
import com.mytiki.publish.client.email.messageResponse.MessageResponse
import io.flutter.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
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
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
            val request = Request.Builder()
                .url(provider.userInfoEndpoint)
                .addHeader("Authorization", "Bearer $auth")
                .get()
                .build()
            val apiResponse = client.newCall(request).execute()

            if (apiResponse.code in 200..299) {
                emailResponse.complete(EmailResponse.fromJson(JSONObject(apiResponse.body?.string()!!)))
            } else throw Exception("error on user info request")
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
                val client = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
                val request = Request.Builder()
                    .url(endpoint.value)
                    .addHeader("Authorization", "Bearer $auth")
                    .get()
                    .build()
                val apiResponse = client.newCall(request).execute()
                if (apiResponse.code in 200..299) {
                    val resp = MessageResponse.fromJson(JSONObject(apiResponse.body?.string()!!))
                    if (!resp.messages.isNullOrEmpty()) {
                        emailRepository.saveIndexes(context, email, resp.messages)
                        if (isFirst.value) {
                            emailRepository.saveData(
                                context,
                                IndexData(email, Date(), resp.nextPageToken)
                            )
                        } else {
                            emailRepository.updateNextPageToken(context, email, resp.nextPageToken)
                        }
                        if (!resp.nextPageToken.isNullOrEmpty()) {
                            endpoint.value =
                                provider.messagesIndexListEndpoint + "&pageToken=${resp.nextPageToken}"
                        } else {
                            isWorking.value = false
                        }
                        isFirst.value = false
                    }
                } else throw Exception("error on getting messagesIndex")
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

    @OptIn(ExperimentalEncodingApi::class)
    fun scrapeInChunks(context: Context, email: String, numberOfItems: Int): CompletableDeferred<Boolean>{
        val token = TikiClient.auth.authRepository.get(context, email)
            ?: throw Exception("this email is not logged")
        val provider = token.provider
        val auth = token.auth

        val scrapeInChunks = CompletableDeferred<Boolean>()
        val indexes = emailRepository.readIndexes(context, email, numberOfItems)
        val messageRequestArray = mutableListOf<Deferred<Response>>()
        val attachmentRequestArray = mutableListOf<Deferred<Unit>>()

        CoroutineScope(Dispatchers.IO).launch {
            indexes.forEachIndexed { index, messageID ->
                val client = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
                val request = Request.Builder()
                    .url(provider.messageEndpoint(messageID))
                    .addHeader("Authorization", "Bearer $auth")
                    .get()
                    .build()
                val apiRequest = async { client.newCall(request).execute() }
                messageRequestArray.add(apiRequest)
                Log.d("**************", "COMEÇOU $index")
            }

            val messageResponseList = awaitAll(*messageRequestArray.toTypedArray())
            messageResponseList.forEach { apiResponse ->
                if (apiResponse.code in 200..299) {
                    val attachmentDeferred = async {
                        val message = (Message.fromJson(JSONObject(apiResponse.body?.string()!!)))
                        val attachmentList = mutableListOf<Any>()
                        decodeByMimiType(context, email, message, attachmentList).await()
                        val published = TikiClient.capture.publish(message, attachmentList)
                        if (!published) scrapeInChunks.completeExceptionally(Exception("error updating index list"))
                    }
                    attachmentRequestArray.add(attachmentDeferred)
                } else { scrapeInChunks.completeExceptionally(Exception("error on getting messagesIndex")) }
            }
            awaitAll(*attachmentRequestArray.toTypedArray())

            val removed = emailRepository.removeIndex(context, email, *indexes)
            if (!removed) scrapeInChunks.completeExceptionally(Exception("error updating index list"))
            scrapeInChunks.complete(numberOfItems >= messageRequestArray.size)
        }
        return scrapeInChunks
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decodeByMimiType(context: Context, email: String, message: Message, attachmentList: MutableList<Any>): CompletableDeferred<Unit>{
        val decodeByMimiType = CompletableDeferred<Unit>()
        CoroutineScope(Dispatchers.IO).launch {
            if (!message.payload?.mimeType.isNullOrEmpty()) {
                val mimiType = message.payload?.mimeType!!.substringBefore("/")
                if (mimiType.substringBefore("/") == "multipart") {
                    message.payload.parts?.forEach { messagePart ->
                        if (!messagePart?.body?.data.isNullOrEmpty()) {
                            val attachment = Base64.UrlSafe.decode(messagePart?.body?.data!!)

                            if (messagePart.mimeType!!.substringBefore("/") == "image") {
                                attachmentList.add(
                                    BitmapFactory.decodeByteArray(
                                        attachment,
                                        0,
                                        attachment.size
                                    )
                                )
                                decodeByMimiType.complete(Unit)

                            } else if (messagePart.mimeType.substringBefore("/") == "text") {
                                attachmentList.add(String(attachment))
                                decodeByMimiType.complete(Unit)
                            } else if (messagePart.mimeType == "application/pdf") {
                                val filePDF =
                                    File(context.filesDir, messagePart.body?.attachmentId + ".pdf")
                                val pdfOutputStream = FileOutputStream(filePDF, false)
                                pdfOutputStream.write(attachment)
                                pdfOutputStream.flush()
                                pdfOutputStream.close()
                                attachmentList.add(filePDF)
                                decodeByMimiType.complete(Unit)
                            }
                        }
                    }
                } else if (mimiType.substringBefore("/") == "image") {
                    val attachment = Base64.UrlSafe.decode(message.payload.body?.data!!)
                    attachmentList.add(
                        BitmapFactory.decodeByteArray(
                            attachment,
                            0,
                            attachment.size
                        )
                    )
                    decodeByMimiType.complete(Unit)
                } else if (mimiType.substringBefore("/") == "text") {
                    val attachment = Base64.UrlSafe.decode(message.payload.body?.data!!)
                    attachmentList.add(String(attachment))
                    decodeByMimiType.complete(Unit)
                } else if (message.payload.mimeType == "application/pdf") {
                    val attachment = Base64.UrlSafe.decode(message.payload.body?.data!!)
                    val filePDF =
                        File(context.filesDir, message.payload.body?.attachmentId + ".pdf")
                    val pdfOutputStream = FileOutputStream(filePDF, false)
                    pdfOutputStream.write(attachment)
                    pdfOutputStream.flush()
                    pdfOutputStream.close()
                    attachmentList.add(filePDF)
                    decodeByMimiType.complete(Unit)
                } else if (!message.payload.body?.attachmentId.isNullOrEmpty()) {
                    val attachment = getAttachments(
                        context,
                        email,
                        message.id,
                        message.payload.body?.attachmentId!!
                    ).await()
                    if (message.payload.mimeType.substringBefore("/") == "image") {
                        attachmentList.add(
                            BitmapFactory.decodeByteArray(
                                attachment,
                                0,
                                attachment.size
                            )
                        )
                        decodeByMimiType.complete(Unit)
                    } else if (message.payload.mimeType.substringBefore("/") == "text") {
                        attachmentList.add(String(attachment))
                        decodeByMimiType.complete(Unit)
                    } else if (message.payload.mimeType == "application/pdf") {
                        val filePDF =
                            File(context.filesDir, message.payload.body?.attachmentId + ".pdf")
                        val pdfOutputStream = FileOutputStream(filePDF, false)
                        pdfOutputStream.write(attachment)
                        pdfOutputStream.flush()
                        pdfOutputStream.close()
                        attachmentList.add(filePDF)
                        decodeByMimiType.complete(Unit)
                    }
                }
            }
        }
        return decodeByMimiType
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun getAttachments(context: Context, email: String, messageID: String, attachmentID: String): CompletableDeferred<ByteArray>{
        val getAttachments = CompletableDeferred<ByteArray>()

        val token = TikiClient.auth.authRepository.get(context, email)
            ?: throw Exception("this email is not logged")
        val provider = token.provider
        val auth = token.auth

        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
            val request = Request.Builder()
                .url(provider.attachmentEndpoint(messageID, attachmentID))
                .addHeader("Authorization", "Bearer $auth")
                .get()
                .build()
            val response = client.newCall(request).execute()
            if (response.code in 200..299) {
                val attachmentResponse = MessagePartBody.fromJson(JSONObject(response.body?.string()!!))
                val attachmentBytes = Base64.UrlSafe.decode(attachmentResponse.data!!)
                getAttachments.complete(attachmentBytes)

            } else getAttachments.completeExceptionally(Exception("error on getting messagesIndex"))
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