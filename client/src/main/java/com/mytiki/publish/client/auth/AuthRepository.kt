package com.mytiki.publish.client.auth

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.mytiki.publish.client.email.EmailProviderEnum

class AuthRepository() {

    private var masterKey: MasterKey? = null
    private var sharedPreferences: SharedPreferences? = null
    private var editor:  SharedPreferences.Editor? = null


    private fun check(context: Context){
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

    fun save(context: Context, token: AuthToken): Boolean {
        check(context)
        val tokenJson = token.toString()
        return editor!!.putString(token.username, tokenJson).commit()
    }

    fun get(context: Context, email: String): AuthToken? {
        check(context)
        val token = sharedPreferences!!.getString(email, null)
        return if (token != null){
           AuthToken.fromString(token, email)
        } else {
            null
        }
    }
    
    fun remove(context: Context, email: String){
        check(context)
        editor!!.remove(email).commit()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun accounts(context: Context, emailProviderEnum: EmailProviderEnum): List<String> {
        check(context)
        val allEntries: MutableMap<String, *>? = sharedPreferences!!.all
        val accounts = mutableListOf<String>()
        allEntries?.forEach { (key, value) ->
            if(value is String){
                if(AuthToken.fromString(value, key).provider == emailProviderEnum){
                    accounts.add(key)
                }
            }
        }
        return accounts
    }
}