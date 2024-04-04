package com.mytiki.publish.client.auth

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.utils.apiService.ApiService
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.bouncycastle.jcajce.provider.digest.SHA3
import org.json.JSONObject
import java.security.*
import java.util.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Service class for authentication with TIKI.
 */
class AuthService {


    /**
     * Retrieves the authentication token, refreshing if necessary.
     * @param providerID The provider ID.
     * @param publicKey The public key.
     * @return The authentication token.
     */
    fun token(address: String? = null): CompletableDeferred<String> {
        val token = CompletableDeferred<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val body = FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", "provider:${TikiClient.config.providerId}")
                .add("client_secret", TikiClient.config.publicKey)
                .add("scope", "account:provider trail publish")
                .add("expires", "600")
                .build()

            val response = ApiService.post(
                mapOf("accept" to "application/json"),
                "https://account.mytiki.com/api/latest/auth/token",
                body,
                Exception("error on getting token")
            ).await()

            token.complete(TikiTokenResponse.fromJson(JSONObject(response?.string()!!)).access_token)
        }
        return token
    }

    /**
     * Gets the RSA key pair.
     * @return The RSA key pair.
     */
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

    /**
     * Generates the address.
     * @param keyPair The RSA key pair.
     * @return The address.
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun address(keyPair: KeyPair): String? {
        try {
            val publicKeyBytes = keyPair.public.encoded

            val digest = SHA3.Digest256()
            val addressBytes = digest.digest(publicKeyBytes)

            return Base64.UrlSafe.encode(addressBytes).replace("=", "")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Signs the message.
     * @param message The message.
     * @param privateKey The private key.
     * @return The signature.
     */
    fun signMessage(message: String, privateKey: PrivateKey): ByteArray? {
        val signature = Signature
            .getInstance("SHA256withRSA")
            .apply {
                initSign(privateKey)
                update(message.toByteArray())
            }
        return signature.sign()
    }

    /**
     * Registers the address.
     * @param providerID The provider ID.
     * @param publicKey The public key.
     * @param userId The user ID.
     * @return The registration response.
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun registerAddress(): CompletableDeferred<RegisterAddressResponse> {

        val registerAddress = CompletableDeferred<RegisterAddressResponse>()
        try {
            MainScope().async {
                val keyPair = getKey()
                val address = keyPair?.let { address(it) }
                val signature = address?.let { address ->
                    signMessage("${TikiClient.userID}.$address", keyPair.private)?.let { Base64.Default.encode(it) }
                }
                val pubKey = keyPair?.let { Base64.Default.encode(it.public.encoded) }

                if (!address.isNullOrEmpty() && !pubKey.isNullOrEmpty() && !signature.isNullOrEmpty()) {
                    val jsonBody = JSONObject()
                        .put("id", TikiClient.userID)
                        .put("address", address)
                        .put("pubKey", pubKey)
                        .put("signature", signature)
                        .toString()
                        .toRequestBody("application/json".toMediaTypeOrNull())

                    val token = token().await()

                    val response = ApiService.post(
                        mapOf(
                            "Authorization" to "Bearer $token",
                            "accept" to "application/json"
                        ),
                        "https://account.mytiki.com/api/latest/provider/${TikiClient.config.providerId}/user",
                        jsonBody,
                        Exception("error on registerAddress")
                    ).await()
                    registerAddress.complete(RegisterAddressResponse.fromJson(JSONObject(response?.string()!!)))
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
    fun revoke() {
        // Placeholder method, to be implemented
    }
}
