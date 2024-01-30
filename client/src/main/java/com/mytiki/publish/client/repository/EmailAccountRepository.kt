package com.mytiki.publish.client.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.mytiki.publish.client.auth.AuthToken

class EmailAccountRepository() {

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
            updateToken(email, token.toString())
        } else {
            editor!!.putString(email, token.toString()).commit()
        }
    }

    private fun updateToken(email: String, token: String){
        editor!!.remove(email)
        editor!!.putString(email, token)
    }

    fun getToken(context: Context, email: String): AuthToken? {
        check(context)
        val token = sharedPreferences!!.getString(email, null)
        return if (token != null){
           AuthToken.fromString(token)
        } else {
            null
        }
    }

    fun getToken(context: Context): List<AuthToken> {
        check(context)
        val token = sharedPreferences!!.all
        val tokenList = mutableListOf<AuthToken>()
        token.forEach{
            if (it.value is String && it.value != null) {
                tokenList.add(AuthToken.fromString(it.value as String))
            }
        }
        return tokenList.toList()
    }

    fun removeToken(context: Context, email: String){
        check(context)
        editor!!.remove(email)
    }
}