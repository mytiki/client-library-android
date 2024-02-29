package com.mytiki.publish.client.capture

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.PermissionChecker
import com.mytiki.publish.client.email.messageResponse.Message
import io.flutter.embedding.android.FlutterView.FlutterEngineAttachmentListener
import java.io.File

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
    fun publish(data: Bitmap): Boolean {
        // Placeholder method, to be implemented
        Log.d("*******************", "Worked!!!!")
        return true
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
