package com.mytiki.publish.client.capture

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.utils.apiService.ApiService
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
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

    fun publish(data: Array<Bitmap>):CompletableDeferred<Unit>{
        val isPublished = CompletableDeferred<Unit>()
        MainScope().async {
            data.forEachIndexed { index, bitmap ->
                publish(bitmap).await()
                if (index == data.size - 1) isPublished.complete(Unit)
            }
        }
        return isPublished
    }
}
