package com.mytiki.publish.client.email

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.mytiki.publish.client.auth.AuthToken

class EmailRepository   () {

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

    fun save(context: Context, email: String, token: AuthToken){
        check(context)
        if (sharedPreferences!!.contains(email)){
            update(email, token.toString())
        } else {
            editor!!.putString(email, token.toString()).commit()
        }
    }

    private fun update(email: String, token: String){
        editor!!.remove(email)
        editor!!.putString(email, token)
    }

    fun get(context: Context, email: String): AuthToken? {
        check(context)
        val token = sharedPreferences!!.getString(email, null)
        return if (token != null){
           AuthToken.fromString(token)
        } else {
            null
        }
    }
    
    fun remove(context: Context, email: String){
        check(context)
        editor!!.remove(email)
    }

    fun accounts(context: Context): Set<String>{
        check(context)
        val allEntries: Map<String, *> = sharedPreferences!!.all
        return allEntries.keys
    }
}