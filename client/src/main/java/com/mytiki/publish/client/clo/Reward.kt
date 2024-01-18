package com.mytiki.publish.client.clo

/**
 * Earned reward data
 *
 * @property merchantId
 * @property merchantName
 * @property commission
 */
data class Reward(
    val merchantId: String,
    val merchantName: String,
    val commission: Double
)
