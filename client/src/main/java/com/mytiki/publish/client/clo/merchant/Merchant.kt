/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.clo.merchant

data class Merchant(
    val accountProvider: MerchantEnum,
    val discount: String,
    val offerLink: String
)