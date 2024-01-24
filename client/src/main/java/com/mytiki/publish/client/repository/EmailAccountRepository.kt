package com.mytiki.publish.client.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.mytiki.publish.client.auth.AuthToken


class EmailAccountRepository() {

    private var masterKey: MasterKey? = null
    private var sharedPreferences: SharedPreferences? = null
    private var editor:  SharedPreferences.Editor? = null
    private val gson = Gson()

    private fun check(context: Context){
        if (masterKey == null){
            masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        }
        if (sharedPreferences == null){
            sharedPreferences = EncryptedSharedPreferences.create(
                context,
                "secret_shared_prefs",
                masterKey!!,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            editor = sharedPreferences!!.edit()
        }
    }

    fun saveToken(context: Context, email: String, token: AuthToken){
        check(context)
        if (sharedPreferences!!.contains(email)){
            updateToken(email, token)
        } else {
            editor!!.putString(email, gson.toJson(token))
        }
    }

    private fun updateToken(email: String, token: AuthToken){
        editor!!.remove(email)
        editor!!.putString(email, gson.toJson(token))
    }

    fun getToken(context: Context, email: String): AuthToken? {
        check(context)
        val token = sharedPreferences!!.getString(email, null)
        return if (token != null){
            gson.fromJson(token, AuthToken::class.java)
        } else {
            null
        }
    }
    
    fun removeToken(context: Context, email: String){
        check(context)
        editor!!.remove(email)
    }

}