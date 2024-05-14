package com.mytiki.publish.client.capture

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.email.Attachment
import com.mytiki.publish.client.email.AttachmentType
import com.mytiki.publish.client.utils.apiService.ApiService
import java.util.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

/** Service class for capturing and processing receipt data. */
class CaptureService {
  var imageCallback: (Bitmap) -> Unit = {}
    private set

  private var baseUrl = "https://publish.mytiki.com/receipt/"

  /**
   * Launches the camera activity for capturing an image of a receipt.
   *
   * @param activity The ComponentActivity launching the camera.
   */
  fun scan(activity: ComponentActivity, imageCallback: (Bitmap) -> Unit) {
    this.imageCallback = imageCallback
    activity.startActivity(Intent(activity, CaptureActivity::class.java))
  }

  /**
   * Uploads a bitmap image for receipt data extraction.
   *
   * @param data The bitmap image data.
   * @return A CompletableDeferred object that will resolve when the data has been published.
   * @throws Exception if there is an error during the process.
   */
  fun publish(context: Context, attachment: Attachment): CompletableDeferred<Unit> {
    return when (attachment.type) {
      AttachmentType.IMAGE -> publishImage(attachment)
      AttachmentType.PDF -> publishPdf(context, attachment)
      AttachmentType.TEXT -> publishText(attachment)
    }
  }

  /**
   * Uploads an array of bitmap images for receipt data extraction.
   *
   * @param data The array of bitmap image data.
   * @return A CompletableDeferred object that will resolve when all the data has been published.
   */
  fun publish(context: Context, attachmentList: Array<Attachment>): CompletableDeferred<Unit> {
    val isPublished = CompletableDeferred<Unit>()
    MainScope().async {
      attachmentList.forEachIndexed { index, attachment ->
        publish(context, attachment).await()
        if (index == attachmentList.size - 1) isPublished.complete(Unit)
      }
    }
    return isPublished
  }

  /**
   * Uploads a bitmap image for receipt data extraction.
   *
   * @param data The bitmap image data.
   * @return A CompletableDeferred object that will resolve when the data has been published.
   * @throws Exception if there is an error during the process.
   */
  private fun publishImage(attachment: Attachment): CompletableDeferred<Unit> {
    val isPublished = CompletableDeferred<Unit>()
    CoroutineScope(Dispatchers.IO).launch {
      if (!TikiClient.license.verify())
          throw Exception(
              "The License is invalid. Use the TikiClient.license method to issue a new License.")
      val auth = TikiClient.auth.addressToken().await()
      val image = attachment.toImage()
      val id = UUID.randomUUID()
      val body =
          MultipartBody.Builder()
              .setType(MultipartBody.FORM)
              .addFormDataPart(
                  "file", "receipt.jpeg", image.asRequestBody("image/jpeg".toMediaTypeOrNull()))
              .build()
      ApiService.post(
              header = mapOf("Content-Type" to "image/jpeg", "Authorization" to "Bearer $auth"),
              endPoint = baseUrl + id,
              onError = Exception("error uploading image"),
              body,
          )
          .await()
      isPublished.complete(Unit)
    }
    return isPublished
  }

  private fun publishPdf(context: Context, attachment: Attachment): CompletableDeferred<Unit> {
    val isPublished = CompletableDeferred<Unit>()
    CoroutineScope(Dispatchers.IO).launch {
      if (!TikiClient.license.verify())
          throw Exception(
              "The License is invalid. Use the TikiClient.license method to issue a new License.")
      val auth = TikiClient.auth.addressToken().await()

      val id = UUID.randomUUID()
      val pdf = attachment.toPdf(context)
      val body =
          MultipartBody.Builder()
              .setType(MultipartBody.FORM)
              .addFormDataPart(
                  "file", "${id}.pdf", pdf.asRequestBody("application/pdf".toMediaTypeOrNull()))
              .build()

      ApiService.post(
              header =
                  mapOf("Content-Type" to "application/pdf", "Authorization" to "Bearer $auth"),
              endPoint = baseUrl + id,
              onError = Exception("error uploading image"),
              body,
          )
          .await()
      isPublished.complete(Unit)
    }
    return isPublished
  }

  /**
   * Uploads a bitmap image for receipt data extraction.
   *
   * @param data The bitmap image data.
   * @return A CompletableDeferred object that will resolve when the data has been published.
   * @throws Exception if there is an error during the process.
   */
  private fun publishText(attachment: Attachment): CompletableDeferred<Unit> {
    val isPublished = CompletableDeferred<Unit>()
    isPublished.complete(Unit)
    return isPublished
  }

  /**
   * Retrieve the structured data extracted from the processed receipt images.
   *
   * This method fetches the result of the receipt image processing from the server.
   *
   * @param receiptId The unique identifier for the receipt obtained from the publish method.
   * @param token The address token to connect with TIKI API.
   * @param onResult A callback functions that revceives the array of ReceiptResponse objects, each
   *   containing the structured data extracted from an image of the receipt, or null if the
   *   retrieval fails.
   */
  suspend fun receipt(
      receiptId: String,
      token: String,
      onResult: (Array<ReceiptResponse?>) -> Unit
  ) {
    val response =
        ApiService.get(
                header = mapOf("Content-Type" to "image/jpeg", "Authorization" to "Bearer $token"),
                endPoint = "https://publish.mytiki.com/receipt/${receiptId}",
                onError = Exception("error uploading image"),
            )
            .await()
    onResult(listOf(ReceiptResponse.from(response!!)).toTypedArray())
  }
}
