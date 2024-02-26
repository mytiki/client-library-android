package com.mytiki.publish.client.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import okhttp3.logging.HttpLoggingInterceptor
import org.bouncycastle.cert.jcajce.JcaCertStore
import org.bouncycastle.cms.CMSProcessableByteArray
import org.bouncycastle.cms.CMSSignedDataGenerator
import org.bouncycastle.cms.CMSTypedData
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder
import org.bouncycastle.jcajce.provider.digest.SHA3
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder
import org.bouncycastle.util.Store
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.security.*
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class AuthService {

    val authRepository = AuthRepository()

    /**
     * Authenticates with TIKI and saves the auth and refresh tokens.
     *
     * @param publishingId
     * @param userId
     * @return The authentication token.
     */
    fun authenticate(publishingId: String, userId: String): String{
        return ""
    }

    /**
     * Retrieves the authentication token, refreshing if necessary.
     * @return The authentication token.
     */
    fun token(providerID: String, publicKey: String): CompletableDeferred<String> {
        val token = CompletableDeferred<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()

            val body = FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", "provider:$providerID")
                .add("client_secret", publicKey)
                .add("scope", "account:provider trail publish")
                .add("expires", "600")
                .build();

            val request = Request.Builder()
                .url("https://account.mytiki.com/api/latest/auth/token")
                .addHeader("accept", "application/json")
                .post(body)
                .build()

            val apiResponse = client.newCall(request).execute()
            if (apiResponse.code in 200..299) {
                token.complete(TikiTokenResponse.fromJson(JSONObject(apiResponse.body?.string()!!)).access_token)
            } else token.completeExceptionally(Exception("error on getting token"))
        }
       return token
    }


    fun getKey(): KeyPair? {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            val entry: KeyStore.Entry? = keyStore.getEntry("TikiKeyPair", null)

            if (entry != null) {
                val privateKey = (entry as KeyStore.PrivateKeyEntry).privateKey
                val publicKey = keyStore.getCertificate("TikiKeyPair").publicKey
                return KeyPair(publicKey, privateKey)
            } else {
                val keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore"
                )

                val builder = KeyGenParameterSpec.Builder(
                    "TikiKeyPair",
                    KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                ).setDigests(KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setKeySize(2048)

                keyPairGenerator.initialize(builder.build())
                return keyPairGenerator.generateKeyPair()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun address(keyPair: KeyPair): String? {
        try {
            val publicKeyBytes = keyPair.public.encoded

            val digest = SHA3.Digest256()
            val addressBytes = digest.digest(publicKeyBytes)

            return Base64.UrlSafe.encode(addressBytes).replace("=","")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun signMessage(message: String, privateKey: PrivateKey): ByteArray? {
        val signature = Signature
            .getInstance("SHA256withRSA")
            .apply {
                initSign(privateKey)
                update(message.toByteArray())
            }
        return signature.sign()
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun registerAddress(providerId: String, tikiPublicKey: String, userId: String): CompletableDeferred<RegisterAddressResponse> {
        val registerAddress = CompletableDeferred<RegisterAddressResponse>()
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val accessToken = token(providerId, tikiPublicKey).await()
                val keyPair = getKey()
                val address = keyPair?.let { address(it) }
                val signature = address?.let { signMessage("$userId.$it", keyPair.private)?.let { it1 ->
                    Base64.Default.encode(
                        it1
                    )
                } }
                val pubKey = keyPair?.let { Base64.Default.encode(it.public.encoded) }

                if (!address.isNullOrEmpty() && !pubKey.isNullOrEmpty() && !signature.isNullOrEmpty()) {
                    val client = OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().apply {
                            level = HttpLoggingInterceptor.Level.BODY
                        })
                        .build()

                    val jsonBody = JSONObject()
                        .put("id", userId)
                        .put("address", address)
                        .put("pubKey", pubKey)
                        .put("signature", signature)
                        .toString()
                        .toRequestBody("application/json".toMediaTypeOrNull())

                    val request = Request.Builder()
                        .url("https://account.mytiki.com/api/latest/provider/$providerId/user")
                        .addHeader("accept", "application/json")
                        .addHeader("authorization", "Bearer $accessToken")
                        .post(jsonBody)
                        .build()

                    val apiResponse = client.newCall(request).execute()
                    if (apiResponse.code in 200..299) {
                        val resp = RegisterAddressResponse.fromJson(JSONObject(apiResponse.body?.string()!!))
                        registerAddress.complete(resp)
                    } else registerAddress.completeExceptionally(Exception("error on registerAddress"))
                } else registerAddress.completeExceptionally(Exception("error on registerAddress"))
            }
        } catch (e: Exception) {
            registerAddress.completeExceptionally(Exception("error on registerAddress"))
        }
        return registerAddress
    }

    /**
     * Revokes the authentication token.
     */
    fun revoke(){}

    /**
     * Refreshes the authentication token.
     * @return The updated authentication token.
     */
    fun refresh(context: Context, email: String, clientID: String): CompletableDeferred<AuthToken>{
        val refreshResponse = CompletableDeferred<AuthToken>()
        val authToken = authRepository.get(context, email)
        if (authToken != null){
            CoroutineScope(Dispatchers.IO).launch {
                val client = OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()

                val request = Request.Builder()
                    .url(authToken.provider.refreshTokenEndpoint(authToken.refresh, clientID))
                    .post(EMPTY_REQUEST)
                    .build()
                val apiResponse = client.newCall(request).execute()

                if (apiResponse.code in 200..299) {
                    val json = JSONObject(apiResponse.body?.string()!!)
                    val refreshAuthToken = AuthToken(
                        email,
                        json.getString("access_token"),
                        authToken.refresh,
                        Date(authToken.expiration.time + json.getLong("expires_in")),
                        authToken.provider
                    )
                    authRepository.save(context, refreshAuthToken)
                    refreshResponse.complete(refreshAuthToken)
                } else throw Exception("error on generate refresh token")
            }
            return refreshResponse
        } else throw Exception("unable to retrieve refresh token")
    }
}
