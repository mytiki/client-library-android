package com.mytiki.publish.client.capture

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.capture.rsp.CaptureReceiptRsp
import com.mytiki.publish.client.email.EmailAttachment
import com.mytiki.publish.client.email.EmailAttachmentType
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
   * Publishes an emailAttachment based on its type.
   *
   * This function publishes an emailAttachment. It offerUses the provided Context instance, a
   * string pointer, and an EmailAttachment object. The function calls the appropriate publish
   * method based on the type of the emailAttachment (IMAGE, PDF, TEXT).
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for the publishing
   *   process.
   * @param emailAttachment The EmailAttachment object containing the details of the emailAttachment
   *   to be published.
   * @return A CompletableDeferred object that will be completed when the emailAttachment has been
   *   published.
   */
  fun publish(context: Context, emailAttachment: EmailAttachment): CompletableDeferred<Unit> {
    return when (emailAttachment.type) {
      EmailAttachmentType.IMAGE -> publishImage(emailAttachment)
      EmailAttachmentType.PDF -> publishPdf(context, emailAttachment)
      EmailAttachmentType.TEXT -> publishText(emailAttachment)
    }
  }

  /**
   * Publishes an array of attachments.
   *
   * This function publishes an array of attachments. It offerUses the provided Context instance, a
   * string pointer, and an array of EmailAttachment objects. The function calls the publish method
   * for each attachment in the array.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for the publishing
   *   process.
   * @param emailAttachmentList The array of EmailAttachment objects containing the details of the
   *   attachments to be published.
   * @return A CompletableDeferred object that will be completed when all the attachments have been
   *   published.
   */
  fun publish(
      context: Context,
      emailAttachmentList: Array<EmailAttachment>
  ): CompletableDeferred<Unit> {
    val isPublished = CompletableDeferred<Unit>()
    MainScope().async {
      emailAttachmentList.forEachIndexed { index, attachment ->
        publish(context, attachment).await()
        if (index == emailAttachmentList.size - 1) isPublished.complete(Unit)
      }
    }
    return isPublished
  }

  /**
   * Publishes an image emailAttachment.
   *
   * This function publishes an image emailAttachment. It offerUses a string pointer and an
   * EmailAttachment object. The function calls the post method of the ApiService instance to
   * publish the image.
   *
   * @param emailAttachment The EmailAttachment object containing the details of the image to be
   *   published.
   * @return A CompletableDeferred object that will be completed when the image has been published.
   * @throws Exception if there is an error during the publishing process.
   */
  private fun publishImage(emailAttachment: EmailAttachment): CompletableDeferred<Unit> {
    val isPublished = CompletableDeferred<Unit>()
    CoroutineScope(Dispatchers.IO).launch {
      if (!TikiClient.license.verify())
          throw Exception(
              "The License is invalid. OfferUse the TikiClient.license method to issue a new License.")
      val auth = TikiClient.auth.addressToken().await()
      val image = emailAttachment.toImage()
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

  /**
   * Publishes a PDF emailAttachment.
   *
   * This function publishes a PDF emailAttachment. It offerUses the provided Context instance, a
   * string pointer, and an EmailAttachment object. The function calls the post method of the
   * ApiService instance to publish the PDF.
   *
   * @param context The Context instance. This is typically the current activity or application
   *   context from which this function is called. It is used to provide context for the publishing
   *   process.
   * @param emailAttachment The EmailAttachment object containing the details of the PDF to be
   *   published.
   * @return A CompletableDeferred object that will be completed when the PDF has been published.
   * @throws Exception if there is an error during the publishing process.
   */
  private fun publishPdf(
      context: Context,
      emailAttachment: EmailAttachment
  ): CompletableDeferred<Unit> {
    val isPublished = CompletableDeferred<Unit>()
    CoroutineScope(Dispatchers.IO).launch {
      if (!TikiClient.license.verify())
          throw Exception(
              "The License is invalid. OfferUse the TikiClient.license method to issue a new License.")
      val auth = TikiClient.auth.addressToken().await()

      val id = UUID.randomUUID()
      val pdf = emailAttachment.toPdf(context)
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
   * Publishes a text emailAttachment.
   *
   * This function publishes a text emailAttachment. It offerUses a string pointer and an
   * EmailAttachment object. The function completes the CompletableDeferred object immediately as
   * there is no actual publishing process for text attachments.
   *
   * @param emailAttachment The EmailAttachment object containing the details of the text to be
   *   published.
   * @return A CompletableDeferred object that will be completed immediately.
   */
  private fun publishText(emailAttachment: EmailAttachment): CompletableDeferred<Unit> {
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
   * @param onResult A callback functions that revceives the array of CaptureReceiptRsp objects,
   *   each containing the structured data extracted from an image of the receipt, or null if the
   *   retrieval fails.
   */
  suspend fun receipt(
      receiptId: String,
      token: String,
      onResult: (Array<CaptureReceiptRsp?>) -> Unit
  ) {
    val response =
        ApiService.get(
                header = mapOf("Content-Type" to "image/jpeg", "Authorization" to "Bearer $token"),
                endPoint = "https://publish.mytiki.com/receipt/${receiptId}",
                onError = Exception("error uploading image"),
            )
            .await()
    onResult(listOf(CaptureReceiptRsp.from(response!!)).toTypedArray())
  }
}
