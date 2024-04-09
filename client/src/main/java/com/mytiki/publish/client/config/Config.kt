package com.mytiki.publish.client.config

/**
 * Config
 *
 * @property providerId
 * @property publicKey
 * @property companyName
 * @property companyJurisdiction
 * @property tosUrl
 * @property privacyUrl
 */
data class Config(
    val providerId: String,
    val publicKey: String,
    val companyName: String,
    val companyJurisdiction: String,
    val tosUrl: String,
    val privacyUrl: String
)
