package com.mytiki.publish.client.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.mytiki.publish.client.utils.apiService.ApiService
import kotlinx.coroutines.*
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

/**
 * Service class for authentication with TIKI.
 */
class AuthService {

    val authRepository = AuthRepository()

    /**
     * Authenticates with TIKI and saves the auth and refresh tokens.
     * @param publishingId The publishing ID.
     * @param userId The user ID.
     * @return The authentication token.
     */
    fun authenticate(publishingId: String, userId: String): String {
        // Placeholder method, to be implemented
        return ""
    }

    /**
     * Retrieves the authentication token, refreshing if necessary.
     * @param providerID The provider ID.
     * @param publicKey The public key.
     * @return The authentication token.
     */
    fun token(providerID: String, publicKey: String): CompletableDeferred<String> {
        val token = CompletableDeferred<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val body = FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", "provider:$providerID")
                .add("client_secret", publicKey)
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
    fun registerAddress(providerID: String, publicKey: String, userId: String): CompletableDeferred<RegisterAddressResponse> {
        val registerAddress = CompletableDeferred<RegisterAddressResponse>()
        try {
            MainScope().async {
                val keyPair = getKey()
                val address = keyPair?.let { address(it) }
                val signature = address?.let { address ->
                    signMessage("$userId.$address", keyPair.private)?.let { Base64.Default.encode(it) }
                }
                val pubKey = keyPair?.let { Base64.Default.encode(it.public.encoded) }

                if (!address.isNullOrEmpty() && !pubKey.isNullOrEmpty() && !signature.isNullOrEmpty()) {
                    val jsonBody = JSONObject()
                        .put("id", userId)
                        .put("address", address)
                        .put("pubKey", pubKey)
                        .put("signature", signature)
                        .toString()
                        .toRequestBody("application/json".toMediaTypeOrNull())

                    val token = token(providerID, publicKey).await()

                    val response = ApiService.post(
                        mapOf(
                            "Authorization" to "Bearer $token",
                            "accept" to "application/json"
                        ),
                        "https://account.mytiki.com/api/latest/provider/$providerID/user",
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

    /**
     * Refreshes the authentication token.
     * @param context The context.
     * @param email The email.
     * @param clientID The client ID.
     * @return The updated authentication token.
     */
    fun refresh(context: Context, email: String, clientID: String): CompletableDeferred<AuthToken> {
        val refreshResponse = CompletableDeferred<AuthToken>()
        val authToken = authRepository.get(context, email)
        if (authToken != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = ApiService.post(
                    null,
                    authToken.provider.refreshTokenEndpoint(authToken.refresh, clientID),
                    EMPTY_REQUEST,
                    Exception("error on generate refresh token")
                ).await()

                val json = JSONObject(response?.string()!!)
                val refreshAuthToken = AuthToken(
                    email,
                    json.getString("access_token"),
                    authToken.refresh,
                    Date(authToken.expiration.time + json.getLong("expires_in")),
                    authToken.provider
                )
                authRepository.save(context, refreshAuthToken)
                refreshResponse.complete(refreshAuthToken)
            }
            return refreshResponse
        } else throw Exception("User not logged")
    }
}
