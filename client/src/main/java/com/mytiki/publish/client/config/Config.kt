package com.mytiki.publish.client.config

data class Config(
    val providerId: String,
    val publicKey: String,
    val companyName: String,
    val companyJurisdiction: String,
    val tosUrl: String,
    val privacyUrl: String
)
