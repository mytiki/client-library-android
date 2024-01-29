package com.mytiki.publish.client.utils.apiService

import com.mytiki.publish.client.auth.AuthToken
import com.mytiki.publish.client.email.EmailProviderEnum
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File


object ApiService{


    fun getEmail(providerEnum: EmailProviderEnum, token: AuthToken): Response {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
        val request = Request.Builder()
            .url(providerEnum.userInfoEndpoint)
            .addHeader("Authorization", "Bearer (${token.auth})")
            .get()
            .build()
        return client.newCall(request).execute()
    }

}