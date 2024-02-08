package com.mytiki.publish.client.capture

/**
 * The companyConfig legal information. It is used to setup the terms for the licensed data.
 *
 * @property name the business legal name. e.g.: "Company Inc"
 * @property jurisdiction The jurisdiction in which the business is stabelished. e.g.: "Tennessee, USA"
 * @property privacy The business privacy terms URL. e.g: "https://companyinc.com/privacy"
 * @property terms The user terms and conditions URL. e.g: "https://companyinc.com/terms"
 */
data class Company(
    val name: String,
    val jurisdiction: String,
    val privacy: String,
    val terms: String
)
