package com.mytiki.publish.client.capture

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log



class CaptureService {

    /**
     * Captures an image of a receipt for processing.
     * @return The captured receipt image.
     */
    fun camera(activity: Activity): Bitmap? {
        return null
    }

    /**
     * Downloads potential receipt data from known receipt email senders and publishes it.
     * @param onPublish The callback function to be called on each uploaded email.
     */
    fun email(onPublish: (receiptId: String) -> Unit){}

    /**
     * Uploads receipt images or email data for receipt data extraction.
     * @param data The binary image or email data.
     * @return The ID of the uploaded data to check publishing status.
     */
    fun publish(data: Bitmap): String{
        Log.d("*******************", "Worked!!!!")
        return ""
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