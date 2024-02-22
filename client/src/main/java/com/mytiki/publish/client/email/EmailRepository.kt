package com.mytiki.publish.client.email

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.mytiki.publish.client.email.messageResponse.Message
import kotlinx.coroutines.CompletableDeferred
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
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

    private fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    fun saveIndexes(context: Context, email: String, messages: Array<Message?>){
        val file = File(context.filesDir, md5(email))
        messages.forEach {
            if (it != null){
                file.appendText(it.id.trimStart().trimEnd() + System.getProperty("line.separator"))
            }
        }
    }

    fun readIndexes(context: Context, email: String, numberOfItems: Int): Array<String> {
        val file = File(context.filesDir, md5(email))
        val indexes = mutableListOf<String>()
        if (file.exists()){
            file.useLines {
                var count = 0
                it.forEach { id ->
                    if (count == numberOfItems) return indexes.toTypedArray()
                    indexes.add(id.trimStart().trimEnd())
                    count++
                }
            }
            return indexes.toTypedArray()
        } else throw Exception("data not found for this email")
    }

    fun removeIndex(context: Context, email: String, vararg id: String): Boolean{
        val inputFile = File(context.filesDir, md5(email))
        val tempFile = File(context.filesDir, "TempFile.txt")

        val reader = BufferedReader(FileReader(inputFile))
        val writer = BufferedWriter(FileWriter(tempFile))

        reader.useLines {
            it.forEach { currentLine ->
                if (currentLine in id) return@forEach
                writer.write(currentLine + System.getProperty("line.separator"))
            }
        }

        writer.close()
        reader.close()

        val del = inputFile.delete()
        val rename = tempFile.renameTo(inputFile)

        return del && rename
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