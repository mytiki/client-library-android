/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.apps_receipt_rewards.retailer

data class RetailerOffer(
    val accountProvider: AccountProvider,
    val discount: String,
    val offerLink: String
)