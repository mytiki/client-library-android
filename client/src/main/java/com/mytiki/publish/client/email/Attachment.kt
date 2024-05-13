package com.mytiki.publish.client.email

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

class Attachment(val type: AttachmentType, val data: ByteArray) {
  fun toImage(): File {
    val resp = BitmapFactory.decodeByteArray(data, 0, data.size)
    val file = File.createTempFile("receipt", ".jpeg")
    val output = file.outputStream()
    val image = resp.compress(Bitmap.CompressFormat.JPEG, 100, output)
    if (image) return file else throw Exception("Failed to convert image")
  }

  fun toPdf(context: Context): File {
    val filePDF = File.createTempFile("receipt", ".pdf")
    val pdfOutputStream = filePDF.outputStream()
    pdfOutputStream.write(data)
    return filePDF
  }

  fun toText(): String {
    return String(data)
  }
}
