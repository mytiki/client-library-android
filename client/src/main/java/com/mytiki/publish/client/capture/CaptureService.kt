package com.mytiki.publish.client.capture

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.PermissionChecker
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.email.messageResponse.Message
import com.mytiki.publish.client.utils.apiService.ApiService
import io.flutter.embedding.android.FlutterView.FlutterEngineAttachmentListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

/**
 * Service class for capturing and processing receipt data.
 */
class CaptureService {

    /**
     * Captures an image of a receipt for processing.
     * @param activity The ComponentActivity launching the camera.
     */
    fun camera(activity: ComponentActivity) {
        activity.startActivity(Intent(activity, CaptureActivity::class.java))
    }

    /**
     * Downloads potential receipt data from known receipt email senders and publishes it.
     * @param onPublish The callback function to be called on each uploaded email.
     */
    fun email(onPublish: (receiptId: String) -> Unit) {
        // Placeholder method, to be implemented
    }

    /**
     * Uploads receipt images or email data for receipt data extraction.
     * @param data The binary image or email data.
     * @return True if the data was successfully published, false otherwise.
     */
    fun publish(data: Bitmap): CompletableDeferred<Unit> {
        // Placeholder method, to be implemented
        val isPublished = CompletableDeferred<Unit>()
        CoroutineScope(Dispatchers.IO).launch {
            val auth = TikiClient.auth.token().await()


            val file = File.createTempFile("receipt", ".jpeg")
            val output = file.outputStream()
            val image = data.compress(Bitmap.CompressFormat.JPEG, 100, output)

            if (image) {
                val id = UUID.randomUUID()
                val body = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "file", "receipt.jpeg",
                        file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    .build()

                ApiService.post(
                    mapOf(
                        "Content-Type" to "image/jpeg",
                        "Authorization" to "Bearer $auth"
                    ),
                    "https://publish.mytiki.com/receipt/${id}",
                    body,
                    Exception("error uploading image")
                ).await()
                isPublished.complete(Unit)
            } else throw Exception("error on compressing image")
        }
        return isPublished
    }

    /**
     * Uploads receipt email data for receipt data extraction.
     * @param message The email message containing potential receipt data.
     * @return True if the email data was successfully published, false otherwise.
     */
    fun publish(message: Message): Boolean {
        // Placeholder method, to be implemented
        Log.d("**** Message ******", message.toJson().toString())
        return true
    }

    /**
     * Uploads receipt email data along with attachments for receipt data extraction.
     * @param message The email message containing potential receipt data.
     * @param attachments List of attachments associated with the email.
     * @return True if the email data with attachments was successfully published, false otherwise.
     */
    fun publish(message: Message, attachments: List<Any>?): Boolean {
        // Placeholder method, to be implemented
        Log.d("**** Message/ATT ****", message.toJson().toString())
        return true
    }

    /**
     * Checks the publishing status of the data.
     * @param receiptId The ID of the published data.
     * @return The publishing status.
     */
    fun status(receiptId: String): PublishingStatusEnum {
        return PublishingStatusEnum.IN_PROGRESS
    }
}
