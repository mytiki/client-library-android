package com.mytiki.publish.client.email

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.mytiki.publish.client.auth.AuthToken
import com.mytiki.publish.client.email.messageResponse.Message
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest


class EmailRepository{
    private var masterKey: MasterKey? = null
    private var sharedPreferences: SharedPreferences? = null
    private var editor:  SharedPreferences.Editor? = null


    private fun checkData(context: Context){
        if (masterKey == null){
            masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }
        if (sharedPreferences == null){
            sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "email_repository",
                masterKey!!,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            editor = sharedPreferences!!.edit()
        }
    }

    fun saveData(context: Context, date: IndexData): Boolean {
        checkData(context)
        val dataJson = date.toJson()
        return editor!!.putString(date.email, dataJson).commit()
    }
    fun updateNextPageToken(context: Context, email: String, nextPageToken: String?): Boolean {
        checkData(context)
        val oldData = getData(context, email)
        return if (oldData == null){
            false
        } else{
            val newData = IndexData(email, oldData.date, nextPageToken).toJson()
            editor!!.putString(email, newData).commit()
        }
    }

    fun getData(context: Context, email: String): IndexData? {
        checkData(context)
        val data = sharedPreferences!!.getString(email, null)
        return if (data != null){
            IndexData.fromJson(data, email)
        } else {
            null
        }
    }

    fun removeData(context: Context, email: String){
        checkData(context)
        editor!!.remove(email).commit()
    }

    fun accountsData(context: Context): List<IndexData> {
        checkData(context)
        val allEntries: MutableMap<String, *>? = sharedPreferences!!.all
        val data = mutableListOf<IndexData>()
        allEntries?.forEach { (key, value) ->
            if(value is String){
                data.add(IndexData.fromJson(value, key))
            }
        }
        return data
    }


    companion object{
        private fun md5(input:String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
        }

        fun saveIndexes(context: Context, email: String, messages: Array<Message?>){
            val file = File(context.filesDir, md5(email))
            messages.forEach {
                if (it != null){
                    file.appendText(it.id)
                    file.appendText("\r\n")
                }
            }
        }

        fun readIndexes(context: Context, email: String): Array<String>{
            val file = File(context.filesDir, md5(email))
            if (file.exists()){
                return file.readLines().toTypedArray()
            } else throw Exception("data not found for this email")
        }

        fun deleteIndexes(context: Context, email: String): Boolean {
            val file = File(context.filesDir, md5(email))
            return file.delete()
        }

        fun checkIndexes(context: Context, email: String): Boolean {
            val file = File(context.filesDir, md5(email))
            return file.exists()
        }
    }

}