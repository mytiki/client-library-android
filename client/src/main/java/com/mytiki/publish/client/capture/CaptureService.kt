package com.mytiki.publish.client.capture

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.mytiki.publish.client.TikiClient

class CaptureService {

    /**
     * Captures an image of a receipt for processing.
     * @return The captured receipt image.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun camera(activity: ComponentActivity) {
        if (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            val requestPermissionCode = 98734763
            activity.requestPermissions(
                listOf(Manifest.permission.CAMERA).toTypedArray(),
                requestPermissionCode
            )
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) {
                activity.startActivity(Intent(activity, CaptureActivity::class.java))
            }
        } else {
            activity.startActivity(Intent(activity, CaptureActivity::class.java))
        }
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