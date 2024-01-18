package com.mytiki.publish.client.clo

/**
 * Offer data
 *
 * @property bannerUrl offer banner image url
 * @property description the description of the offer
 * @property clickUrl The url of the offer
 * @property commissionType [ComissionEnum]
 * @property totalCommission Comission in cents
 */
data class Offer(
    val bannerUrl: String,
    val description: String,
    val clickUrl: String,
    val commissionType: ComissionEnum,
    val totalCommission: Double,

)
