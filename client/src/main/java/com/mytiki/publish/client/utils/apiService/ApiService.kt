package com.mytiki.publish.client.utils.apiService

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.internal.EMPTY_REQUEST
import okhttp3.logging.HttpLoggingInterceptor

object ApiService {

  fun get(
      header: Map<String, String>,
      endPoint: String,
      onError: Exception
  ): CompletableDeferred<ResponseBody?> {
    val get = CompletableDeferred<ResponseBody?>()
    CoroutineScope(Dispatchers.IO).launch {
      val client =
          OkHttpClient.Builder()
              .addInterceptor(
                  HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
              .build()
      val request =
          Request.Builder()
              .apply {
                url(endPoint)
                header.forEach { (key, value) -> addHeader(key, value) }
                get()
              }
              .build()
      val apiResponse = client.newCall(request).execute()
      if (apiResponse.code in 200..299) {
        get.complete(apiResponse.body)
      } else get.completeExceptionally(onError)
    }
    return get
  }

  fun post(
      header: Map<String, String>?,
      endPoint: String,
      onError: Exception,
      body: RequestBody? = null
  ): CompletableDeferred<ResponseBody?> {
    val post = CompletableDeferred<ResponseBody?>()
    CoroutineScope(Dispatchers.IO).launch {
      val client =
          OkHttpClient.Builder()
              .addInterceptor(
                  HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
              .build()
      val request =
          Request.Builder()
              .apply {
                url(endPoint)
                header?.forEach { (key, value) -> addHeader(key, value) }
                post(body ?: EMPTY_REQUEST)
              }
              .build()
      val apiResponse = client.newCall(request).execute()
      if (apiResponse.code in 200..299) {
        post.complete(apiResponse.body)
      } else post.completeExceptionally(onError)
    }
    return post
  }
}
