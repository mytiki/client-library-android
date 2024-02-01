package com.mytiki.publish.client.email

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.mytiki.publish.client.auth.AuthToken
import net.openid.appauth.AuthState

class EmailRepository() {

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
        val tokenJson = token.toString()
        editor!!.putString(email, tokenJson).commit()
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