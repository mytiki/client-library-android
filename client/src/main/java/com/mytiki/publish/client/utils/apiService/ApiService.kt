package com.mytiki.publish.client.utils.apiService

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.internal.EMPTY_REQUEST
import okhttp3.logging.HttpLoggingInterceptor
import java.util.UUID


object ApiService{
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    fun get(header: Map<String, String>, endPoint: String, onError: Exception): CompletableDeferred<ResponseBody?> {
        val get = CompletableDeferred<ResponseBody?>()
        CoroutineScope(Dispatchers.IO).launch {
            val request = Request.Builder().apply {
                url(endPoint)
                header.forEach{ (key, value) ->
                    addHeader(key, value)
                }
                get()
            }.build()
            val apiResponse = client.newCall(request).execute()
            if (apiResponse.code in 200..299) {
                get.complete(apiResponse.body)
            } else get.completeExceptionally(onError)
        }
        return get
    }
    fun post(header: Map<String, String>?, endPoint: String, onError: Exception,body: RequestBody? = null): CompletableDeferred<ResponseBody?> {
        val post = CompletableDeferred<ResponseBody?>()
        CoroutineScope(Dispatchers.IO).launch {
            val request = Request.Builder().apply {
                url(endPoint)
                header?.forEach{ (key, value) ->
                    addHeader(key, value)
                }
                post(body ?: EMPTY_REQUEST)
            }.build()
            val apiResponse = client.newCall(request).execute()
            if (apiResponse.code in 200..299) {
                post.complete(apiResponse.body)
            } else post.completeExceptionally(onError)
        }
        return post
    }

}
