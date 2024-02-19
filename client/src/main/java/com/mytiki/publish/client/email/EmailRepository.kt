package com.mytiki.publish.client.email

import android.content.Context
import androidx.collection.floatIntMapOf
import com.mytiki.publish.client.email.messageResponse.Message
import com.mytiki.publish.client.email.messageResponse.MessageResponse
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.MessageDigest


class EmailRepository{
    companion object{
        private fun md5(input:String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
        }
        fun save(context: Context, email: String, messages: Array<Message?>){
            val file = File(context.filesDir, md5(email))
            messages.forEach {
                if (it != null){
                    file.appendText(it.id)
                    file.appendText("\r\n")
                }
            }
        }
        fun read(context: Context, email: String): Array<String>{
            val file = File(context.filesDir, md5(email))
            if (file.exists()){
                return file.readLines().toTypedArray()
            } else throw Exception("data not found for this email")
        }

        fun delete(context: Context, email: String): Boolean {
            val file = File(context.filesDir, md5(email))
            return file.delete()
        }
    }
}