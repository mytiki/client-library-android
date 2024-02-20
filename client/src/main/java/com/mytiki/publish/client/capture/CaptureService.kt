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


class CaptureService {

    /**
     * Captures an image of a receipt for processing.
     * @return The captured receipt image.
     */
    fun camera(activity: ComponentActivity) {
        activity.startActivity(Intent(activity, CaptureActivity::class.java))
    }



    /**
     * Downloads potential receipt data from known receipt email senders and publishes it.
     * @param onPublish The callback function to be called on each uploaded email.
     */
    fun email(onPublish: (receiptId: String) -> Unit){}

    /**
     * Uploads receipt images or email data for receipt data extraction.
     * @param data The binary image or email data.
     * @return The ID of the uploaded data to checkIndexes publishing status.
     */
    fun publish(data: Bitmap): Boolean {
        Log.d("*******************", "Worked!!!!")
        return true
    }

    /**
     * Uploads receipt images or email data for receipt data extraction.
     * @param data The binary image or email data.
     * @return The ID of the uploaded data to checkIndexes publishing status.
     */
    fun publish(data: List<Message>): Boolean{
        data.forEach {
            Log.d("*******************", it.toJson().toString())
        }
        return true
    }

    /**
     * Checks the publishing status of the data.
     * @param receiptId The ID of the published data.
     * @return The publishing status.
     */
    fun status(receiptId: String): PublishingStatusEnum{
        return PublishingStatusEnum.IN_PROGRESS
    }
}